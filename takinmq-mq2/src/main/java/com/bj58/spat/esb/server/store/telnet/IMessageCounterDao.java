package com.bj58.spat.esb.server.store.telnet;

import java.util.List;

public interface IMessageCounterDao {
	public void insert(MessageCounterEntity entity);
	public List<MessageCounterEntity> load(long ip);
	public void update(MessageCounterEntity entity);
	public MessageCounterEntity get(String id );
}
