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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static boolean writeTextToFile(String filename, String msg) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(msg);
            // Close the output stream

            out.close();
            fstream.close();
            return true;
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
    public static void deleteDataFileInState(int state, String folder) {
		File f = new File(folder);
		String [] tempList = f.list();
		if(tempList != null) {
			for(int a = 0; a < tempList.length; a++) {
				if(Integer.parseInt(tempList[a].substring(0,1)) == state) {
					f = new File(folder + "/" + tempList[a]);
					if(f.exists())
						f.delete();
				}	
			}
		}
    }
    public static byte[] readBinaryFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    public static void writeBinaryFile(byte[] data, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    public static String readTextFile(String filename) {
        String encoding = "UTF-8";
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        try {

            Scanner scanner = new Scanner(new FileInputStream(filename),
                    encoding);
            try {
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine() + NL);
                }
            } finally {
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return text.toString();

    }

    public static String createDateFileName() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(cal.getTime());
        return currentDateAndTime;
    }

    public static String getDateFormatedNameReverse(String formatedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = "";
        try {
            Date date = sdf.parse(formatedDate);
            dateTime = date.toString();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateTime;
    }

    public static void zip(String[] files, String zipFile) throws IOException {
        int BUFFER_SIZE = 1024;
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte[] data = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        int size;
        int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream out = new FileOutputStream(path, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {

        }
    }

    public static void saveFileAsFloat(String outputFileName, float[] data) {
        saveFileAsFloat(outputFileName, data, 0);
    }

    public static void saveFileAsFloat(String outputFileName, float[] data, boolean littleEndian) {
        int method = 2;
        if (littleEndian) {
            method = 1;
        }
        saveFileAsFloat(outputFileName, data, method);
    }

    private static void saveFileAsFloat(String outputFileName, float[] data, int method) {
        try {

            File file = new File(outputFileName);

            FileChannel fc = new FileOutputStream(file, false).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);

            if (method == 1) {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            } else if (method == 2) {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            } else {
                byteBuffer.order(ByteOrder.nativeOrder());
            }

            FloatBuffer fb = byteBuffer.asFloatBuffer(); // create a floating
            // point buffer from
            // the ByteBuffer
            fb.put(data); // add the coordinates to the FloatBuffer
            fb.position(0);

            fc.write(byteBuffer);
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveFileAsBinary(String outputFileName, byte[] data) {
        try {

            File file = new File(outputFileName);

            FileChannel fc = new FileOutputStream(file, false).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            fc.write(byteBuffer);
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float[] loadFileFloatToFloat(String inputFileName, boolean littleEndian) {
        float[] data_float = null;
        try {

            File file = new File(inputFileName);

            long totalBytes = 0;

            if (file.exists()) {
                totalBytes = file.length();
            }

            // data
            data_float = new float[(int) (totalBytes / 4)];

            FileChannel fc = new FileInputStream(file).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) (totalBytes));
            if (littleEndian) {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            } else {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            }

            int nRead = 0;

            while ((nRead = fc.read(byteBuffer)) > 0) { // Rewind
                int i = 0;
                byteBuffer.rewind();
                while (byteBuffer.hasRemaining()) {
                    float v = byteBuffer.getFloat();
                    data_float[i] = v;
                    i++;
                }
            }
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data_float;
    }

    public static float[] loadFileAsShortToFloat(String inputFileName, boolean littleEndian) {
        float[] data_float = null;
        try {

            File file = new File(inputFileName);

            long totalBytes = 0;

            if (file.exists()) {
                totalBytes = file.length();
            }

            // data
            data_float = new float[(int) (totalBytes / 2)];

            FileChannel fc = new FileInputStream(file).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) (totalBytes));
            if (littleEndian) {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            } else {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            }

            int nRead = 0;

            while ((nRead = fc.read(byteBuffer)) > 0) { // Rewind
                int i = 0;
                byteBuffer.rewind();
                while (byteBuffer.hasRemaining()) {
                    short v = byteBuffer.getShort();
                    data_float[i] = v;
                    i++;
                }
            }
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data_float;
    }

    public static float[] loadFileAsTextToFloat(String inputFileName) {
        String encoding = "UTF-8";
        ArrayList<Float> al = new ArrayList<Float>();
        float[] data = null;
        try {

            Scanner scanner = new Scanner(new FileInputStream(inputFileName), encoding);
            try {
                while (scanner.hasNextLine()) {
                    String s = scanner.nextLine();

                    try {
                        float v = Float.parseFloat(s);
                        al.add(v);
                    } catch (Exception ex) {
                        al.add(0f);

                    }
                }

                int len = al.size();
                data = new float[len];

                for (int i = 0; i < len; i++) {

                    Float f = al.get(i);
                    data[i] = f;
                }

                al.clear();

                al = null;

            } finally {
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // return e.getMessage();
        }

        return data;
    }

    public static void saveFileAsShort(String outputFileName, short[] data) {
        try {
            // private String OUTPUT_FOLDER =
            // Environment.getExternalStorageDirectory()
            // String inputFile = OUTPUT_FOLDER + "/" + FILE_NAME;

            File file = new File(outputFileName);

            FileChannel fc = new FileOutputStream(file, false).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 2);

            byteBuffer.order(ByteOrder.nativeOrder());

            ShortBuffer sb = byteBuffer.asShortBuffer(); // create a floating
            // point buffer from
            // the ByteBuffer
            sb.put(data); // add the coordinates to the FloatBuffer
            sb.position(0);

            fc.write(byteBuffer);
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveFileAsShort(String outputFileName, short[] data, boolean littleEndian) {
        try {
            // private String OUTPUT_FOLDER =
            // Environment.getExternalStorageDirectory()
            // String inputFile = OUTPUT_FOLDER + "/" + FILE_NAME;

            File file = new File(outputFileName);

            FileChannel fc = new FileOutputStream(file, false).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 2);
            if (littleEndian) {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            } else {
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
            }

            ShortBuffer sb = byteBuffer.asShortBuffer(); // create a floating
            // point buffer from
            // the ByteBuffer
            sb.put(data); // add the coordinates to the FloatBuffer
            sb.position(0);

            fc.write(byteBuffer);
            fc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> listAllFilesFromDirectory(String directoryName) {
//        File directory = new File(directoryName);
//
//        List<File> resultList = new ArrayList<File>();
//
//        // get all the files from a directory
//        File[] fList = directory.listFiles();
//        resultList.addAll(Arrays.asList(fList));
//        for (File file : fList) {
//            if (file.isFile()) {
//                // System.out.println(file.getAbsolutePath());
//            } else if (file.isDirectory()) {
//                resultList.addAll(listAllFilesFromDirectory(file.getAbsolutePath()));
//            }
//        }

        return listAllFilesFromDirectory(directoryName, "*");
    }

    public static List<File> listAllFilesFromDirectory(String directoryName, final String extension) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (extension.equalsIgnoreCase("") || extension.equalsIgnoreCase("*")) {
                    return true;
                } else {
                    return name.toLowerCase().endsWith(extension);
                }
            }
        });
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                // System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listAllFilesFromDirectory(file.getAbsolutePath(), extension));
            }
        }

        return resultList;
    }

    public static List<String> listAllFilesFromDirectoryString(String directoryName) {

        return listAllFilesFromDirectoryString(directoryName, "*");
    }

    public static List<String> listAllFilesFromDirectoryString(String directoryName, String extension) {

        List<String> list = new ArrayList<String>();

        List<File> resultList = listAllFilesFromDirectory(directoryName, extension);
        int len = resultList.size();
        for (int i = 0; i < len; i++) {
            list.add(resultList.get(i).toString());
        }

        return list;
    }

    public static String[] listAllFilesFromDirectoryStringArray(String directoryName) {

        return listAllFilesFromDirectoryStringArray(directoryName, "*");
    }

    public static String[] listAllFilesFromDirectoryStringArray(String directoryName, String extension) {

        List<File> resultList = listAllFilesFromDirectory(directoryName, extension);
        String[] results = new String[resultList.size()];
        int len = resultList.size();
        for (int i = 0; i < len; i++) {
            results[i] = resultList.get(i).toString();
        }

        return results;
    }

    public static void deleteAllFilesInFolder(String folderName) {
        File dir = new File(folderName);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }
    }

    public static void purgeDirectory(File dir) {
        if (dir == null) {
            return;
        }
        if (!dir.exists()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }

    public static void purgeDirectory(String folder) {
        File dir = new File(folder);

        purgeDirectory(dir);
    }

    public static String[] searchFileContainingWordAndReturnFileName(String director, String wordContain) {

        String[] fileList = new String[0];

        ArrayList<String> al = new ArrayList<String>();
        File maindir = new File(director);
        File[] files = maindir.listFiles();
        if (files != null) {
            recursiveFileSearch(files, 0, 0, al, wordContain);
        }
        if (al.size() > 0) {
            fileList = new String[al.size()];
            for (int i = 0; i < al.size(); i++) {
                fileList[i] = al.get(i);
            }
        }

        return fileList;
    }

    private static void recursiveFileSearch(File[] arr, int index, int level, ArrayList<String> al,
            String containWord) {
        // terminate condition
        if (index == arr.length) {
            return;
        }
        if (arr[index].isFile()) {
            String filename = arr[index].getAbsolutePath();
            if (filename.indexOf(containWord) >= 0) {
                // System.out.println(filename);
                al.add(filename);

            }
        } // for sub-directories
        else if (arr[index].isDirectory()) {
            // System.out.println("[" + arr[index].getName() + "]");

            // recursion for sub-directories
            recursiveFileSearch(arr[index].listFiles(), 0, level + 1, al, containWord);
        }

        // recursion for main directory
        recursiveFileSearch(arr, ++index, level, al, containWord);
    }

    public static String readTextFileAsString(String fileName) {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " not found");
        }
        return data;
    }

    public static boolean copyDir(String src, String dest, boolean overwrite) {
        try {

            File f = new File(dest);
            if (!f.exists()) {
                boolean b = f.mkdir();
                if (b == false) {
                    return false;
                }
            }

            Files.walk(Paths.get(src)).forEach(a -> {
                Path b = Paths.get(dest, a.toString().substring(src.length()));
                try {
                    if (!a.toString().equals(src)) {
                        Files.copy(a, b, overwrite ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{});
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        } catch (IOException e) {
            //permission issue
            e.printStackTrace();

        }
        return false;
    }

    ////////////////
//	public static boolean writeTextToFile(String text, String filename) {
//		FileWriter fr;
//		try {
//			fr = new FileWriter(filename);
//
//			// your file extention (".txt" in this case)
//			fr.write(text); // Warning: this will REPLACE your old file content!
//			fr.close();
//			return true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // After '.' write
//		return false;
//	}
    public static void writeBytesToFile(String filename, byte[] data) {
        try {

            OutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(data);
            outputStream.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }

    public static void readShortBinaryFile(String filename, int dataSize, boolean littEndian, CallBack callBack) {
        if (callBack == null) {
            return;
        }

        boolean isSystemLittleEndianBool = !ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);

        File aFile = new File(filename);
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(aFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
        FileChannel inChannel = inFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(2 * dataSize);

        if (littEndian) {
            buf.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            buf.order(ByteOrder.BIG_ENDIAN);
        }
        short[] data = new short[dataSize];
        try {
            while (inChannel.read(buf) != -1) {
                buf.flip().asShortBuffer().get(data);
                callBack.onReceived(data);
                buf.clear();
            }
            inFile.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void readByteBinaryFile(String filename, int bufferSize, int outputByteSize, int nTime, int sleepInMilliSec, boolean paddLastBytesBool, boolean remainingBytesBool, CallBack callBack) {
        if (callBack == null) {
            return;
        }

        File aFile = new File(filename);
        if (aFile.isDirectory()) {
            System.out.println(filename + " is a directory, skipping.");
            return;
        }
        long fileSize = aFile.length();
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(aFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
        FileChannel inChannel = inFile.getChannel();

        if (bufferSize > outputByteSize) {
            bufferSize = outputByteSize;
        }

        ByteBuffer buf = ByteBuffer.allocate(bufferSize);

        byte[] data = new byte[outputByteSize];

        long track = 1;

        int trackIndex = 0;
        try {
            try {
                int nRead = 0;
                while ((nRead = inChannel.read(buf)) != -1) {

                    if (!callBack.continueProcessing()) {
                        break;
                    }

                    buf.rewind();

                    for (int i = 0; i < nRead; i++) {
                        data[trackIndex] = buf.get(i);
                        trackIndex++;
                        if (trackIndex >= outputByteSize) {

                            try {
                                Thread.sleep(sleepInMilliSec);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();

                                break;
                            }

                            callBack.onReceived(data);
                            trackIndex = 0;

                            if (nTime > 0) {
                                track++;
                                if (track > nTime) {
                                    break;
                                }
                            }

                        }
                    }
                    buf.rewind();
                    buf.clear();

                }

                if (remainingBytesBool) {
                    if (paddLastBytesBool) {
                        callBack.onReceived(data);
                    } else {

                        byte[] tmpByes = new byte[trackIndex];
                        for (int i = 0; i < trackIndex; i++) {
                            tmpByes[i] = data[i];
                        }
                        callBack.onReceived(tmpByes);
                    }
                }

            } catch (java.nio.channels.ClosedByInterruptException e) {
                e.printStackTrace();
                System.out.println(e.getLocalizedMessage());
            }
            inFile.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void readByteBinaryFile(String filename, int byteSize, int nTime, CallBack callBack) {
        if (callBack == null) {
            return;
        }

        File aFile = new File(filename);
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(aFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
        FileChannel inChannel = inFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(byteSize);

        byte[] data = new byte[byteSize];

        if (nTime < 0) {
            nTime = (int) (aFile.length() / byteSize);
        }
        long track = 1;

        try {
            while (inChannel.read(buf) != -1) {
                buf.flip().get(data);
                callBack.onReceived(data);
                track++;
                buf.clear();
                if (track > nTime) {
                    break;
                }
            }
            inFile.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public interface CallBack {

        void onReceived(Object data);

        boolean continueProcessing();

    }

}
