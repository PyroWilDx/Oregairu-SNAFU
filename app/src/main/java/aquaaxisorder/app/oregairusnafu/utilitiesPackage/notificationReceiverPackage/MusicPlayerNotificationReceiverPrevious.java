package aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.animation.AnimationUtils;

import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.musicPackage.PlayingFragment;

import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class MusicPlayerNotificationReceiverPrevious extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        mServ.previousTrack();
        try {
            PlayingFragment.getInstance().updateViewForPlayingMusic();
            PlayingFragment.getInstance().updateAndAnimeForegroundImage(AnimationUtils.loadAnimation(context, R.anim.slide_middle_right),
                    AnimationUtils.loadAnimation(context, R.anim.slide_left_middle));
        } catch (NullPointerException ignored) {
        }
    }
}

