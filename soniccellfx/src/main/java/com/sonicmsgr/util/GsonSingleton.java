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
package com.sonicmsgr.util;

import com.google.gson.Gson;

/**
 *
 * @author vithya
 */
public class GsonSingleton {

    private  static Gson gson = null;

    private GsonSingleton(){

    }

    public static Gson getInstance(){

        if(gson==null){
            gson = new Gson();
        }
        return gson;
    }

}
