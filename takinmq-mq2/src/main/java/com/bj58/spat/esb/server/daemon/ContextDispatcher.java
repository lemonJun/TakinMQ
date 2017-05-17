package com.bj58.spat.esb.server.daemon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffers;

import com.bj58.spat.esb.server.protocol.ESBMessage;
import com.bj58.spat.esb.server.bootstrap.constant.ServerState;
import com.bj58.spat.esb.server.communication.ESBContext;

/**
 * main worker 
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class ContextDispatcher {

    private static final Log logger = LogFactory.getLog(ContextDispatcher.class);
    private static final SendWorker sendWorker = new SendWorker();
    private static final SubscribeWorker subscribeWorker = new SubscribeWorker();
    private static final PublisherAckWorker publisherAckWorker = new PublisherAckWorker();
    private static ErrorWorker errorWorker = ErrorWorker.getInstrance();
    private static QueueInsertWorker qworker = new QueueInsertWorker();

    public static void start() {
        errorWorker.start();
        subscribeWorker.start();
        publisherAckWorker.start();
        LogWorker.start();
        qworker.start();
    }

    public static void dispatch(ESBContext ctx) {

        try {
            if (ctx != null) {
                if (ctx.getBuf()[6] == ESBMessage.PROTOCOL_TYPE) {
                    /**
                     * 0:表示发布者发送消息,返回发布者ACK,并推送订阅者
                     * 1:订阅者返回ACK
                     * 3:发布者重新发送消息,返回ACK如果不为重复则进行推送
                     */
                    switch (ctx.getBuf()[5]) {
                        case 0:
                            /**返回发送者ACK*/
                            if (ctx.getBuf()[4] == 0x01) {
                                publisherAckWorker.offer(ctx);
                            }
                            /**推送订阅者*/
                            sendWorker.offer(ctx);
                            break;
                        case 1:
                            //handleWorker.offer(ctx);
                            break;
                        case 3:
                            if (ctx.getBuf()[4] == 0x01) {
                                publisherAckWorker.offer(ctx);
                            }
                            break;
                        case 5:
                            //客户端探测
                            if (ctx.getBuf()[4] == 0x02) {
                                ESBMessage message = new ESBMessage();
                                if (ServerState.isRebooting()) {
                                    message.setCommandType((byte) 4);
                                } else {
                                    message.setCommandType((byte) 6);
                                }
                                ctx.getChannel().write(ChannelBuffers.copiedBuffer(message.toBytes(), ESBMessage.DELIMITER));
                            }
                            break;
                        default:
                            logger.warn("发送者发送消息类型错误!类型ID is " + ctx.getBuf()[5]);
                            break;
                    }
                } else {
                    subscribeWorker.offer(ctx);
                }
            }
        } catch (Exception ex) {
            logger.error("[ESB] MainWork.MainTask error " + ex.getMessage());
        }

    }
}
