package aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import aquaaxisorder.app.oregairusnafu.OregairuMusicService;
import aquaaxisorder.app.oregairusnafu.R;

import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class MusicPlayerNotificationReceiverPlayPause extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mServ.mPlayer.isPlaying()) {
            mServ.pauseMusic();
            context.getSharedPreferences(context.getString(R.string.prefsName), Context.MODE_PRIVATE)
                    .edit().putBoolean(context.getString(R.string.prefsMusicPaused), true).apply();
        } else {
            mServ.resumeMusic();
            context.getSharedPreferences(context.getString(R.string.prefsName), Context.MODE_PRIVATE)
                    .edit().putBoolean(context.getString(R.string.prefsMusicPaused), false).apply();
        }
        OregairuMusicService.getInstance().updateNotifPlayPauseButtons();
    }
}
