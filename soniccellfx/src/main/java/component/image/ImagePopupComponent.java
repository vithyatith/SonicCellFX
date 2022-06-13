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
package component.image;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.JsonUtil;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//import javax.print.attribute.standard.Media;
   
/**
 *
 * @author yada
 */
public class ImagePopupComponent extends ComponentAbstraction {

    private String savePath = "path";
    private boolean processingBool = false;
    private String data = "";

    public ImagePopupComponent() {
        setName("Json2Image");
        this.setProperty("savePath", savePath);
        this.addInput(new DataTypeIO("string", "json"));
        this.addOutput(new DataTypeIO("boolean", "Success or Fail"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            try {
                String filename = JsonUtil.jsonToFile(data, savePath);
                sendData(0, true);
                if (filename != null) {
                    imagePopupWindowShow(filename);
                }
            } catch (Exception e) {
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            data = (String) obj;
            processingBool = true;
        }
    }

    @Override
    public boolean start() {
        savePath = getProperty("savePath").toString();
        File file = new File(savePath);
        if (!file.exists()) {
            boolean b = file.mkdir();
            System.out.println("File creating for " + savePath + " is " + b);
        }
        processingBool = false;
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("savePath")) {
            savePath = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public int getPlatformSupport() {
        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {

    }

    @Override
    public void onDestroy() {

    }

    private void imagePopupWindowShow(String imagePath) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                // All of our necessary variables
                File imageFile;
                File audioFile;
                Image image;
                ImageView imageView;
                Media audio;
                MediaPlayer audioPlayer;
                BorderPane pane;
                Scene scene;
                Stage stage;

                // The path to your image can be a URL,
                // or it can be a directory on your computer.
                // If the picture is on your computer, type the path
                // likes so:
                //     C:\\Path\\To\\Image.jpg
                // If you have a Mac, it's like this:
                //     /Path/To/Image.jpg
                // Replace the path with the one on your computer
                imageFile = new File(imagePath);
                image = new Image(imageFile.toURI().toString());
                imageView = new ImageView(image);

                pane = new BorderPane();
                pane.setCenter(imageView);
                scene = new Scene(pane);

                // Create the actual window and display it.
                stage = new Stage();
                stage.setScene(scene);
                // Without this, the audio won't stop!
                stage.setOnCloseRequest(
                        e -> {
                            e.consume();
                            //  audioPlayer.stop();
                            stage.close();
                        }
                );
                stage.showAndWait();
            }
        });

    }
    
        @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
}
