//
//package com.takin.mq.message;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//
//import com.takin.emmet.store.GZIPCompression;
//
///**
// * 压缩  解压
// *
// *
// * @version 1.0
// * @date  2017年4月16日 下午1:08:43
// * @see 
// * 
// * @since
// */
//public class CompressionUtils {
//    public static Message compress(Message[] messages, CompressionCodec compressionCodec) {
//        GZIPCompression gzipcom = null;
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            gzipcom = new GZIPCompression(null, outputStream);
//            ByteBuffer messageByteBuffer = ByteBuffer.allocate(MessageSet.messageSetSize(messages));
//            for (Message message : messages) {
//                message.serializeTo(messageByteBuffer);
//            }
//            messageByteBuffer.rewind();
//
//            gzipcom.write(messageByteBuffer.array());
//        } catch (IOException e) {
//            throw new IllegalStateException("writting data failed", e);
//        } finally {
//            gzipcom.close();
//        }
//        return new Message(outputStream.toByteArray(), compressionCodec);
//    }
//
//    public static ByteBufferMessageSet decompress(Message message) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        GZIPCompression gzipcom = null;
//        try {
//            InputStream inputStream = new ByteBufferBackedInputStream(message.payload());
//            //
//            byte[] intermediateBuffer = new byte[1024];
//            gzipcom = new GZIPCompression(inputStream, null);
//            int dataRead = 0;
//            while ((dataRead = gzipcom.read(intermediateBuffer)) > 0) {
//                outputStream.write(intermediateBuffer, 0, dataRead);
//            }
//        } catch (IOException e) {
//            throw new IllegalStateException("decompression data failed", e);
//        } finally {
//            gzipcom.close();
//        }
//        ByteBuffer outputBuffer = ByteBuffer.allocate(outputStream.size());
//        outputBuffer.put(outputStream.toByteArray());
//        outputBuffer.rewind();
//        return new ByteBufferMessageSet(outputBuffer);
//    }
//
//}
