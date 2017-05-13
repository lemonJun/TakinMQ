/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.takin.mq.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.takin.emmet.store.GZIPCompression;

/**
 * 压缩  解压
 *
 *
 * @author Administrator
 * @version 1.0
 * @date  2017年4月16日 下午1:08:43
 * @see 
 * 
 * @since
 */
public class CompressionUtils {

    public static Message compress(Message[] messages, CompressionCodec compressionCodec) {
        GZIPCompression gzipcom = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            gzipcom = new GZIPCompression(null, outputStream);
            ByteBuffer messageByteBuffer = ByteBuffer.allocate(MessageSet.messageSetSize(messages));
            for (Message message : messages) {
                message.serializeTo(messageByteBuffer);
            }
            messageByteBuffer.rewind();

            gzipcom.write(messageByteBuffer.array());
        } catch (IOException e) {
            throw new IllegalStateException("writting data failed", e);
        } finally {
            gzipcom.close();
        }
        return new Message(outputStream.toByteArray(), compressionCodec);
    }

    public static ByteBufferMessageSet decompress(Message message) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPCompression gzipcom = null;
        try {
            InputStream inputStream = new ByteBufferBackedInputStream(message.payload());
            //
            byte[] intermediateBuffer = new byte[1024];
            gzipcom = new GZIPCompression(inputStream, null);
            int dataRead = 0;
            while ((dataRead = gzipcom.read(intermediateBuffer)) > 0) {
                outputStream.write(intermediateBuffer, 0, dataRead);
            }
        } catch (IOException e) {
            throw new IllegalStateException("decompression data failed", e);
        } finally {
            gzipcom.close();
        }
        ByteBuffer outputBuffer = ByteBuffer.allocate(outputStream.size());
        outputBuffer.put(outputStream.toByteArray());
        outputBuffer.rewind();
        return new ByteBufferMessageSet(outputBuffer);
    }

}
