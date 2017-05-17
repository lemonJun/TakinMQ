package com.bj58.esb.server.store;

import java.util.Arrays;

import com.bj58.esb.server.store.buffer.IoBuffer;
import com.bj58.esb.server.store.command.CommandHeader;
import com.bj58.esb.server.store.utils.ByteUtils;


/**
 * 发送消息命令，协议格式： </br></br> put topic partition value-length flag checksum
 * [transactionkey] opaque\r\n data </br></br> data的结构如下：</br> </br>
 * attribute(0个或者1个，固定长度字符串，取决于flag字段) + binary data
 * 
 */
public class PutCommand extends AbstractRequestCommand {
    static final long serialVersionUID = -1L;
    protected byte[] data;
    protected int partition;
    protected final int flag;
    protected int checkSum = -1; 
    // 23.
    private TransactionId transactionId;


    public TransactionId getTransactionId() {
        return this.transactionId;
    }


    public void setTransactionId(final TransactionId transactionId) {
        this.transactionId = transactionId;
    }


    public byte[] getData() {
        return this.data;
    }


    public int getCheckSum() {
        return this.checkSum;
    }


    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }


    public int getFlag() {
        return this.flag;
    }


    public void setData(final byte[] data) {
        this.data = data;
    }


    public void setPartition(final int partition) {
        this.partition = partition;
    }


    public int getPartition() {
        return this.partition;
    }


    public PutCommand(final String topic, final int partition, final byte[] data, final TransactionId transactionId,
            final int flag, final Integer opaque) {
        this(topic, partition, data, flag, -1, transactionId, opaque);
    }


    public PutCommand(final String topic, final int partition, final byte[] data, final int flag,
            final int checkSum, final TransactionId transactionId, final Integer opaque) {
        super(topic, opaque);
        this.partition = partition;
        this.data = data;
        this.flag = flag;
        this.checkSum = checkSum;
        this.transactionId = transactionId;
    }


    @Override
    public CommandHeader getRequestHeader() {
        return new CommandHeader() {
            @Override
            public Integer getOpaque() {
                return PutCommand.this.getOpaque();
            }
        };
    }


    public IoBuffer encode() {
        final int dataLen = this.data == null ? 0 : this.data.length;
        final String transactionKey = this.transactionId != null ? this.transactionId.getTransactionKey() : null;

        final IoBuffer buffer =
                IoBuffer.allocate(11 + ByteUtils.stringSize(this.partition) + ByteUtils.stringSize(dataLen)
                        + ByteUtils.stringSize(this.getOpaque()) + this.getTopic().length()
                        + (transactionKey != null ? transactionKey.length() + 1 : 0) + ByteUtils.stringSize(this.flag)
                        + ByteUtils.stringSize(this.checkSum) + dataLen);
        if (transactionKey != null) {
            ByteUtils.setArguments(buffer, MetaEncodeCommand.PUT_CMD, this.getTopic(), this.partition, dataLen,
                this.flag, this.checkSum, transactionKey, this.getOpaque());
        }
        else {
            ByteUtils.setArguments(buffer, MetaEncodeCommand.PUT_CMD, this.getTopic(), this.partition, dataLen,
                this.flag, this.checkSum, this.getOpaque());
        }
        if (this.data != null) {
            buffer.put(this.data);
        }
        buffer.flip();
        return buffer;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.checkSum;
        result = prime * result + Arrays.hashCode(this.data);
        result = prime * result + this.flag;
        result = prime * result + this.partition;
        result = prime * result + (this.transactionId == null ? 0 : this.transactionId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PutCommand other = (PutCommand) obj;
        if (this.checkSum != other.checkSum) {
            return false;
        }
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        if (this.flag != other.flag) {
            return false;
        }
        if (this.partition != other.partition) {
            return false;
        }
        if (this.transactionId == null) {
            if (other.transactionId != null) {
                return false;
            }
        }
        else if (!this.transactionId.equals(other.transactionId)) {
            return false;
        }
        return true;
    }

}