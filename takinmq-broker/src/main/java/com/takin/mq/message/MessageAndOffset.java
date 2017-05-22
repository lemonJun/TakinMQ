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

/**
 * Represents message and offset of the next message. This is used in the
 * MessageSet to iterate over it
 * 当存储的时候是不需要知道offset的，而一旦消息写入文件中，它就有了一个固定的offset值
 * 
 * 
 * @since 1.0
 */
public class MessageAndOffset {

    private final Message message;

    private final long offset;

    public MessageAndOffset(Message message, long offset) {
        this.message = message;
        this.offset = offset;
    }

    public Message getMessage() {
        return message;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return String.format("MessageAndOffset [offset=%s, message=%s]", offset, message);
    }
}
