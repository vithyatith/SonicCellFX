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

import com.component.graph2D.Graph2DComponent;
import com.component.io.OutputComponent;
import com.component.sound.JsonToMP3PlayerComponent;
import com.component.sound.SoundBytePlayerFXComponent;
import com.component.sound.SoundCaptureComponent;
import com.component.web.ServletComponent;
import com.fx.TextConsoleFX;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import component.basic.*;
import component.crypto.*;
import component.file.*;
import component.math.*;
import component.string.*;
import component.typeconverter.HexByteArrayToShortArrayInterleaveComponent;
import component.typeconverter.TypeArrayConverterComponent;
import component.web.*;
/**
 *
 * @author Vithya Tith
 */
public class ComponentListJson {

    //public static final String json = "";
    public ComponentListJson() {

    }

    public static ComponentDBList getComponentDBList() {
        ComponentDBList componentDB = new ComponentDBList();

        componentDB.add("Byte", new ComponentItem(AppendToByteArrayComponent.class.getCanonicalName(), "AppendBytes"));

        componentDB.add("Condition", new ComponentItem("component.condition.IfConditionComponent", "IF"));
        componentDB.add("String", new ComponentItem(TextConsoleFX.class.getCanonicalName(), "TextConsole"));

        componentDB.add("Filter", new ComponentItem(FilterNum.class.getCanonicalName(), "FilterNum"));

        componentDB.add("Cryto", new ComponentItem(CrytoEncryptTextComponent.class.getCanonicalName(), "TxtEncrypt"));
        componentDB.add("Cryto", new ComponentItem(CrytoDecryptTextComponent.class.getCanonicalName(), "TxtDecrypt"));

        componentDB.add("Cryto", new ComponentItem(CrytoKeyIVEncryptTextComponent.class.getCanonicalName(), "TxtKeyIVEncrypt"));
        componentDB.add("Cryto", new ComponentItem(CrytoKeyIVDecryptTextComponent.class.getCanonicalName(), "TxtKeyIVDecrypt"));

        componentDB.add("Cryto", new ComponentItem(RSACreatorComponent.class.getCanonicalName(), "RSACreator"));
        componentDB.add("Cryto", new ComponentItem("component.crypto.EncrypteTextRSAComponent", "EncryptTextRSA"));
        componentDB.add("Cryto", new ComponentItem("component.crypto.DecryptTextRSAComponent", "DecryptTextRSA"));

        componentDB.add("Cryto", new ComponentItem(CheckSumFileComponent.class.getCanonicalName(), "CheckSumFile"));
        componentDB.add("Cryto", new ComponentItem(CheckSumTextComponent.class.getCanonicalName(), "CheckSumText"));
        componentDB.add("Cryto", new ComponentItem(FileEncryptComponent.class.getCanonicalName(), "FileEncrypt"));
        componentDB.add("Cryto", new ComponentItem(FileDecryptComponent.class.getCanonicalName(), "FileDecrypt"));

        componentDB.add("File", new ComponentItem(FileReadFullyTextComponent.class.getCanonicalName(), "ReadTxtFull"));
        componentDB.add("File", new ComponentItem(FileReadLineTextComponent.class.getCanonicalName(), "ReadTxtLine"));
        componentDB.add("File", new ComponentItem("component.file.FileWriterTextComponent", "WriteTextFile"));
        componentDB.add("File", new ComponentItem(WriteBinaryFullyComponent.class.getCanonicalName(), "WriteFullyBinaryFile"));
        componentDB.add("File", new ComponentItem(WriteBinaryComponent.class.getCanonicalName(), "WriteBinaryFile"));
        componentDB.add("File", new ComponentItem("component.file.FileReaderFullyBinaryComponent", "ReadFullyBinaryFile"));
        componentDB.add("File", new ComponentItem("component.file.FileTailComponent", "Tail"));
        componentDB.add("File", new ComponentItem("component.file.FileToJsonComponent1", "File2Json"));
        componentDB.add("File", new ComponentItem("component.file.Json2FileComponent", "Json2File"));
        componentDB.add("File", new ComponentItem(FileReaderBinaryComponent.class.getCanonicalName(), "ReadBinary"));
        componentDB.add("File", new ComponentItem(ListFilesInDirComponent.class.getCanonicalName(), "ListFiles"));
        componentDB.add("File", new ComponentItem(FolderMonitorComponent.class.getCanonicalName(), "FolderMon"));

        componentDB.add("Image", new ComponentItem("component.image.ImagePopupComponent", "Json2Image"));

        //  comDb.add("Graph", new ComponentItem(GraphGramComponent.class.getCanonicalName(), "Gram"));
        // comDb.add("Graph", new ComponentItem(LineGraphComponent.class.getCanonicalName(), "LineGraph"));
        componentDB.add("Graph", new ComponentItem(Graph2DComponent.class.getCanonicalName(), "Graph2D"));

        componentDB.add("Signal Processing", new ComponentItem(GaussianComponent.class.getCanonicalName(), "Gaussian"));
        componentDB.add("Signal Processing", new ComponentItem(AverageComponent.class.getCanonicalName(), "Average"));
        componentDB.add("Signal Processing", new ComponentItem(FFTComponent.class.getCanonicalName(), "FFT"));
        componentDB.add("Signal Processing", new ComponentItem(ContinousWaveToneComponent.class.getCanonicalName(), "ContinousWaveTone"));
        componentDB.add("Signal Processing", new ComponentItem(ComplexAbsComponent.class.getCanonicalName(), "ComplexAbs"));
        componentDB.add("Signal Processing", new ComponentItem(VectorOperatorComponent.class.getCanonicalName(), "VectorOperator"));

        componentDB.add("Number", new ComponentItem("component.basic.NumberDoubleCom", "Double"));
        componentDB.add("Number", new ComponentItem("component.basic.NumberIntegerCom", "Integer"));
        componentDB.add("Number", new ComponentItem("component.basic.MathOperatorCom", "MathOperator"));
        componentDB.add("Number", new ComponentItem("component.basic.NumberFloatComponent", "Float"));
        componentDB.add("Number", new ComponentItem("component.basic.StringToLongComponent", "StringToLong"));
        componentDB.add("Number", new ComponentItem(NumberShortComponent.class.getCanonicalName(), "Short"));
        componentDB.add("Number", new ComponentItem(NumberLongComponent.class.getCanonicalName(), "Long"));
        componentDB.add("Number", new ComponentItem(NumberByteComponent.class.getCanonicalName(), "Byte"));

        componentDB.add("Network", new ComponentItem("component.network.TcpClientComponent", "TcpTextClient"));
        componentDB.add("Network", new ComponentItem("component.network.TcpTextServerComponent", "TcpTextServer"));

        componentDB.add("IO", new ComponentItem(OutputComponent.class.getCanonicalName(), "Output"));

        componentDB.add("Print", new ComponentItem("component.basic.Print", "Print"));
        componentDB.add("Print", new ComponentItem("com.sonicmsgr.soniccell.component.fx.PrintGuiFX", "PrintFX"));

        //////////////////

        ////////////////////
//        comDb.add("MsgQue", new ComponentItem(MsgQueStartComponent.class.getCanonicalName(), "MsgStart"));
//        comDb.add("MsgQue", new ComponentItem(MsgQueStopComponent.class.getCanonicalName(), "MsgStop"));

        componentDB.add("System", new ComponentItem("component.system.CommandExecComponent", "CommandLineExec"));

        componentDB.add("Sound", new ComponentItem(SoundBytePlayerFXComponent.class.getCanonicalName(), "WavPlayer"));
        componentDB.add("Sound", new ComponentItem(JsonToMP3PlayerComponent.class.getCanonicalName(), "Json2MP3Player"));

        componentDB.add("Sound", new ComponentItem(SoundCaptureComponent.class.getCanonicalName(), "SoundCapture"));

        componentDB.add("String", new ComponentItem(StringComponent.class.getCanonicalName(), "String"));
        componentDB.add("String", new ComponentItem(StrContainComponent.class.getCanonicalName(), "StrContain"));
        componentDB.add("String", new ComponentItem(StrFindReplaceComponent.class.getCanonicalName(), "StrFindReplace"));
        componentDB.add("String", new ComponentItem(StringToLongComponent.class.getCanonicalName(), "StrtoLong"));
        componentDB.add("String", new ComponentItem(StrArrayToStrComponent.class.getCanonicalName(), "StrArrayToString"));
        componentDB.add("String", new ComponentItem(SubStringStringComponent.class.getCanonicalName(), "SubStrStr"));
        componentDB.add("String", new ComponentItem(SubStringStringComponent.class.getCanonicalName(), "SubStrStr"));
        componentDB.add("String", new ComponentItem(StrAppendComponent.class.getCanonicalName(), "Append"));

        componentDB.add("Type", new ComponentItem(StringToTypeConverterComponent.class.getCanonicalName(), "StrToTypeConverter"));

        componentDB.add("Type", new ComponentItem(TypeCastConverterComponent.class.getCanonicalName(), "TypeCast"));
        componentDB.add("Type", new ComponentItem(TypeArrayConverterComponent.class.getCanonicalName(), "TypeConverter"));
        componentDB.add("Type", new ComponentItem(HexByteArrayToShortArrayInterleaveComponent.class.getCanonicalName(), "Bytes2ShortInterleave"));

        componentDB.add("Web", new ComponentItem(WebServiceJsonComponent.class.getCanonicalName(), "WebServiceJson"));
        componentDB.add("Web", new ComponentItem(WebCallComopnent.class.getCanonicalName(), "WebCall"));
        componentDB.add("Web", new ComponentItem(HttpPostJsonComponent.class.getCanonicalName(), "PostJson"));
        componentDB.add("Web", new ComponentItem(ServletComponent.class.getCanonicalName(), "Servlet"));
        componentDB.add("Web", new ComponentItem(JsonExtractComopnent.class.getCanonicalName(), "JsonExtract"));
        componentDB.add("Web", new ComponentItem(HttpReqResComponent.class.getCanonicalName(), "HttpReqRes"));
        componentDB.add("Web", new ComponentItem(HttpReqResAdvanceComponent.class.getCanonicalName(), "HttpReqResAd"));

        return componentDB;
    }

    public static String getDefaultJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(getComponentDBList());
        return json;
    }
}
