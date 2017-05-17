package com.bj58.spat.esb.server.config;

import java.util.List;

public class Subject {
    private int subject;
    private boolean isQueue;
    private boolean canAbandon = false;

    public boolean isCanAbandon() {
        return canAbandon;
    }

    public void setCanAbandon(boolean canAbandon) {
        this.canAbandon = canAbandon;
    }

    private List<Client> clientList;

    public Subject(int subject, List<Client> clientList, boolean isQueue, boolean canAbandon) {
        this.subject = subject;
        this.clientList = clientList;
        this.isQueue = isQueue;
        this.canAbandon = canAbandon;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public boolean isQueue() {
        return isQueue;
    }

    public void setQueue(boolean isQueue) {
        this.isQueue = isQueue;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }
}
