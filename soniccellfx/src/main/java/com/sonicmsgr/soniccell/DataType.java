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

public final class DataType implements java.io.Serializable{
    
    // Number
    public static final int BYTE=1;
    public static final int BYTE_VECTOR=2;
    public static final int BYTE_MATRIX=3;
    
    public static final int SHORT=4;
    public static final int SHORT_VECTOR=5;
    public static final int SHORT_MATRIX=6;
    
    public static final int INT=7;
    public static final int INT_VECTOR=8;
    public static final int INT_MATRIX=9;
    
    public static final int LONG=10;
    public static final int LONG_VECTOR=11;
    public static final int LONG_MATRIX=12;
    
    public static final int FLOAT=13;
    public static final int FLOAT_VECTOR=14;
    public static final int FLOAT_MATRIX=15;
    
    public static final int DOUBLE=16;
    public static final int DOUBLE_VECTOR=17;
    public static final int DOUBLE_MATRIX=18;
    
    public static final int COMPLEX=19;
    public static final int COMPLEX_VECTOR=20;
    public static final int COMPLEX_MATRIX=21;
    
    public static final int ANY_NUMBER = 22;
    public static final int ANY_NUMBER_VECTOR = 23;
    public static final int ANY_NUMBER_MATRIX = 24;
    
    // String
    public static final int STRING=101;
    public static final int STRING_VECTOR=102;
    public static final int STRING_MATRIX=103;
    
    // Object
    public static final int OBJECT=201;
    public static final int OBJECT_VECTOR=202;
    public static final int OBJECT_MATRIX=203;
    
    // Anything
    public static final int ANYTHING=301;
    
    // Multitype
    public static final int MULTI_TYPE = 401;
    
    // No type
    // Multitype
    public static final int NO_TYPE = 500;
    public static String getName(int id){
        String name="";
        switch(id){
            case BYTE:
                name="byte";
                break;
            case BYTE_VECTOR:
                name="byte[]";
                break;
            case BYTE_MATRIX:
                name="byte[][]";
                break;
            case SHORT:
                name="short";
                break;
            case SHORT_VECTOR:
                name="short[]";
                break;
            case SHORT_MATRIX:
                name="short[][]";
                break;
            case INT:
                name="int";
                break;
            case INT_VECTOR:
                name="int[]";
                break;
            case INT_MATRIX:
                name="int[][]";
                break;
            case LONG:
                name="long";
                break;
            case LONG_VECTOR:
                name="long[]";
                break;
            case LONG_MATRIX:
                name="long[][]";
                break;
            case FLOAT:
                name="float";
                break;
            case FLOAT_VECTOR:
                name="float[]";
                break;
            case FLOAT_MATRIX:
                name="float[][]";
                break;
            case DOUBLE:
                name="double";
                break;
            case DOUBLE_VECTOR:
                name="double[]";
                break;
            case DOUBLE_MATRIX:
                name="double[][]";
                break;
            case COMPLEX:
                name="complex";
                break;
            case COMPLEX_VECTOR:
                name="complex[]";
                break;
            case COMPLEX_MATRIX:
                name="complex[][]";
                break;
            case OBJECT:
                name="Object";
                break;
            case OBJECT_VECTOR:
                name="Object[]";
                break;
            case OBJECT_MATRIX:
                name="Object[][]";
                break;
            case STRING:
                name="String";
                break;
            case STRING_VECTOR:
                name="String[]";
                break;
            case STRING_MATRIX:
                name="String[][]";
                break;
            case ANYTHING:
                name="anything";
                break;
            case ANY_NUMBER:
                name="any_number";
                break;
            case ANY_NUMBER_VECTOR:
                name="any_number[]";
                break;
            case ANY_NUMBER_MATRIX:
                name="any_number[][]";
                break;
            case MULTI_TYPE:
                name="multi_type";
                break;
            case NO_TYPE:
                name="no_type";
                break;
            default:
                break;
        }
        return name;
    }
    public static int getID(String name){
        
        int id = 0;
        if(name.equalsIgnoreCase("byte"))
            id = BYTE;
        else if(name.equalsIgnoreCase("byte[]"))
            id = BYTE_VECTOR;
        else if(name.equalsIgnoreCase("byte[][]"))
            id = BYTE_MATRIX;
        else if(name.equalsIgnoreCase("short"))
            id = SHORT;
        else if(name.equalsIgnoreCase("short[]"))
            id = SHORT_VECTOR;
        else if(name.equalsIgnoreCase("short[][]"))
            id = SHORT_MATRIX;
        else if(name.equalsIgnoreCase("int"))
            id = INT;
        else if(name.equalsIgnoreCase("int[]"))
            id = INT_VECTOR;
        else if(name.equalsIgnoreCase("int[][]"))
            id = INT_MATRIX;
        else if(name.equalsIgnoreCase("float"))
            id = FLOAT;
        else if(name.equalsIgnoreCase("float[]"))
            id = FLOAT_VECTOR;
        else if(name.equalsIgnoreCase("float[][]"))
            id = FLOAT_MATRIX;
        else if(name.equalsIgnoreCase("long"))
            id = LONG;
        else if(name.equalsIgnoreCase("long[]"))
            id = LONG_VECTOR;
        else if(name.equalsIgnoreCase("long[][]"))
            id = LONG_MATRIX;
        else if(name.equalsIgnoreCase("double"))
            id = DOUBLE;
        else if(name.equalsIgnoreCase("double[]"))
            id = DOUBLE_VECTOR;
        else if(name.equalsIgnoreCase("double[][]"))
            id = DOUBLE_MATRIX;
        else if(name.equalsIgnoreCase("complex"))
            id = COMPLEX;
        else if(name.equalsIgnoreCase("complex[]"))
            id = COMPLEX_VECTOR;
        else if(name.equalsIgnoreCase("complex[][]"))
            id = COMPLEX_MATRIX;
        else if(name.equalsIgnoreCase("string"))
            id = STRING;
        else if(name.equalsIgnoreCase("string[]"))
            id = STRING_VECTOR;
        else if(name.equalsIgnoreCase("string[][]"))
            id = STRING_MATRIX;        
        else if(name.equalsIgnoreCase("object"))
            id = OBJECT;
        else if(name.equalsIgnoreCase("object[]"))
            id = OBJECT_VECTOR;
        else if(name.equalsIgnoreCase("object[][]"))
            id = OBJECT_MATRIX;        
        return id;
    }
}
