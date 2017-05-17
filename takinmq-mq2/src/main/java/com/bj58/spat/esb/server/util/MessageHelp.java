package com.bj58.spat.esb.server.util;

import com.bj58.spat.esb.server.store.MongoBase;

public class MessageHelp {
	
	public static long getMessageID() throws Exception{
		return MongoBase.GenInfoID();
	}
}
