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
package com.sonicmsgr.soniccell.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author yada
 */
public class ComponentDBList {

    private final Map<String, ArrayList<ComponentItem>> componentList = new TreeMap<String, ArrayList<ComponentItem>>();

    public int getSize() {
        return componentList.size();
    }

    public Map<String, ArrayList<ComponentItem>> getHashMap() {
        sortByName();
        return componentList;
    }

    public void add(String category, ComponentItem componentItem) {
        if (componentList.containsKey(category)) {
            ArrayList<ComponentItem> al = componentList.get(category);
            if (al.contains(componentItem)) {
                for (int i = 0; i < al.size(); i++) {
                    if (al.equals(componentItem)) {
                        al.add(i, componentItem);
                        return;
                    }
                }
            } else {
                al.add(componentItem);
            }
        } else {
            ArrayList<ComponentItem> al = new ArrayList<ComponentItem>();
            al.add(componentItem);
            componentList.put(category, al);

        }
    }

    public void sortByName() {
        for (Map.Entry<String, ArrayList<ComponentItem>> entry : componentList.entrySet()) {
            Collections.sort(entry.getValue());
        }
    }

    public String getClassName(String category, String shortName) {
        ArrayList<ComponentItem> al = getComponentItems(category);
        if ((al == null)) {
            return "";
        }

        int size = al.size();
        for (int i = 0; i < size; i++) {
            ComponentItem item = al.get(i);
            if (item.shortName.equalsIgnoreCase(shortName)) {
                return item.className;
            }
        }

        return "";
    }

    public void remove(String cat, ComponentItem componentItem) {
        if (componentList.containsKey(cat)) {
            ArrayList<ComponentItem> al = componentList.get(cat);
            al.remove(componentItem);

        }
    }

    public ArrayList<ComponentItem> getComponentItems(String category) {
        return componentList.get(category);
    }

    public void removeCategory(String cat) {
        componentList.remove(cat);
    }

}
