package com.lemon.takinmq.remoting.netty5;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.remoting.netty5.MessageType;
import com.lemon.takinmq.remoting.netty5.NettyMessage;
import com.lemon.takinmq.remoting.util.SerializeUtil;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        NettyMessage msg = (NettyMessage) obj;
        if (msg.getType() == MessageType.REMOTING_INVOKE.value()) {
            logger.info(JSON.toJSONString(msg));
            String clazzName = msg.getClazz();
            String methodName = msg.getMethod();
            Object[] args = msg.getArgs();
            //            ClassPool pool = ClassPool.getDefault();
            //            Class clazz = pool.get(clazzStr).toClass();
            Class c[] = null;
            if (args != null) {//存在
                int len = args.length;
                c = new Class[len];
                for (int i = 0; i < len; ++i) {
                    c[i] = args[i].getClass();
                }
            }
            Class clazz = Class.forName(clazzName);
            Method method = clazz.getDeclaredMethod(methodName, c);
            //            Object impl = GuiceDI.getInstance(clazz);
            Object impl = clazz.newInstance();

            Object result = method.invoke(impl, args);
            if (!method.getReturnType().getName().equals("void")) {
                msg.setResultJson(SerializeUtil.jsonSerialize(result));
            }
            ctx.writeAndFlush(msg);
        } else {
            ctx.fireChannelRead(obj);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
