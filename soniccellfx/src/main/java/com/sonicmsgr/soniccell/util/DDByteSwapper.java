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
package com.sonicmsgr.soniccell.util;


import java.io.*;

/**
 *
 * @author anakinw
 *//*
 * (C) 2004 - Geotechnical Software Services
 *
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.
 */

/**
 * Utility class for doing byte swapping (i.e. conversion between
 * little-endian and big-endian representations) of different data types.
 * Byte swapping is typically used when data is read from a stream
 * delivered by a system of different endian type as the present one.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class DDByteSwapper implements Serializable {

    /**
     * Byte swap a single short value.
     *
     * @param value  Value to byte swap.
     * @return       Byte swapped representation.
     */
    private byte byte_vec[] = null;
    private int m = 0;
    private int n = 0;
    private int len = 0;
    private boolean leBool = true;
    private int byte_size = 0;

    public DDByteSwapper() {
    }

    public void setByteVector(byte b[]) {
        b = byte_vec;
    }

    public byte[] get() {
        return byte_vec;
    }

    public void byteToVec(byte b) {
        if (byte_vec == null) {
            byte_size = 1;
            byte_vec = new byte[byte_size];
        }
        byte_vec[0] = b;

    }

    public void byteMatToVec(byte b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            byte_vec = new byte[n * m];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                byte_vec[i * n + j] = b[m][n];
            }
        }
    }

    public void reset() {
        byte_vec = null;
        m = 0;
        n = 0;
        len = 0;
        leBool = true;
        byte_size = 0;

    }

    public void setLittleEndianBool(boolean _b) {
        leBool = _b;
    }

    public void swap(short b) {
        swap(b, 0);
    }

    public void swap(short b, int index) {
        if (byte_vec == null) {
            byte_size = 2;
            byte_vec = new byte[byte_size];
        }
        if (leBool) {
            byte_vec[index] = (byte) b;
            byte_vec[index + 1] = (byte) (b >> 8);
        } else {
            byte_vec[index] = (byte) (b >> 8);
            byte_vec[index + 1] = (byte) (b);
        }
    }

    public void swap(Byte b) {
        swap(b.byteValue());
    }

    public void swap(Short s) {
        swap(s.shortValue());
    }

    public void swap(Long l) {
        swap(l.longValue());
    }

    public void swap(Float f) {
        swap(f.floatValue());
    }

    public void swap(Integer i) {
        swap(i.intValue());
    }

    public void swap(Double d) {
        swap(d.doubleValue());
    }

    public void swap(short b[]) {
        if (byte_vec == null) {
            len = b.length;
            byte_size = 2;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < len; i++) {
            short v = b[i];
            swap(v, i * byte_size);
        }
    }

    public void swap(short b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            len = n * m;
            byte_size = 2;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                short v = b[i][j];
                swap(v, i * n + j * byte_size);
            }
        }
    }

    public void swap(int b) {
        swap(b, 0);
    }

    public void swap(int v, int index) {
        if (byte_vec == null) {
            byte_size = 4;
            byte_vec = new byte[byte_size];
        }
        if (leBool) {
            byte_vec[index + 0] = (byte) v;
            byte_vec[index + 1] = (byte) (v >> 8);
            byte_vec[index + 2] = (byte) (v >> 16);
            byte_vec[index + 3] = (byte) (v >> 24);
        } else {
            byte_vec[index + 0] = (byte) (v >> 24);
            byte_vec[index + 1] = (byte) ((v << 8) >> 24);
            byte_vec[index + 2] = (byte) ((v << 16) >> 24);
            byte_vec[index + 3] = (byte) ((v << 24) >> 24);
        }
    }

    public void swap(int b[]) {
        if (byte_vec == null) {
            len = b.length;
            byte_size = 4;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < len; i++) {
            int v = b[i];
            swap(v, i * byte_size);
        }
    }

    public void swap(int b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            len = n * m;
            byte_size = 4;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int v = b[i][j];
                swap(v, i * n + j * byte_size);
            }
        }
    }

    public void swap(float b) {
        swap(b, 0);
    }

    public void swap(float f, int index) {
        int v = Float.floatToIntBits(f);
        swap(v, index);
    }

    public void swap(float b[]) {
        if (byte_vec == null) {
            len = b.length;
            byte_size = 4;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < len; i++) {
            float v = b[i];
            swap(v, i * byte_size);
        }
    }

    public void swap(float b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            len = n * m;
            byte_size = 4;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                float v = b[i][j];
                swap(v, i * n + j * byte_size);
            }
        }
    }

    public void swap(long b) {
        swap(b, 0);
    }

    public void swap(long v, int index) {
        if (byte_vec == null) {
            byte_size = 8;
            byte_vec = new byte[byte_size];
        }

        if (leBool) {
            byte_vec[index + 0] = (byte) v;
            byte_vec[index + 1] = (byte) (v >> 8);
            byte_vec[index + 2] = (byte) (v >> 16);
            byte_vec[index + 3] = (byte) (v >> 24);
            byte_vec[index + 4] = (byte) (v >> 32);
            byte_vec[index + 5] = (byte) (v >> 40);
            byte_vec[index + 6] = (byte) (v >> 48);
            byte_vec[index + 7] = (byte) (v >> 56);
        } else {
            byte_vec[index + 0] = (byte) (v >> 56);
            byte_vec[index + 1] = (byte) ((v << 8) >> 56);
            byte_vec[index + 2] = (byte) ((v << 16) >> 56);
            byte_vec[index + 3] = (byte) ((v << 24) >> 56);
            byte_vec[index + 4] = (byte) ((v << 32) >> 56);
            byte_vec[index + 5] = (byte) ((v << 40) >> 56);
            byte_vec[index + 6] = (byte) ((v << 48) >> 56);
            byte_vec[index + 7] = (byte) ((v << 56) >> 56);
        }
    }

    public void swap(long b[]) {
        if (byte_vec == null) {
            len = b.length;
            byte_size = 8;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < len; i++) {
            long v = b[i];
            swap(v, i * byte_size);
        }
    }

    public void swap(long b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            len = n * m;
            byte_size = 8;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                long v = b[i][j];
                swap(v, i * n + j * byte_size);
            }
        }
    }

    public void swap(double b) {
        swap(b, 0);
    }

    public void swap(double f, int index) {
        if (byte_vec == null) {
            byte_size = 8;
            byte_vec = new byte[byte_size];
        }
        long v = Double.doubleToLongBits(f);
        swap(v, index);
    }

    public void swap(double b[]) {
        if (byte_vec == null) {
            len = b.length;
            byte_size = 8;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < len; i++) {
            double v = b[i];
            swap(v, i * byte_size);
        }
    }

    public void swap(double b[][]) {
        if (byte_vec == null) {
            m = b.length;
            n = b[0].length;
            len = n * m;
            byte_size = 8;
            byte_vec = new byte[len * byte_size];
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                double v = b[i][j];
                swap(v, i * n + j * byte_size);
            }
        }
    }
}
