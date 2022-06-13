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
package com.sonicmsgr.soniccell;

/**
 *
 * @author Vithya Tith
 */
public class DataTypeIO {

    private String typeName = "";
    private String propName = "";
    private String descriptions = "";

    public DataTypeIO() {
    }

    public DataTypeIO(String type) {
        this.typeName = type;
    }

    public DataTypeIO(String type, String descriptions) {
        this.typeName = type;
        this.descriptions = descriptions;
    }

    public DataTypeIO(String type, String descriptions, String propName) {
        this.typeName = type;
        this.descriptions = descriptions;
        this.propName = propName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDescriptions() {
        return descriptions;
    }
    
    public String getPropName(){
        return propName;
    }

}
