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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.takin.emmet.util.Closer;
import com.takin.emmet.util.Tuple;
import com.takin.mq.broker.BrokerConfig;
import com.takin.mq.broker.ServerRegister;
import com.takin.mq.broker.TopicCommand;
import com.takin.mq.utils.IteratorTemplate;
import com.takin.mq.utils.Scheduler;
import com.takin.mq.utils.Utils;
import com.takin.rpc.server.GuiceDI;

/**
 * 主要的日志文件管理类
 *
 * @author WangYazhou
 * @date  2017年4月20日 下午4:10:51
 * @see
 */
@Singleton
public class StoreManager implements Closeable {

    private Scheduler scheduler;

    private boolean needRecovery;

    private final Logger logger = LoggerFactory.getLogger(StoreManager.class);

    final int numPartitions;

    final File logDir;

    final int flushInterval;

    private final Object logCreationLock = new Object();

    final Random random = new Random();

    final CountDownLatch startupLatch;

    private final ConcurrentHashMap<String, Map<Integer, FileStore>> topicLogMap = new ConcurrentHashMap<String, Map<Integer, FileStore>>();

    private final Scheduler logFlusherScheduler = new Scheduler(1, "jafka-logflusher-", false);

    private final LinkedBlockingQueue<TopicCommand> topicRegisterTasks = new LinkedBlockingQueue<TopicCommand>();

    private volatile boolean stopTopicRegisterTasks = false;

    final int logRetentionSize;

    private ServerRegister serverRegister;

    private RollingStrategy rollingStategy;

    private BrokerConfig config;

    @Inject
    private StoreManager() {
        config = GuiceDI.getInstance(BrokerConfig.class);
        this.logDir = Utils.getCanonicalFile(new File(config.getLogdirs()));
        this.numPartitions = config.getNumpartitions();
        this.flushInterval = config.getLogflushintervalmessages();
        this.startupLatch = config.isUsezk() ? new CountDownLatch(1) : null;
        this.logRetentionSize = config.getLogsegmentbytes();
    }

    public void load() throws IOException {
        if (this.rollingStategy == null) {
            this.rollingStategy = new FixedSizeRollingStrategy(config.getLogfilesize());
        }
        if (!logDir.exists()) {
            logger.info("No log directory found, creating '" + logDir.getAbsolutePath() + "'");
            logDir.mkdirs();
        }
        if (!logDir.isDirectory() || !logDir.canRead()) {
            throw new IllegalArgumentException(logDir.getAbsolutePath() + " is not a readable log directory.");
        }
        File[] subDirs = logDir.listFiles();
        if (subDirs != null) {
            for (File dir : subDirs) {
                if (!dir.isDirectory()) {//如果不是上目录
                    logger.warn("Skipping unexplainable file '" + dir.getAbsolutePath() + "'--should it be there?");
                } else {
                    logger.info("Loading log from " + dir.getAbsolutePath());
                    final String topicNameAndPartition = dir.getName();
                    if (-1 == topicNameAndPartition.indexOf('-')) {
                        throw new IllegalArgumentException("error topic directory: " + dir.getAbsolutePath());
                    }
                    final Tuple<String, Integer> topicPartion = fileNameToTopicPartition(topicNameAndPartition);
                    final String topic = topicPartion.key;
                    final int partition = topicPartion.value;
                    FileStore log = new FileStore(dir, partition, this.rollingStategy, flushInterval, needRecovery, config.getMaxmessagesize());

                    topicLogMap.putIfAbsent(topic, new HashMap<Integer, FileStore>());
                    Map<Integer, FileStore> parts = topicLogMap.get(topic);
                    parts.put(partition, log);
                }
            }
        }

        //        dodemon();
        if (config.isUsezk()) {
            this.serverRegister = new ServerRegister();
            serverRegister.startup();
            TopicRegisterTask task = new TopicRegisterTask();
            task.setName("jafka.topicregister");
            task.setDaemon(true);
            task.start();
        }
    }

    /**
     * Register this broker in ZK for the first time.
     */
    public void startup() {
        if (config.isUsezk()) {
            serverRegister.registerBrokerInZk();
            for (String topic : getAllTopics()) {
                serverRegister.processTask(new TopicCommand(TopicCommand.TaskType.CREATE, topic));
            }
            startupLatch.countDown();
        }
        logFlusherScheduler.scheduleWithRate(new Runnable() {
            public void run() {
                flushAllLogs(true);
            }
        }, config.getLogflushintervalms(), config.getLogflushintervalms());
    }

    public int choosePartition(String topic) {
        return random.nextInt(getPartitionNum(topic));
    }

    /**
     * Create the log if it does not exist or return back exist log
     *
     * @param topic     the topic name
     * @param partition the partition id
     * @return read or create a log
     * @throws IOException any IOException
     */
    public IStore getOrCreateLog(String topic, int partition) throws IOException {
        awaitStartup();

        final int configPartitionNumber = getPartitionNum(topic);
        if (partition >= configPartitionNumber) {
            throw new IOException("partition is bigger than the number of configuration: " + configPartitionNumber);
        }

        boolean hasNewTopic = false;
        Map<Integer, FileStore> parts = topicLogMap.get(topic);
        if (parts == null) {
            Map<Integer, FileStore> found = topicLogMap.putIfAbsent(topic, new HashMap<Integer, FileStore>());
            if (found == null) {
                hasNewTopic = true;
            }
            parts = topicLogMap.get(topic);
        }
        //
        FileStore log = parts.get(partition);
        if (log == null) {
            log = createLogFile(topic, partition);
            FileStore found = parts.putIfAbsent(partition, log);
            if (found != null) {
                Closer.closeQuietly(log, logger);
                log = found;
            } else {
                logger.info(format("Created log for [%s-%d], now create other logs if necessary", topic, partition));
                final int configPartitions = getPartitionNum(topic);
                for (int i = 0; i < configPartitions; i++) {
                    getOrCreateLog(topic, i);
                }
            }
        }
        if (hasNewTopic && config.isUsezk()) {
            topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.CREATE, topic));
        }
        return log;
    }

    /**
     * create logs with given partition number
     *
     * @param topic        the topic name
     * @param partitions   partition number
     * @param forceEnlarge enlarge the partition number of log if smaller than runtime
     * @return the partition number of the log after enlarging
     */
    public int createLogs(String topic, final int partitions, final boolean forceEnlarge) {
        validate(topic);
        synchronized (logCreationLock) {
            final int configPartitions = getPartitionNum(topic);
            if (configPartitions >= partitions || !forceEnlarge) {
                return configPartitions;
            }
            if (config.isUsezk()) {
                if (topicLogMap.get(topic) != null) {//created already
                    topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.ENLARGE, topic));
                } else {
                    topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.CREATE, topic));
                }
            }
            return partitions;
        }
    }

    private FileStore createLogFile(String topic, int partition) throws IOException {
        synchronized (logCreationLock) {
            File d = new File(logDir, topic + "-" + partition);
            d.mkdirs();
            return new FileStore(d, partition, this.rollingStategy, flushInterval, false, config.getMaxmessagesize());
        }
    }

    private void awaitStartup() {
        if (config.isUsezk()) {
            try {
                startupLatch.await();
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    private int getPartitionNum(String topic) {
        return this.numPartitions;
    }

    /**
     * flush all messages to disk
     *
     * @param force flush anyway(ignore flush interval)
     */
    public void flushAllLogs(final boolean force) {
        Iterator<FileStore> iter = getLogIterator();
        while (iter.hasNext()) {
            FileStore log = iter.next();
            try {
                boolean needFlush = force;
                if (!needFlush) {
                    long timeSinceLastFlush = System.currentTimeMillis() - log.getLastFlushedTime();
                    int logFlushInterval = config.getLogflushintervalms();
                    final String flushLogFormat = "[%s] flush interval %d, last flushed %d, need flush? %s";
                    needFlush = timeSinceLastFlush >= logFlushInterval;
                    logger.trace(String.format(flushLogFormat, log.getTopicName(), logFlushInterval, log.getLastFlushedTime(), needFlush));
                }
                if (needFlush) {
                    log.flush();
                }
            } catch (IOException ioe) {
                logger.error("Error flushing topic " + log.getTopicName(), ioe);
                logger.error("Halting due to unrecoverable I/O error while flushing logs: " + ioe.getMessage(), ioe);
                Runtime.getRuntime().halt(1);
            } catch (Exception e) {
                logger.error("Error flushing topic " + log.getTopicName(), e);
            }
        }
    }

    //
    private Collection<String> getAllTopics() {
        return topicLogMap.keySet();
    }

    private Iterator<FileStore> getLogIterator() {
        return new IteratorTemplate<FileStore>() {
            final Iterator<Map<Integer, FileStore>> iterator = topicLogMap.values().iterator();

            Iterator<FileStore> logIter;

            @Override
            protected FileStore makeNext() {
                while (true) {
                    if (logIter != null && logIter.hasNext()) {
                        return logIter.next();
                    }
                    if (!iterator.hasNext()) {
                        return allDone();
                    }
                    logIter = iterator.next().values().iterator();
                }
            }
        };
    }

    private static final String illegalChars = "/" + '\u0000' + '\u0001' + "-" + '\u001F' + '\u007F' + "-" + '\u009F' + '\uD800' + "-" + '\uF8FF' + '\uFFF0' + "-" + '\uFFFF';
    private static final Pattern p = Pattern.compile("(^\\.{1,2}$)|[" + illegalChars + "]");

    public static void validate(String topic) {
        if (topic.length() == 0) {
            throw new IllegalArgumentException("topic name is emtpy");
        }
        if (topic.length() > 255) {
            throw new IllegalArgumentException("topic name is too long");
        }
        if (p.matcher(topic).find()) {
            throw new IllegalArgumentException("topic name [" + topic + "] is illegal");
        }
    }

    /**---------------以下主要为维护工作----------------*/

    /**
     * delete topic who is never used
     * <p>
     * This will delete all log files and remove node data from zookeeper
     * </p>
     *
     * @param topic topic name
     * @param password auth password
     * @return number of deleted partitions or -1 if authentication failed
     */
    public int deleteLogs(String topic, String password) {
        int value = 0;
        synchronized (logCreationLock) {
            Map<Integer, FileStore> parts = topicLogMap.remove(topic);
            if (parts != null) {
                List<FileStore> deleteLogs = new ArrayList<FileStore>(parts.values());
                for (FileStore log : deleteLogs) {
                    log.delete();
                    value++;
                }
            }
            if (config.isUsezk()) {
                topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.DELETE, topic));
            }
        }
        return value;
    }

    /**
     * Runs through the log removing segments older than a certain age
     *
     * @throws IOException
     */
    private void cleanupLogs() throws IOException {
        logger.trace("Beginning log cleanup...");
        int total = 0;
        Iterator<FileStore> iter = getLogIterator();
        long startMs = System.currentTimeMillis();
        while (iter.hasNext()) {
            FileStore log = iter.next();
            total += cleanupExpiredSegments(log) + cleanupSegmentsToMaintainSize(log);
        }
        if (total > 0) {
            logger.warn("Log cleanup completed. " + total + " files deleted in " + (System.currentTimeMillis() - startMs) / 1000 + " seconds");
        } else {
            logger.trace("Log cleanup completed. " + total + " files deleted in " + (System.currentTimeMillis() - startMs) / 1000 + " seconds");
        }
    }

    /**
     * Runs through the log removing segments until the size of the log is at least
     * logRetentionSize bytes in size
     * 
     * @throws IOException
     */
    private int cleanupSegmentsToMaintainSize(final FileStore log) throws IOException {
        if (logRetentionSize < 0 || log.size() < logRetentionSize)
            return 0;

        List<Segment> toBeDeleted = log.markDeletedWhile(new SegmentFilter() {

            long diff = log.size() - logRetentionSize;

            public boolean filter(Segment segment) {
                diff -= segment.size();
                return diff >= 0;
            }
        });
        return deleteSegments(log, toBeDeleted);
    }

    /**
     * 清除过期的段
     * 应该是一个TOPIC一个配置的 
     * @param log
     * @return
     * @throws IOException
     */
    private int cleanupExpiredSegments(FileStore log) throws IOException {
        final long startMs = System.currentTimeMillis();
        String topic = fileNameToTopicPartition(log.dir.getName()).key;
        long logCleanupThresholdMS = config.getLogretentionhours();
        final long expiredThrshold = logCleanupThresholdMS;
        List<Segment> toBeDeleted = log.markDeletedWhile(new SegmentFilter() {

            public boolean filter(Segment segment) {
                //check file which has not been modified in expiredThrshold millionseconds
                return startMs - segment.getFile().lastModified() > expiredThrshold;
            }
        });
        return deleteSegments(log, toBeDeleted);
    }

    /**
     * Attemps to delete all provided segments from a log and returns how many it was able to
     */
    private int deleteSegments(FileStore log, List<Segment> segments) {
        int total = 0;
        for (Segment segment : segments) {
            boolean deleted = false;
            try {
                try {
                    segment.getFileMessage().close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
                if (!segment.getFile().delete()) {
                    deleted = true;
                } else {
                    total += 1;
                }
            } finally {
                logger.warn(String.format("DELETE_LOG[%s] %s => %s", log.name, segment.getFile().getAbsolutePath(), deleted));
            }
        }
        return total;
    }

    private void dodemon() {
        if (this.scheduler != null) {
            logger.debug("starting log cleaner every " + config.getLogCleanupIntervalms() + " ms");
            this.scheduler.scheduleWithRate(new Runnable() {
                public void run() {
                    try {
                        cleanupLogs();
                    } catch (IOException e) {
                        logger.error("cleanup log failed.", e);
                    }
                }
            }, 60 * 1000, config.getLogCleanupIntervalms());
        }
    }

    private void registeredTaskLooply() {
        while (!stopTopicRegisterTasks) {
            try {
                TopicCommand task = topicRegisterTasks.take();
                if (task.type == TopicCommand.TaskType.SHUTDOWN)
                    break;
                serverRegister.processTask(task);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.debug("stop topic register task");
    }

    class TopicRegisterTask extends Thread {
        @Override
        public void run() {
            registeredTaskLooply();
        }
    }

    public void close() {
        logFlusherScheduler.shutdown();
        Iterator<FileStore> iter = getLogIterator();
        while (iter.hasNext()) {
            Closer.closeQuietly(iter.next(), logger);
        }
        if (config.isUsezk()) {
            stopTopicRegisterTasks = true;
            //wake up again and again
            topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.SHUTDOWN, null));
            topicRegisterTasks.add(new TopicCommand(TopicCommand.TaskType.SHUTDOWN, null));
            Closer.closeQuietly(serverRegister);
        }
    }

    private Tuple<String, Integer> fileNameToTopicPartition(String topicPartition) {
        int index = topicPartition.lastIndexOf('-');
        return new Tuple<String, Integer>(topicPartition.substring(0, index), Integer.valueOf(topicPartition.substring(index + 1)));
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setNeedRecovery(boolean needRecovery) {
        this.needRecovery = needRecovery;
    }

    public void setRollingStategy(RollingStrategy rollingStategy) {
        this.rollingStategy = rollingStategy;
    }

}
