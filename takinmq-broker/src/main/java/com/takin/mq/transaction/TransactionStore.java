package com.takin.mq.transaction;

import java.util.List;

public interface TransactionStore {
    public boolean open();

    public void close();

    public boolean put(final List<TransactionRecord> trs);

    public void remove(final List<Long> pks);

    public List<TransactionRecord> traverse(final long pk, final int nums);

    public long totalRecords();

    public long minPK();

    public long maxPK();
}
