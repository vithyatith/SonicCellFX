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
package com.component.io;

import java.io.*;

public class UnsynchronizedBufferedWriter extends Writer {
    
    private final static int CAPACITY = 8192;
    
    private char[]  buffer = new char[CAPACITY];
    private int     position = 0;
    private Writer  out;
    private boolean closed = false;
    
    public UnsynchronizedBufferedWriter(Writer out) {
        this.out = out;
    }
    
    public void write(char[] text, int offset, int length) throws IOException {
        checkClosed();
        while (length > 0) {
            int n = Math.min(CAPACITY - position, length);
            System.arraycopy(text, offset, buffer, position, n);
            position += n;
            offset += n;
            length -= n;
            if (position >= CAPACITY) flushInternal();
        }
    }
    
    public void write(String s) throws IOException {
        write(s, 0, s.length());
    }
    
    public void write(String s, int offset, int length) throws IOException {
        checkClosed();
        while (length > 0) {
            int n = Math.min(CAPACITY - position, length);
            s.getChars(offset, offset + n, buffer, position);
            position += n;
            offset += n;
            length -= n;
            if (position >= CAPACITY) flushInternal();
        }
    }
    
    public void write(int c) throws IOException {
        checkClosed();
        if (position >= CAPACITY) flushInternal();
        buffer[position] = (char) c;
        position++;
    }
    
    public void flush() throws IOException {
        flushInternal();
        out.flush();
    }
    
    private void flushInternal() throws IOException {
        if (position != 0) {
            out.write(buffer, 0, position);
            position = 0;
        }
    }
    
    public void close() throws IOException {
        closed = true;
        this.flush();
        out.close();
    }
    
    private void checkClosed() throws IOException {
        if (closed) throw new IOException("Writer is closed");
    }
}
