package com.lemon.takinmq.remoting;

/**
 * A class contains global variable
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
public class Global {

    private ThreadLocal<RemotingContext> threadLocal = new ThreadLocal<RemotingContext>();

    private static final Object lockHelper = new Object();

    private static Global m_global = null;

    /**
     * 获取单例Global
     * @return
     */
    public static Global getSingleton() {
        if (m_global == null) {
            synchronized (lockHelper) {
                if (m_global == null) {
                    m_global = new Global();
                }
            }
        }

        return m_global;
    }

    private Global() {
    }

    public ThreadLocal<RemotingContext> getThreadLocal() {
        return threadLocal;
    }

}