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
package com.sonicmsgr.setting;

/**
 *
 * @author yada
 */
public class Settings {
    public Settings() {

    }

    public Settings(String license) {
        licensekey = license;
    }
    
    // 2nd channel that will be activatted
    public int secondChannelToActivate = 5; // channel 6;
    
    public boolean enableAppSetting = true;

    public String appFolder = "SonicMsgr";

    public String appStartupSonicCode = "startup";
    public boolean enableStartupMessage = false;

    // Enable Service
    public boolean enableSoundMessageService = true;
    public boolean enabledGPSMessageService = true;
    public boolean enabledSonicIDChat = false;

    // Enable/Disable Message

    public boolean enableNotificationMessage = true;
    public boolean enableURLMessage = true;
    public boolean enableTextMessage = true;
    public boolean enableRSAMessage = true;
    public boolean enableIotaTransferMessage = true;
    public boolean enableAppLaunchMessage = true;
    public boolean enableSonicPayRequestMessage = true;
    public boolean enableSonicPayReplyMessage = false;
    
    public boolean onSonicBootBool = false;

    public int numberOfSecondRepeatedMessage = 10;
    public long msgTimeToExpireInSec = 10;

    public boolean startService = true;
    public int gpsMessageTimeToExpireInSec = 43200; // 12 hours

    public int gpsMaxThresholdIndicateNotMovingInFeet = 15;
    public int gpsSonicDbRefreshRateInSec = 60*60*2; // every two hours

    public final String serverWSAddressFinal = "https://www.soniccreator.com:8895/SonicWS/sonicsecure";
    public String serverWSAddress = null;
    public String licensekey = "";
    public boolean sonicExtenderBool = false;

    // SoundPrint
    public boolean enableLoggingSignature = false;
    public boolean enableSoundPrintMessage = false;
    public boolean enableSoundPrintFeature = false;

    public int playMethod = 0;
    public String wavFileName = "";

  
    public int currentTrackCode = -1;
    
    public int restartAudioInSec = 60 * 60; // Every hourse
    public boolean restartAudioUsingAudioMethod = true;

    public boolean enableRestartSonicService = true;
    
    
    // Notification service and icon
    public boolean enabledNotificationServiceIcon = true;
    
    //public String notificationServiceTitleName = "Sonic Messenger";
    public String notificationServiceTitleName = "";

    // Private
    public boolean enabledIOTA = true;

    // Dont'modify
    public boolean isStartedStatic = false;
    
    public String seed = null;
    
    
    //
    public String type = "";
    public String phoneNumber = "";
    public String vinNumber = "";
    public String macAddr = "";
    public String hashWord = "";
    public String email = "";
    
    public String sonicIDServer = "soniccreator.com";
    public String mamURL = "https://www.soniccreator.com:3800/mam";
    public int sonicIDPort = 3899;

}
