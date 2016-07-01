package com.eje_c.vrmultiview.gearvr;

import android.os.Environment;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.gearvr.MeganekkoActivity;
import com.eje_c.vrmultiview.common.ControlMessage;

import java.io.File;
import java.io.IOException;

public class App extends MeganekkoApp {

    private PlayerScene scene;

    public App(Meganekko meganekko) {
        super(meganekko);
        setSceneFromXML(R.xml.scene);
        scene = (PlayerScene) getScene();
    }

    /**
     * Called when receive WebSocket string message.
     *
     * @param controlMessage Message from controller.
     */
    public void onControlMessageReceived(ControlMessage controlMessage) {

        final String videoPath = new File(Environment.getExternalStorageDirectory(), controlMessage.path).getAbsolutePath();

        try {
            scene.load(videoPath);
            scene.seekTo(controlMessage.seek);

            switch (controlMessage.state) {
                case ControlMessage.STATE_PLAY:
                    scene.start();
                    break;
                case ControlMessage.STATE_STOP:
                    scene.pause();
                    break;
            }

        } catch (IOException e) {

            // Show error
            e.printStackTrace();
            ((MeganekkoActivity) getContext()).createVrToastOnUiThread(e.getLocalizedMessage());
        }
    }

    @Override
    public void shutdown() {
        scene.release();
        super.shutdown();
    }

    /**
     * Called when connected with Gear VR.
     */
    public void onControllerConnected() {
        runOnGlThread(() -> scene.setText(R.string.waiting_for_controller_operation));
    }

    /**
     * Called when WebSocket connection is disconnected.
     */
    public void onControllerDisconnected() {
        runOnGlThread(() -> scene.setText(R.string.waiting_for_controller_connection));
    }

    public void onHmdMounted() {
        scene.setVolume(1);
    }

    public void onHmdUnmounted() {
        scene.setVolume(0);
    }
}