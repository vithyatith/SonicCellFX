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

/**
 *
 * @author yada
 */
public class ComponentItem implements Comparable<ComponentItem>{

    public String className;
    public String shortName;

    public ComponentItem(String className, String shortName) {
        this.className = className;
        this.shortName = shortName;
    }

    @Override
    public int compareTo(ComponentItem o) {
        
        return this.shortName.compareTo(o.shortName);

      //   return (this.shortName) > o.shortName ? 1 : this.shortName < o.shortName ? -1 : 0;
    }

}
