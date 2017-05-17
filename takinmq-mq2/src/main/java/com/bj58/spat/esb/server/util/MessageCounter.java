package com.bj58.spat.esb.server.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.bj58.spat.esb.server.config.ServerConfigBase;
import com.bj58.spat.esb.server.store.telnet.IMessageCounterDao;
import com.bj58.spat.esb.server.store.telnet.MessageCounterDaoMongo;
import com.bj58.spat.esb.server.store.telnet.MessageCounterEntity;

public class MessageCounter {

	public static enum CounterType {
		IN, OUT, PERSIST
	}

	class CounterIncreaser {
		private AtomicInteger in = new AtomicInteger(0);
		private AtomicInteger out = new AtomicInteger(0);
		private AtomicInteger persist = new AtomicInteger(0);

		public int incIn() {
			return in.incrementAndGet();
		}

		public int incOut() {
			return out.incrementAndGet();
		}

		public int incPersist() {
			return persist.incrementAndGet();
		}

		public int decIn() {
			return in.decrementAndGet();
		}

		public int decOut() {
			return out.decrementAndGet();
		}

		public int decPersist() {
			return persist.decrementAndGet();
		}

		public int getIn() {
			return in.intValue();
		}

		public int getOut() {
			return out.intValue();
		}

		public int getPersist() {
			return persist.intValue();
		}

		public CounterIncreaser() {

		}

		public CounterIncreaser(MessageCounterEntity messageCounterEntity) {
			this.in = new AtomicInteger(messageCounterEntity.getIn());
			this.out = new AtomicInteger(messageCounterEntity.getOut());
			this.persist = new AtomicInteger(messageCounterEntity.getPersist());
		}
	}

	private MessageCounter() {

	}

	private static MessageCounter messageCounter = new MessageCounter();

	private static final Map<Integer, CounterIncreaser> subCounterMap = new ConcurrentHashMap<Integer, MessageCounter.CounterIncreaser>();

	private static final IMessageCounterDao messageCounterDao = MessageCounterDaoMongo.getInstance();

	private static final long ip = IpUtil.ip2long(ServerConfigBase.getServerListenIP());

	static {
		List<MessageCounterEntity> listInDb = messageCounterDao.load(ip);

		for (MessageCounterEntity messageCounterEntity : listInDb) {
			subCounterMap.put(new Integer(messageCounterEntity.getSubject()),
					messageCounter.new CounterIncreaser(messageCounterEntity));
		}

		// 定时更新数据库
		Thread saver = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(60 * 1000);

						for (Entry<Integer, CounterIncreaser> entry : subCounterMap.entrySet()) {
							CounterIncreaser increaser = entry.getValue();
							boolean isnew = false;
							MessageCounterEntity entity = messageCounterDao.get(entry.getKey() + "_" + ip);

							if (entity == null) {
								entity = new MessageCounterEntity();
								entity.setId(entry.getKey() + "_" + ip);
								entity.setIp(ip);
								entity.setSubject(entry.getKey());
								isnew = true;
							}
							entity.setIn(increaser.getIn());
							entity.setOut(increaser.getOut());
							entity.setPersist(increaser.getPersist());

							if (isnew) {
								messageCounterDao.insert(entity);
							} else {
								messageCounterDao.update(entity);
							}
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		saver.setName("messageCounterSaver");
		saver.setDaemon(true);

		saver.start();
	}

	public static void increase(Integer subject, CounterType type) {
		if (subCounterMap.get(subject) == null) {
			subCounterMap.put(subject, messageCounter.new CounterIncreaser());
		}

		CounterIncreaser tempincreaser = subCounterMap.get(subject);

		switch (type) {
			case IN:
				tempincreaser.incIn();
				break;
			case OUT:
				tempincreaser.incOut();
				break;
			case PERSIST:
				tempincreaser.incPersist();
				break;
		}
	}

	public static void decrease(Integer subject, CounterType type) {
		if (subCounterMap.get(subject) == null) {
			subCounterMap.put(subject, messageCounter.new CounterIncreaser());
		}

		CounterIncreaser tempincreaser = subCounterMap.get(subject);

		switch (type) {
			case IN:
				tempincreaser.decIn();
				break;
			case OUT:
				tempincreaser.decOut();
				break;
			case PERSIST:
				tempincreaser.decPersist();
				break;
		}
	}

	public static void clean(Integer subject) {
		if (subCounterMap.get(subject) != null) {
			subCounterMap.put(subject, messageCounter.new CounterIncreaser());
		}
	}

	// 返回的数组的顺序: in ,out , persist
	public static int[] getCount(Integer subjectName) {
		CounterIncreaser counter = subCounterMap.get(subjectName);
		if (counter == null) {
			return new int[] { 0, 0, 0 };
		}
		return new int[] { counter.getIn(), counter.getOut(),
				counter.getPersist() };
	}

}
