/*
 * Copyright 2022 Vithya Tith
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.ributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See t
 */
package com.sonicmsgr.pubsub;

import java.util.*;

public class PubSubReplySingleton {

    private final ArrayList<PubSubMsgListener> msgListenerArray = new ArrayList<PubSubMsgListener>();
    private static PubSubReplySingleton pubSubSingleton = null;
    private final String tmpStr = "";

    public static PubSubReplySingleton getIntance() {

        if (pubSubSingleton == null) {
            pubSubSingleton = new PubSubReplySingleton();
        }
        return pubSubSingleton;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ArrayList al = new ArrayList();

        al.add("vt");
        al.add("vt");

        PubSubReplySingleton m = PubSubReplySingleton.getIntance();
        m.addListener(new PubSubMsgListener("a") {

            public Object onMessage(Object msg) {
                System.out.println("got ib");
                return null;

            }
        });

        m.addListener(new PubSubMsgListener("a") {

            public Object onMessage(Object msg) {
                System.out.println("again");
                return null;
            }
        });

        m.producerSend("a", new HashMap());
    }

    private synchronized Object updateConsumer(String _topicName, Object msg, int method) {
        int size = msgListenerArray.size();
        for (int i = 0; i < size; i++) {
            PubSubMsgListener ml = msgListenerArray.get(i);
            if (ml.getTopicName().equals(_topicName)) {

                if (method == 1) {
                    return ml.onMessage(msg);
                } else {
                    ml.onMessage(msg);
                }
            }
        }
        return null;
    }

    public synchronized void addListener(PubSubMsgListener msg) {
        msgListenerArray.add(msg);
    }

    public synchronized void producerSend(String _topicname, Object msg) {
        updateConsumer(_topicname, msg, 0);
    }

    public synchronized Object sendReplyTopic(String _topicname, Object msg) {
        return updateConsumer(_topicname, msg, 1);
    }
}
