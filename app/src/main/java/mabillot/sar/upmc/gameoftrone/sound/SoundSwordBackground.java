package mabillot.sar.upmc.gameoftrone.sound;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import mabillot.sar.upmc.gameoftrone.R;

/**
 * Created by paulo on 29/01/2016.
 */
public class SoundSwordBackground extends Service {
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        player = MediaPlayer.create(this, R.raw.attack);
        player.setLooping(false);
    }

    @Override
    public void onDestroy() {
        player.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_NOT_STICKY;
    }
}
