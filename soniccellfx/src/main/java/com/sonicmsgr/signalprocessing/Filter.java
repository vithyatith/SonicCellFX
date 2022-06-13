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
package com.sonicmsgr.signalprocessing;

public class Filter {
    public static void main(String[] args) {

         float[] x = new float[10];
         for(int i=0; i<10; i++){
        	 x[i]=i+1;
         }
         float[] y_out = new float[10];
         
         float[] b = new float[3];
         b[0]=1;
         b[1]=2;
         b[2]=3;
         float[] a = new float[1];
         a[0]=1;
         
         filter(b,a,x,y_out);
         
         for(int i=0; i<10; i++){
        	 System.out.println(y_out[i]);
         }
         
    }
    public static float filter(float[] b, float[] a, float[] x, int x_pos, int x_length, float[] y_out, int y_pos) {
        int   lenB = b.length;
        int   lenA = a.length;
       // int   lenX = x.length;
        float sumA = 0;
        float sumB = 0;
        
        float maxValue= Float.NEGATIVE_INFINITY;
        
        float f = 0;

        for (int i = 0; i < x_length; i++) {
            sumA = 0;
            sumB = 0;

            int dif    = i - lenB;
            int tmpLen = lenB;

            if (dif < 0) {
                tmpLen = lenB + dif + 1;
            }

            for (int k = 0; k < tmpLen; k++) {
                sumB = sumB + b[k] * x[i - k+x_pos];
            }

            dif    = i - lenA;
            tmpLen = lenA;

            if (dif < 0) {
                tmpLen = lenA + dif + 1;
            }

            for (int k = 1; k < tmpLen; k++) {
                sumA = sumA + a[k] * y_out[i - k+y_pos];
            }

            f = (sumB - sumA)/a[0];
            y_out[i+y_pos] = f;
            
            if(f>maxValue){
            	maxValue = f;
            }
            
        }
        return maxValue;
    }
    public static float filter(float[] b, float[] a, float[] x, float[] y_out) {
        int   lenB = b.length;
        int   lenA = a.length;
        int   lenX = x.length;
        float sumA = 0;
        float sumB = 0;
        
        float maxValue= Float.NEGATIVE_INFINITY;
        
        float f = 0;

        for (int i = 0; i < lenX; i++) {
            sumA = 0;
            sumB = 0;

            int dif    = i - lenB;
            int tmpLen = lenB;

            if (dif < 0) {
                tmpLen = lenB + dif + 1;
            }

            for (int k = 0; k < tmpLen; k++) {
                sumB = sumB + b[k] * x[i - k];
            }

            dif    = i - lenA;
            tmpLen = lenA;

            if (dif < 0) {
                tmpLen = lenA + dif + 1;
            }

            for (int k = 1; k < tmpLen; k++) {
                sumA = sumA + a[k] * y_out[i - k];
            }

            f = (sumB - sumA)/a[0];
            y_out[i] = f;
            
            if(f>maxValue){
            	maxValue = f;
            }
            
        }
        return maxValue;
    }
}