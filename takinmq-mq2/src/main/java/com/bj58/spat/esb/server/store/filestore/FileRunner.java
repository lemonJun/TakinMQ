/*
 *  Copyright 2011 sunli [sunli1223@gmail.com][weibo.com@sunli1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.bj58.spat.esb.server.store.filestore;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sunli
 * @date 2011-5-18
 * @version $Id: FileRunner.java 74 2012-03-21 13:51:26Z sunli1223@gmail.com $
 */
public class FileRunner implements Runnable {
    private static final Log logger = LogFactory.getLog(FileRunner.class);
    // 删除队列
    private final Queue<String> deleteQueue = new ConcurrentLinkedQueue<String>();
    // 新创建队
    private final Queue<String> createQueue = new ConcurrentLinkedQueue<String>();
    private String baseDir = null;
    private int fileLimitLength = 0;
    public boolean transCorrect = false;

    public void addDeleteFile(String path) {
        deleteQueue.add(path);
    }

    public void addCreateFile(String path) {
        createQueue.add(path);
    }

    public void removeDeleteFile(String path) {
        deleteQueue.remove(path);
    }

    public FileRunner(String baseDir, int fileLimitLength) {
        this.baseDir = baseDir;
        this.fileLimitLength = fileLimitLength;
    }

    @Override
    public void run() {
        String filePath, fileNum;
        while (true) {
            if (transCorrect == false) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                continue;
            }
            filePath = deleteQueue.poll();
            fileNum = createQueue.poll();
            if (filePath == null && fileNum == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                continue;
            }
            if (filePath != null) {
                File delFile = new File(filePath);

                if (delFile.delete()) {
                    System.out.println("delete" + " " + filePath);
                } else {
                    System.out.println("delete failed" + filePath);
                }
            }

            if (fileNum != null) {
                filePath = baseDir + fileNum + ".idb";
                try {
                    create(filePath);
                } catch (IOException e) {
                    logger.error("预创建数据文件失败");
                }
            }
            transCorrect = false;
        }

    }

    private boolean create(String path) throws IOException {
        File file = new File(path);
        if (file.exists() == false) {
            if (file.createNewFile() == false) {
                return false;
            }
            RandomAccessFile raFile = new RandomAccessFile(file, "rwd");
            FileChannel fc = raFile.getChannel();
            MappedByteBuffer mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0, this.fileLimitLength);
            mappedByteBuffer.put(LogEntity.MAGIC.getBytes());
            mappedByteBuffer.putInt(1);// 8 version
            mappedByteBuffer.putInt(-1);// 12next fileindex
            mappedByteBuffer.putInt(-2);// 16 endpoint
            mappedByteBuffer.force();
            //          MappedByteBufferUtil.clean(mappedByteBuffer);
            fc.close();
            raFile.close();
            return true;
        } else {
            return false;
        }
    }
}
