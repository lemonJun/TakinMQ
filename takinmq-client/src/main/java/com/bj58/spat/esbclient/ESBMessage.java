package com.bj58.spat.esbclient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import com.bj58.spat.esbclient.util.ByteConverter;
import com.bj58.spat.esbclient.exception.SerializeException;

public class ESBMessage {

    /** 版本 [length:1] */
    /**
     * {@link Date}2013-06-05
     * 取消ACK版，升级协议版本号为2
     * */
    private byte version = 0x02;
    /** 协议头长度 */
    public final static int HEADER_LENGTH = 46;
    /** 分隔符 */
    public final static byte[] DELIMITER = new byte[] { 9, 10, 13, 17, 18 };
    /** 消息长度 [length:4] */
    private int totalLen;
    /**消息属性 :
     * 0发布者发送;
     * 1推送发送者ACK应答;
     * 2推送订阅者;
     * 3发布者重发[length:1];
     * 4服务器reboot时返回客户端
     * 5客户端探测
     * */
    private byte commandType;
    /**1:ESBMessage 2:ESBSubject*/
    private byte protocolType = 0x01;
    /** 主题 [length:4] */
    private int subject;
    /** 消息id [length:8] */
    private long messageID;
    /**sessionID[length:8]*/
    private long sessionID;
    /** 消息类型(0持久化类型;1非持久化类型) */
    private byte messageType = 0;
    /** 投递方式[length:1] */
    private byte deliveryMode = 0;
    /** 客户端ID [length:4] */
    private int clientID;
    /**是否需要回应(0不需要，1需要)*/
    private byte needReplay = 0;
    /**客户端IP*/
    private int ip;
    /**时间戳*/
    private long timestamp;

    private byte[] body;

    public ESBMessage() {

    }

    private ESBMessage(int subject, byte messageType, byte deliveryMode, long sessionID, byte[] body) {
        this.subject = subject;
        this.messageType = messageType;
        this.deliveryMode = deliveryMode;
        this.body = body;
    }

    public ESBMessage(int subject, byte[] body) {
        this(subject, (byte) 0, (byte) 0, 0, body);
    }

    public ESBMessage(ESBSubject subject, byte[] body) {
        this(subject.getSubjectID(), (byte) 0, (byte) 0, 0, body);
    }

    public ESBMessage(int subject, byte messageType, byte deliveryMode, byte[] body) {
        this(subject, messageType, deliveryMode, 0, body);
    }

    /**
     * 订阅者返回ACK
     */
    public ESBMessage(long messageID, int subject, int clientID, byte messageType, byte deliveryMode, long sessionID, byte[] body) {
        this.messageID = messageID;
        this.subject = subject;
        this.commandType = 1;
        this.clientID = clientID;
        this.messageType = messageType;
        this.deliveryMode = deliveryMode;
        this.sessionID = sessionID;
        this.body = body;
    }

    public byte[] toBytes() throws SerializeException {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            if (body != null) {
                stream.write(ByteConverter.intToBytesBigEndian(HEADER_LENGTH + body.length));
            } else {
                stream.write(ByteConverter.intToBytesBigEndian(HEADER_LENGTH));
            }

            stream.write(version);
            stream.write(commandType);
            stream.write(protocolType);
            stream.write(ByteConverter.intToBytesBigEndian(this.getSubject()));
            stream.write(ByteConverter.longToBytesBigEndian(this.getMessageID()));
            stream.write(ByteConverter.longToBytesBigEndian(this.getSessionID()));
            stream.write(messageType);
            stream.write(deliveryMode);
            stream.write(ByteConverter.intToBytesBigEndian(this.getClientID()));
            stream.write(needReplay);
            stream.write(ByteConverter.intToBytesBigEndian(this.getIp()));
            stream.write(ByteConverter.longToBytesBigEndian(this.getTimestamp()));

            if (body != null) {
                stream.write(body);
            }

            return stream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new SerializeException(e);
                }
            }
        }
    }

    public static ESBMessage fromBytes(byte[] buf) {
        int index = 0;

        int totalLen = ByteConverter.bytesToIntBigEndian(buf, index);
        index += 4;

        byte version = buf[index];
        index++;

        byte commandType = buf[index];
        index++;

        byte protocolType = buf[index];
        index++;

        int subject = ByteConverter.bytesToIntBigEndian(buf, index);
        index += 4;

        long messageID = ByteConverter.bytesToLongBigEndian(buf, index);
        index += 8;

        long sessionID = ByteConverter.bytesToLongBigEndian(buf, index);
        index += 8;

        byte messageType = buf[index];
        index++;

        byte deliveryMode = buf[index];
        index++;

        int clientID = ByteConverter.bytesToIntBigEndian(buf, index);
        index += 4;

        byte needReplay = buf[index];
        index++;

        int ip = ByteConverter.bytesToIntBigEndian(buf, index);
        index += 4;

        long timestamp = ByteConverter.bytesToLongBigEndian(buf, index);
        index += 8;

        byte[] body = new byte[totalLen - HEADER_LENGTH];
        if (body.length > 0) {
            System.arraycopy(buf, index, body, 0, totalLen - HEADER_LENGTH);
        }
        ESBMessage esbMessage = new ESBMessage();
        esbMessage.setTotalLen(totalLen);
        esbMessage.setVersion(version);
        esbMessage.setCommandType(commandType);
        esbMessage.setProtocolType(protocolType);
        esbMessage.setSubject(subject);
        esbMessage.setMessageID(messageID);
        esbMessage.setSessionID(sessionID);
        esbMessage.setMessageType(messageType);
        esbMessage.setDeliveryMode(deliveryMode);
        esbMessage.setClientID(clientID);
        esbMessage.setNeedReplay(needReplay);
        esbMessage.setIp(ip);
        esbMessage.setTimestamp(timestamp);
        esbMessage.setBody(body);
        return esbMessage;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(byte deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public byte getProtocolType() {
        return protocolType;
    }

    public byte getNeedReplay() {
        return needReplay;
    }

    public void setNeedReplay(byte needReplay) {
        this.needReplay = needReplay;
    }

    public byte getCommandType() {
        return commandType;
    }

    public void setCommandType(byte commandType) {
        this.commandType = commandType;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
