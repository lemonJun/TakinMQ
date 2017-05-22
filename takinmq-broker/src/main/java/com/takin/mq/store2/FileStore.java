/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.takin.mq.store2;

import static java.lang.String.format;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mq.common.OffsetOutOfRangeException;
import com.takin.mq.message.FileMessage;
import com.takin.mq.message.Message;
import com.takin.mq.message.MessageAndOffset;
import com.takin.mq.utils.Range;
import com.takin.mq.utils.Utils;

/**
 *
 * 
 * @since 1.0
 */
public class FileStore implements IStore {

    private final Logger logger = LoggerFactory.getLogger(FileStore.class);

    private static final String FileSuffix = ".queue";

    public final File dir;

    private final RollingStrategy rollingStategy;

    final int flushInterval;
    final boolean needRecovery;

    private final Object lock = new Object();

    private final AtomicInteger unflushed = new AtomicInteger(0);

    private final AtomicLong lastflushedTime = new AtomicLong(System.currentTimeMillis());

    public final String name;

    private final SegmentList segments;

    public final int partition;
    private final int maxMessageSize;

    public FileStore(File dir, int partition, RollingStrategy rollingStategy, int flushInterval, boolean needRecovery, int maxMessageSize) throws IOException {
        this.dir = dir;
        this.partition = partition;
        this.rollingStategy = rollingStategy;
        this.flushInterval = flushInterval;
        this.needRecovery = needRecovery;
        this.maxMessageSize = maxMessageSize;
        this.name = dir.getName();
        segments = loadSegments();
    }

    private SegmentList loadSegments() throws IOException {
        List<LogSegment> accum = new ArrayList<LogSegment>();
        File[] ls = dir.listFiles(new FileFilter() {

            public boolean accept(File f) {
                return f.isFile() && f.getName().endsWith(FileSuffix);
            }
        });
        logger.info("loadSegments files from [" + dir.getAbsolutePath() + "]: " + ls.length);
        int n = 0;
        for (File f : ls) {
            n++;
            String filename = f.getName();
            long start = Long.parseLong(filename.substring(0, filename.length() - FileSuffix.length()));
            final String logFormat = "LOADING_LOG_FILE[%2d], start(offset)=%d, size=%d, path=%s";
            logger.info(String.format(logFormat, n, start, f.length(), f.getAbsolutePath()));
            FileMessage messageSet = new FileMessage(f, false);
            accum.add(new LogSegment(f, messageSet, start));
        }
        if (accum.size() == 0) {
            // no existing segments, create a new mutable segment
            File newFile = new File(dir, FileStore.nameFromOffset(0));
            FileMessage fileMessageSet = new FileMessage(newFile, true);
            accum.add(new LogSegment(newFile, fileMessageSet, 0));
        } else {
            // there is at least one existing segment, validate and recover them/it
            // sort segments into ascending order for fast searching
            Collections.sort(accum);
            validateSegments(accum);
        }
        //
        LogSegment last = accum.remove(accum.size() - 1);
        last.getFileMessage().close();
        logger.info("Loading the last segment " + last.getFile().getAbsolutePath() + " in mutable mode, recovery " + needRecovery);
        LogSegment mutable = new LogSegment(last.getFile(), new FileMessage(last.getFile(), true, new AtomicBoolean(needRecovery)), last.start());
        accum.add(mutable);
        return new SegmentList(name, accum);
    }

    /**
     * Check that the ranges and sizes add up, otherwise we have lost some data somewhere
     */
    private void validateSegments(List<LogSegment> segments) {
        synchronized (lock) {
            for (int i = 0; i < segments.size() - 1; i++) {
                LogSegment curr = segments.get(i);
                LogSegment next = segments.get(i + 1);
                if (curr.start() + curr.size() != next.start()) {
                    throw new IllegalStateException("The following segments don't validate: " + curr.getFile().getAbsolutePath() + ", " + next.getFile().getAbsolutePath());
                }
            }
        }
    }

    public int getNumberOfSegments() {
        return segments.getView().size();
    }

    /**
     * delete all log segments in this topic-partition
     * <p>
     * The log directory will be removed also.
     * @return segment counts deleted
     */
    public int delete() {
        close();
        int count = segments.trunc(Integer.MAX_VALUE).size();
        Utils.deleteDirectory(dir);
        return count;
    }

    public void close() {
        synchronized (lock) {
            for (LogSegment seg : segments.getView()) {
                try {
                    seg.getFileMessage().close();
                } catch (IOException e) {
                    logger.error("close file message set failed", e);
                }
            }
        }
        //unregisterMBean
        //        Utils.unregisterMBean(this.logStats);
    }

    /**
     * 
     * read messages beginning from offset
     * 
     * @param offset next message offset
     * @param length the max package size
     * @return a MessageSet object with length data or empty
     * @see MessageSet#Empty
     * @throws IOException any exception
     */
    @Override
    public MessageAndOffset read(long offset, int length) throws IOException {
        List<LogSegment> views = segments.getView();
        LogSegment found = findRange(views, offset, views.size());
        if (found == null) {
            if (logger.isTraceEnabled()) {
                logger.trace(format("NOT FOUND MessageSet from Log[%s], offset=%d, length=%d", name, offset, length));
            }
            return null;
        }
        return found.getFileMessage().read(offset - found.start(), length);
    }

    //
    public long append(Message message) {
        message.verifyMessageSize(maxMessageSize);
        int numberOfMessages = 1;
        long offset = 0l;
        synchronized (lock) {
            try {
                LogSegment lastSegment = segments.getLastView();
                long[] writtenAndOffset = lastSegment.getFileMessage().append(message);
                logger.info(String.format("[%s,%s] save %d messages, bytesize %d", name, lastSegment.getName(), numberOfMessages, writtenAndOffset[0]));
                maybeFlush(numberOfMessages);
                maybeRoll(lastSegment);
                offset = writtenAndOffset[1];
            } catch (IOException e) {
                logger.error("Halting due to unrecoverable I/O error while handling producer request", e);
                Runtime.getRuntime().halt(1);
            } catch (RuntimeException re) {
                throw re;
            }
        }
        return offset;
    }

    /**
     * check the log whether needing rolling
     * 
     * @param lastSegment the last file segment
     * @throws IOException any file operation exception
     */
    private void maybeRoll(LogSegment lastSegment) throws IOException {
        if (rollingStategy.check(lastSegment)) {
            roll();
        }
    }

    private void roll() throws IOException {
        synchronized (lock) {
            long newOffset = nextAppendOffset();
            File newFile = new File(dir, nameFromOffset(newOffset));
            if (newFile.exists()) {
                logger.warn("newly rolled logsegment " + newFile.getName() + " already exists, deleting it first");
                if (!newFile.delete()) {
                    logger.error("delete exist file(who will be created for rolling over) failed: " + newFile);
                    throw new RuntimeException("delete exist file(who will be created for rolling over) failed: " + newFile);
                }
            }
            logger.info("Rolling log '" + name + "' to " + newFile.getName());
            segments.append(new LogSegment(newFile, new FileMessage(newFile, true), newOffset));
        }
    }

    private long nextAppendOffset() throws IOException {
        flush();
        LogSegment lastView = segments.getLastView();
        return lastView.start() + lastView.size();
    }

    private void maybeFlush(int numberOfMessages) throws IOException {
        if (unflushed.addAndGet(numberOfMessages) >= flushInterval) {
            flush();
        }
    }

    /**
     * Flush this log file to the physical disk
     * 
     * @throws IOException file read error
     */
    public void flush() throws IOException {
        if (unflushed.get() == 0)
            return;

        synchronized (lock) {
            if (logger.isTraceEnabled()) {
                logger.debug("Flushing log '" + name + "' last flushed: " + getLastFlushedTime() + " current time: " + System.currentTimeMillis());
            }
            segments.getLastView().getFileMessage().flush();
            unflushed.set(0);
            lastflushedTime.set(System.currentTimeMillis());
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /**
     * Find a given range object in a list of ranges by a value in that range. Does a binary
     * search over the ranges but instead of checking for equality looks within the range.
     * Takes the array size as an option in case the array grows while searching happens
     * @param <T> Range type
     * @param ranges data list
     * @param value value in the list
     * @param arraySize the max search index of the list
     * @return search result of range
     * TODO: This should move into SegmentList.scala
     */
    public static <T extends Range> T findRange(List<T> ranges, long value, int arraySize) {
        if (ranges.size() < 1)
            return null;
        T first = ranges.get(0);
        T last = ranges.get(arraySize - 1);
        // check out of bounds
        if (value < first.start() || value > last.start() + last.size()) {
            throw new OffsetOutOfRangeException(format("offset %s is out of range (%s, %s)", //
                            value, first.start(), last.start() + last.size()));
        }

        // check at the end
        if (value == last.start() + last.size())
            return null;

        int low = 0;
        int high = arraySize - 1;
        while (low <= high) {
            int mid = (high + low) / 2;
            T found = ranges.get(mid);
            if (found.contains(value)) {
                return found;
            } else if (value < found.start()) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return null;
    }

    public static <T extends Range> T findRange(List<T> ranges, long value) {
        return findRange(ranges, value, ranges.size());
    }

    /**
     * Make log segment file name from offset bytes. All this does is pad out the offset number
     * with zeros so that ls sorts the files numerically
     * @param offset offset value (padding with zero)
     * @return filename with offset
     */
    public static String nameFromOffset(long offset) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(20);
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        return nf.format(offset) + FileStore.FileSuffix;
    }

    public String getTopicName() {
        return this.name.substring(0, name.lastIndexOf("-"));
    }

    public long getLastFlushedTime() {
        return lastflushedTime.get();
    }

    /**
     * all message size in the broker(some old messages has been deleted)
     * 
     * @return effected message size
     */
    public long size() {
        int size = 0;
        for (LogSegment seg : segments.getView()) {
            size += seg.size();
        }
        return size;
    }

    /**
     * get the current high watermark of the log
     * @return the offset of last message
     */
    public long getHighwaterMark() {
        return segments.getLastView().size();
    }

    /**
     * Delete any log segments matching the given predicate function
     * 
     * @throws IOException
     */
    List<LogSegment> markDeletedWhile(LogSegmentFilter filter) throws IOException {
        synchronized (lock) {
            List<LogSegment> view = segments.getView();
            List<LogSegment> deletable = new ArrayList<LogSegment>();
            for (LogSegment seg : view) {
                if (filter.filter(seg)) {
                    deletable.add(seg);
                }
            }
            for (LogSegment seg : deletable) {
                seg.setDeleted(true);
            }
            int numToDelete = deletable.size();
            //
            // if we are deleting everything, create a new empty segment
            if (numToDelete == view.size()) {
                if (view.get(numToDelete - 1).size() > 0) {
                    roll();
                } else {
                    // If the last segment to be deleted is empty and we roll the log, the new segment will have the same
                    // file name. So simply reuse the last segment and reset the modified time.
                    view.get(numToDelete - 1).getFile().setLastModified(System.currentTimeMillis());
                    numToDelete -= 1;
                }
            }
            return segments.trunc(numToDelete);
        }
    }

    @Override
    public String toString() {
        return "Log [dir=" + dir + ", lastflushedTime=" + //
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(lastflushedTime.get())) + "]";
    }

    public long getTotalOffset() {
        LogSegment lastView = segments.getLastView();
        return lastView.start() + lastView.size();
    }

    public long getTotalAddressingOffset() {
        LogSegment lastView = segments.getLastView();
        return lastView.start() + lastView.addressingSize();
    }

    public long getLastSegmentAddressingSize() {
        return segments.getLastView().addressingSize();
    }

    @Override
    public String reallogfile() {
        return dir.getAbsolutePath();
    }

}
