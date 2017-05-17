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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 完成基于文件的先进先出的读写功能
 * 
 * @author sunli
 * @date 2010-8-13
 * @version $Id: FSQueue.java 2 2011-07-31 12:25:36Z sunli1223@gmail.com $
 */
public class FSQueue {
	private static final Log log = LogFactory.getLog(FSQueue.class);
	public static final String filePrefix = "fqueue";
	private int fileLimitLength = 1024 * 1024 * 100;
	public static final String dbName = "icqueue.db";
	public static final String fileSeparator = System.getProperty("file.separator");
	public String path = null;
	private final Executor executor = Executors.newSingleThreadExecutor();
	/**
	 * 文件操作实例
	 */
	private LogIndex db = null;
	private LogEntity writerHandle = null;
	private LogEntity readerHandle = null;
	/**
	 * 文件操作位置信息
	 */
	private int readerIndex = -1;
	private int writerIndex = -1;
	private boolean isChangeFile = false;
	
	FileRunner deleteFileRunner = null;
	private Lock lock = new ReentrantReadWriteLock().writeLock();

	public FSQueue(String path) throws Exception {
		this(path, 1024 * 1024 * 40);
	}
	
	public LogEntity FSGetReadHandle() {
		return this.readerHandle;
	}

	public LogEntity FSGetWriterHandle() {
		return this.writerHandle;
	}
	/**
	 * 在指定的目录中，以fileLimitLength为单个数据文件的最大大小限制初始化队列存储
	 * 
	 * @param dir
	 *            队列数据存储的路径
	 * @param fileLimitLength
	 *            单个数据文件的大小，不能超过2G
	 * @throws Exception
	 */
	public FSQueue(String dir, int fileLimitLength) throws Exception {
		this.fileLimitLength = fileLimitLength;
		File fileDir = new File(dir);
		if (fileDir.exists() == false && fileDir.isDirectory() == false) {
			if (fileDir.mkdirs() == false) {
				throw new IOException("create dir error");
			}
		}
		path = fileDir.getAbsolutePath();
		// 打开db, 元数据
		db = new LogIndex(path + fileSeparator + dbName);  
		writerIndex = db.getWriterIndex();
		readerIndex = db.getReaderIndex();
		writerHandle = createLogEntity(path + fileSeparator + filePrefix + "data_" + writerIndex + ".idb", db,
				writerIndex);
		if (readerIndex == writerIndex) {
			readerHandle = writerHandle;
		} else {
			File tempFile;
			while(readerIndex < writerIndex) {
				tempFile = new File(path + fileSeparator + filePrefix + "data_" + readerIndex + ".idb");
				if (tempFile.exists() == false) {
					readerIndex++;
					db.putReaderIndex(readerIndex);
					db.putReaderPosition(20);
				} else {
					break;
				}
			}
			readerHandle = createLogEntity(path + fileSeparator + filePrefix + "data_" + readerIndex + ".idb", db,
					readerIndex);
		}
		deleteFileRunner = new FileRunner(path + fileSeparator + filePrefix + "data_", fileLimitLength);
		executor.execute(deleteFileRunner);
	}

	/**
	 * 创建或者获取一个数据读写实例
	 * 
	 * @param dbpath
	 * @param db
	 * @param fileNumber
	 * @return
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public LogEntity createLogEntity(String dbpath, LogIndex db, int fileNumber) throws IOException,
			FileFormatException {
		return new LogEntity(dbpath, db, fileNumber, this.fileLimitLength);
	}

	/**
	 * 一个文件的数据写入达到fileLimitLength的时候，滚动到下一个文件实例
	 * 
	 * @throws IOException
	 * @throws FileFormatException
	 */
	private void rotateNextLogWriter() throws IOException, FileFormatException {
		writerIndex = writerIndex + 1;
		writerHandle.putNextFile(writerIndex);
		if (readerHandle != writerHandle) {
			writerHandle.close();
		}
		db.putWriterIndex(writerIndex);
		writerHandle = createLogEntity(path + fileSeparator + filePrefix + "data_" + writerIndex + ".idb", db,
				writerIndex);
	}

	/**
	 * 向队列存储添加一个字符串
	 * 
	 * @param message
	 *            message
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public void add(String message) throws IOException, FileFormatException {
		add(message.getBytes());
	}

	/**
	 * 向队列存储添加一个byte数组
	 * 
	 * @param message
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public void add(byte[] message) throws IOException, FileFormatException {
		lock.lock();
		try {
			short status = writerHandle.write(message);
			if (status == LogEntity.WRITEFULL) {
				rotateNextLogWriter();
				status = writerHandle.write(message);
			}
			if (status == LogEntity.WRITESUCCESS) {
				db.incrementSize();
			}
		} finally{
			lock.unlock();
		}
	}
	/**
	 * 从队列存储中取出最先入队的数据，并移除它
	 * @return
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public byte[] readNextAndRemove() throws IOException, FileFormatException {
		byte[] b = null;
		if (isChangeFile) {
			deleteFileRunner.transCorrect = true;
		}
		lock.lock();
		try {
			try {
				b = readerHandle.readNextAndRemove();
			} catch (FileEOFException e) {
				int deleteNum = readerHandle.getCurrentFileNumber();
				int nextfile = readerHandle.getNextFile();
				readerHandle.close();
				if (readerHandle.getCurrentFileNumber() == writerHandle
						.getCurrentFileNumber()) {
					System.out.println("delete write");
					writerHandle.close();
				}
				deleteFileRunner.addDeleteFile(path + fileSeparator
						+ filePrefix + "data_" + deleteNum + ".idb");
				// 更新下一次读取的位置和索引
				db.putReaderPosition(LogEntity.messageStartPosition);
				db.putReaderIndex(nextfile);
				if (writerHandle.getCurrentFileNumber() == nextfile) {
					readerHandle = writerHandle;
				} else {
					readerHandle = createLogEntity(path + fileSeparator
							+ filePrefix + "data_" + nextfile + ".idb", db,
							nextfile);
				}
				try {
					b = readerHandle.readNextAndRemove();
				} catch (FileEOFException e1) {
					log.error(
							"read new log file FileEOFException error occurred",
							e1);
				}
				isChangeFile = true;
			}
			if (b != null) {
				db.decrementSize();
			}
		} finally {
			lock.unlock();
		}
		return b;
	}
	
	public byte[][] readNextAndRemove(int n) throws IOException, FileFormatException {
		byte[][] b = new byte[n][];
		int readnfinished = 0;
		int count = 0;
		if (n <= 0) {
			System.out.println("invalid parameter n: " + n);
			return null;
		}
		lock.lock();
		if (isChangeFile) {
			deleteFileRunner.transCorrect = true;
		}
		try {
			o: while (n > 0) {
				try {
					int length = 0;
					int readerPos = readerHandle.getReaderPosition();
					for (int i = 0; i < n; i++) {
						if (readerHandle.getEndPosition() != -1
								&& readerHandle.getReaderPosition() >= readerHandle
										.getEndPosition()) {
							throw new FileEOFException("file eof");
						}
						if (readerHandle.getReaderPosition() >= readerHandle
								.getWriterPostion()) {
							System.out.println(readerHandle.getReaderPosition()
									+ "   " + readerHandle.getReaderPosition());
							readerHandle.putReaderPosition(readerHandle
									.getWriterPostion());
							break o;
						}
						readerHandle.mappedByteBuffer.position(readerHandle
								.getReaderPosition());
						length = readerHandle.mappedByteBuffer.getInt();
						byte[] msg = new byte[length];
						readerPos += length + 4;
						readerHandle.setReaderPosition(readerPos);
						readerHandle.mappedByteBuffer.get(msg);
						b[readnfinished] = msg;
						count++;
						readnfinished++;
					}
					readerHandle.putReaderPosition(readerHandle
							.getReaderPosition());
					n = n - readnfinished;
				} catch (FileEOFException e) {
					n = n - readnfinished;
					int deleteNum = readerHandle.getCurrentFileNumber();
					int nextfile = readerHandle.getNextFile();
					readerHandle.close();
					if (readerHandle.getCurrentFileNumber() == writerHandle
							.getCurrentFileNumber()) {
						System.out.println("delete write");
						writerHandle.close();
					}
					deleteFileRunner.addDeleteFile(path + fileSeparator
							+ filePrefix + "data_" + deleteNum + ".idb");
					// 更新下一次读取的位置和索引
					db.putReaderPosition(LogEntity.messageStartPosition);
					db.putReaderIndex(nextfile);
					if (writerHandle.getCurrentFileNumber() == nextfile) {
						readerHandle = writerHandle;
					} else {
						readerHandle = createLogEntity(path + fileSeparator
								+ filePrefix + "data_" + nextfile + ".idb", db,
								nextfile);
					}
					isChangeFile = true;
				}
			}

			for (int i = 0; i < count; i++) {
				db.decrementSize();
			}
		} finally {
			lock.unlock();
		}
		byte[][] b2 = new byte[readnfinished][];
		System.arraycopy(b, 0, b2, 0, readnfinished);
		return b2;
	}

	public void close() {
		readerHandle.close();
		writerHandle.close();
		db.close();
	}

	public int getQueuSize() {
		return db.getSize();
	}
	
	// offset: bytes failed to send
	public void readGoBack(int backLength) throws IOException, FileFormatException {
		int currFileNumber = readerHandle.getCurrentFileNumber();
		int readposition = db.getReaderPosition();
		lock.lock();
		if (backLength > (readposition - 20)) {
			currFileNumber = currFileNumber - 1;
			if (currFileNumber > 0) {
				deleteFileRunner.removeDeleteFile(path + fileSeparator + filePrefix + "data_" + currFileNumber + ".idb");
				db.recalculateSize(readerHandle.mappedByteBuffer);
				readerHandle.putReaderPos(20);
				//改变readerHandle为刚才close的文件
				readerHandle = createLogEntity(path + fileSeparator + filePrefix + "data_" + currFileNumber + ".idb", db,
						currFileNumber);
				db.putReaderPosition(readerHandle.getEndPosition());
				backLength = backLength - readposition + 20;
			}
		}
		db.goBackPosition(backLength, readerHandle.mappedByteBuffer);
		readerHandle.putReaderPos(db.getReaderPosition());
		db.putReaderPosition(db.getReaderPosition());
		db.putReaderIndex(currFileNumber);
		deleteFileRunner.transCorrect = true;
		lock.unlock();
	}
	
	public void syncDisk() {
		writerHandle.ForceSyc();
	}
}
