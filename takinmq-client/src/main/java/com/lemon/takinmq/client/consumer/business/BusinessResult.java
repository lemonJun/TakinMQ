package com.lemon.takinmq.client.consumer.business;

import com.lemon.takinmq.remoting.core.domain.Action;

/**
 * 业务执行完后的返回结果
 *
 *
 * @author lemon
 * @version 1.0
 * @date  2015年9月19日 上午11:29:54
 * @see 
 * @since
 */
public class BusinessResult {

    private Action action;
    private String msg;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
