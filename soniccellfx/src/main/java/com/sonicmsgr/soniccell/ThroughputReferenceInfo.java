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
package com.sonicmsgr.soniccell;

import java.io.Serializable;

/**
 *
 * @author yada
 */
public class ThroughputReferenceInfo implements Serializable {
    public static final int IO_TYPE_INPUT = 0;
    public static final int IO_TYPE_OUTPUT = 1;
    
    public int ioType;  // 0 for input and 1 for output
    public int srcId;
    public int srcThruId;
    public int destId;
    public int destThruId;
}
