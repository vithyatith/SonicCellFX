/*
 * Copyright 2018 Vithya Tith
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
package com.sonicmsgr.soniccell.msg;

import java.util.*;

/**
 *
 * @author vithya.tith
 */
public class PubSubSingleton {

    private ArrayList<MsgListener> msgListenerArray = new ArrayList<MsgListener>();
    private HashMap<Integer, ResponseListener> responeListenerArray = new HashMap<Integer, ResponseListener>();
    private static PubSubSingleton pubSubSingleton = null;
    private String tmpStr = "";

    public static PubSubSingleton getIntance() {

        if (pubSubSingleton == null) {
            pubSubSingleton = new PubSubSingleton();
        }
        return pubSubSingleton;
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        PubSubSingleton m = PubSubSingleton.getIntance();
        m.addListener(new MsgListener("a") {

            public void onMessage(Object msg) {
                System.out.println(msg);
            }
        });
        
        
        m.addListener(new MsgListener("sock error topic") {

            public void onMessage(Object msg) {
                System.out.println("Got new message and the socket error is: "+msg);

            }
        });
        

        m.addListener(new MsgListener("a") {

            public void onMessage(Object msg) {
                System.out.println(msg);
            }
        });

        m.addReponseListener(new ResponseListener("hello") {

            public Object reply(Object msg) {
                
                return "Got the response " +msg;
            }
        });

        
        

        System.out.println(m.sendReply("hello", "There"));

        m.send("sock error topic", "Just got disconnected.");

    }

    private synchronized void updateConsumer(String _topicName, Object msg) {
        int size = msgListenerArray.size();
        for (int i = 0; i < size; i++) {
            MsgListener ml = msgListenerArray.get(i);
            if (ml.topicHashCode==_topicName.hashCode()) {
                ml.onMessage(msg);
            }
        }
    }

    public synchronized void addListener(MsgListener msg) {
        msgListenerArray.add(msg);
    }

    public synchronized void addReponseListener(ResponseListener msg) {
        responeListenerArray.put(msg.topicHashCode, msg);
    }

    public synchronized Object sendReply(String name, Object argObj) {
        return responeListenerArray.get((Integer)name.hashCode()).reply(argObj);
    }

    public synchronized void send(String _topicname, Object msg) {
        updateConsumer(_topicname, msg);
    }
}
