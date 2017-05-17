package com.bj58.esb.server.store;

import com.bj58.esb.server.store.command.CommandHeader;
import com.bj58.esb.server.store.command.RequestCommand;

/**
 * 请求应用基类
 * 
 */
public abstract class AbstractRequestCommand implements RequestCommand, MetaEncodeCommand {
    private Integer opaque;
    private String topic;
    static final long serialVersionUID = -1L;


    public AbstractRequestCommand(final String topic, final Integer opaque) {
        super();
        this.topic = topic;
        this.opaque = opaque;
    }


    @Override
    public CommandHeader getRequestHeader() {
        return this;
    }


    @Override
    public Integer getOpaque() {
        return this.opaque;
    }


    public void setOpaque(final Integer opaque) {
        this.opaque = opaque;
    }


    public String getTopic() {
        return this.topic;
    }


    public void setTopic(final String topic) {
        this.topic = topic;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.opaque == null ? 0 : this.opaque.hashCode());
        result = prime * result + (this.topic == null ? 0 : this.topic.hashCode());
        return result;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final AbstractRequestCommand other = (AbstractRequestCommand) obj;
        if (this.opaque == null) {
            if (other.opaque != null) {
                return false;
            }
        }
        else if (!this.opaque.equals(other.opaque)) {
            return false;
        }
        if (this.topic == null) {
            if (other.topic != null) {
                return false;
            }
        }
        else if (!this.topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

//    public abstract void test();
    
}