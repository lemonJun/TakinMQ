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

package com.takin.mq.store2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * the log segments(all readable messages and the last writeable file)
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 1.0
 */
public class SegmentList {

    private final AtomicReference<List<Segment>> contents;

    private final String name;

    /**
     * create the messages segments
     * 
     * @param name the message topic name
     * @param segments exist segments
     */
    public SegmentList(final String name, List<Segment> segments) {
        this.name = name;
        contents = new AtomicReference<List<Segment>>(segments);
    }

    /**
     * Append the given item to the end of the list
     * @param segment segment to append
     */
    public void append(Segment segment) {
        while (true) {
            List<Segment> curr = contents.get();
            List<Segment> updated = new ArrayList<Segment>(curr);
            updated.add(segment);
            if (contents.compareAndSet(curr, updated)) {
                return;
            }
        }
    }

    /**
     * Delete the first n items from the list
     * 
     * @param newStart the logsegment who's index smaller than newStart will be deleted.
     * @return the deleted segment
     */
    public List<Segment> trunc(int newStart) {
        if (newStart < 0) {
            throw new IllegalArgumentException("Starting index must be positive.");
        }
        while (true) {
            List<Segment> curr = contents.get();
            int newLength = Math.max(curr.size() - newStart, 0);
            List<Segment> updatedList = new ArrayList<Segment>(curr.subList(Math.min(newStart, curr.size() - 1), curr.size()));
            if (contents.compareAndSet(curr, updatedList)) {
                return curr.subList(0, curr.size() - newLength);
            }
        }
    }

    /**
     * get the last segment at the moment
     * 
     * @return the last segment
     */
    public Segment getLastView() {
        List<Segment> views = getView();
        return views.get(views.size() - 1);
    }

    /**
     * get all segments at the moment
     * 
     * @return all segments
     */
    public List<Segment> getView() {
        return contents.get();
    }

    @Override
    public String toString() {
        return "[" + name + "] " + getView();
    }
}
