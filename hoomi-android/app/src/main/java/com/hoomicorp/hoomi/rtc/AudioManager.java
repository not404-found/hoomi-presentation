package com.hoomicorp.hoomi.rtc;

import android.content.Context;
import android.util.Log;

import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

public class AudioManager {
    private Context applicationContext;
    private android.media.AudioManager audioManager;

    public AudioManager(final Context applicationContext) {
        this.applicationContext = applicationContext;
        this.audioManager = ((android.media.AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE));
        setMicrophoneMute(false);
    }

    public void setSpeakerphoneOn(final boolean enable) {
        if (enable != audioManager.isSpeakerphoneOn())  {
            Log.d("[PC]", "setSpeakerphoneOn(): " + enable);
            audioManager.setSpeakerphoneOn(enable);
        }
    }


    public void setMicrophoneMute(final boolean enable) {
        if (enable != audioManager.isMicrophoneMute()) {
            Log.d("[PC]", "setMicrophoneMute(): " + enable);
            audioManager.setMicrophoneMute(enable);
        }
    }

    public AudioDeviceModule createAudioDeviceModule () {
        return JavaAudioDeviceModule.builder(applicationContext).createAudioDeviceModule();
    }
}
