package com.hoomicorp.hoomi.rtc;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.util.Log;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

public class CameraManager {
    private CameraEnumerator cameraEnumerator;

    public CameraManager(final Context applicationContext) {
        boolean camera2supported = false;
        try {
            camera2supported = Camera2Enumerator.isSupported(applicationContext);
        } catch (Throwable tr) {
            // Some devices will crash here with: Fatal Exception: java.lang.AssertionError: Supported FPS ranges cannot be null.
            // Make sure we don't.
            Log.w("[PC]", "Error checking for Camera2 API support.", tr);
        }

        if (camera2supported) {
            Log.d("[PC]", "Creating video capturer using Camera2 API.");
            cameraEnumerator = new Camera2Enumerator(applicationContext);
        } else {
            Log.d("[PC]", "Creating video capturer using Camera1 API.");
            cameraEnumerator = new Camera1Enumerator(false);
        }
    }

    public VideoCapturer createCameraCapturer(boolean isFront) {
        final String[] deviceNames = cameraEnumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (isFront ? cameraEnumerator.isFrontFacing(deviceName) : cameraEnumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = cameraEnumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    public VideoCapturer createScreenCapturer(final Intent mMediaProjectionPermissionResultData) {
        return new ScreenCapturerAndroid(mMediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                System.out.println("STOOOOOP SCREEN CAPTURER");
            }
        });
    }
}
