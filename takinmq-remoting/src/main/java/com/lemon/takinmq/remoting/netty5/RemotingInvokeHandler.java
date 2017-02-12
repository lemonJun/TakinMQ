package com.lemon.takinmq.remoting.netty5;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lemon.takinmq.common.anno.ImplementBy;
import com.lemon.takinmq.common.util.SerializeUtil;
import com.lemon.takinmq.common.util.StringUtils;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 接收客户端发起的请求   并按
 * 
 * 
 * @author lemon
 * @version 1.0
 * @date  2015年10月14日 下午4:09:22
 * @see 
 * @since
 */
public class RemotingInvokeHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(RemotingInvokeHandler.class);

    //设置环境变量
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        RemotingMessage msg = (RemotingMessage) obj;
        try {
            logger.info("REQUEST: " + JSON.toJSONString(msg));
            RemotingContext context = new RemotingContext(ctx);
            //            GlobalContext.getSingleton().setThreadLocal(context);
            String clazzName = msg.getClazz();
            String methodName = msg.getMethod();
            Object[] args = msg.getArgs();
            //            ClassPool pool = ClassPool.getDefault();
            //            Class clazz = pool.get(clazzStr).toClass();
            //
            //确定方法参数
            Class<?> mc[] = null;
            if (args != null) {//存在
                int len = args.length;
                mc = new Class[len];
                for (int i = 0; i < len; ++i) {
                    mc[i] = args[i].getClass();
                }
            }
            if (StringUtils.isNotEmpty(clazzName)) {
                //反射调用
                Class<?> clazz = Class.forName(clazzName);
                if (clazz.isAnnotationPresent(ImplementBy.class)) {
                    ImplementBy impl = (ImplementBy) clazz.getAnnotation(ImplementBy.class);

                    Method method = clazz.getDeclaredMethod(methodName, mc);
                    Object target = getOjbectFromClass(impl.implclass());
                    Object result = method.invoke(target, args);
                    if (!method.getReturnType().getName().equals("void")) {
                        msg.setResultJson(SerializeUtil.jsonSerialize(result));
                    }
                }
            } else {
                msg.setResultJson("no class name content");
            }
            logger.info("RESPONSE: " + JSON.toJSONString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //            GlobalContext.getSingleton().removeThreadLocal();
            ctx.writeAndFlush(msg);
        }
    }

    //获取实现类
    private Object getOjbectFromClass(String clazz) {
        if (implMap.get(clazz) == null) {
            synchronized (RemotingInvokeHandler.class) {
                if (implMap.get(clazz) == null) {
                    try {
                        Object obj = Class.forName(clazz).newInstance();
                        implMap.put(clazz, obj);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
        return implMap.get(clazz);
    }

    private final Map<String, Object> implMap = Maps.newConcurrentMap();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        logger.info("channel connect " + remoteAddress);
    }

    //    @Override
    //    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //        super.channelReadComplete(ctx);
    //    }
    //
    //    @Override
    //    public void flush(ChannelHandlerContext ctx) throws Exception {
    //        super.flush(ctx);
    //    }

}
