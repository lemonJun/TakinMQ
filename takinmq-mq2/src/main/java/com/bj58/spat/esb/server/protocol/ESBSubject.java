package com.bj58.spat.esb.server.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bj58.spat.esb.server.exception.SerializeException;
import com.bj58.spat.esb.server.util.ByteConverter;

public class ESBSubject {

	public final static byte[] DELIMITER = new byte[] { 9, 10, 13, 17, 18 };
	
	private final static int TOTAL_LENGTH = 15;
	/**总长度 暂没用,为了与发送消息协议保持一致*/
	private int totalLen = 0;
	/**版本*/
	private byte version = 0x01;
	/**消息属性(-1订阅)暂没用,为了与发送消息协议保持一致*/
	private byte commandType = -1;
	/**消息类型： 1:ESBMessage 2:ESBSubject*/
	private byte protocolType = 0x02;
	/**主题ID*/
	private int subjectID;
	/**客户端ID*/
	private int clientID;
	

	public ESBSubject(byte version, 
			byte protocolType, 
			int subjectID,
			int clientID) {
		super();
		this.version = version;
		this.protocolType = protocolType;
		this.subjectID = subjectID;
		this.clientID = clientID;
	}


	public ESBSubject(int subjectID) {
		super();
		this.subjectID = subjectID;
	}
	
	
	public static List<ESBSubject> fromBytes(byte[] buf) {
		List<ESBSubject> subjs = new ArrayList<ESBSubject>(); 
		int index = 0;
		for(int i=0; i<buf.length; i+=TOTAL_LENGTH){
			int totalLen = ByteConverter.bytesToIntBigEndian(buf, index);
			index += 4;
			
			byte version = buf[index];
			index++;
			
			byte commandType = buf[index];
			index++;
			
			byte protocolType = buf[index];
			index++;
			
			int subjectID = ByteConverter.bytesToIntBigEndian(buf, index);
			index += 4;
			
			int clientID = ByteConverter.bytesToIntBigEndian(buf, index);
			index += 4;
			
			ESBSubject subj = new ESBSubject(version, protocolType, subjectID, clientID);
			subjs.add(subj);
		}
		
		return subjs;
	}
	
	public static byte[] toBytes(ESBSubject ... subject) throws SerializeException {
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			for(ESBSubject subj :subject){
				stream.write(ByteConverter.intToBytesBigEndian(subj.getTotalLen()));
				stream.write(subj.getVersion());
				stream.write(subj.getCommandType());
				stream.write(subj.getProtocolType());
				stream.write(ByteConverter.intToBytesBigEndian(subj.getSubjectID()));
				stream.write(ByteConverter.intToBytesBigEndian(subj.getClientID()));
			}
			return stream.toByteArray();
		} catch (Exception e) {
			throw new SerializeException(e);
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new SerializeException(e);
				}
			}
		}
	}

	public int getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public int getClientID() {
		return clientID;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public byte getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(byte protocolType) {
		this.protocolType = protocolType;
	}
	
	public int getTotalLen() {
		return totalLen;
	}

	public void setTotalLen(int totalLen) {
		this.totalLen = totalLen;
	}

	public byte getCommandType() {
		return commandType;
	}

	public void setCommandType(byte commandType) {
		this.commandType = commandType;
	}
}
