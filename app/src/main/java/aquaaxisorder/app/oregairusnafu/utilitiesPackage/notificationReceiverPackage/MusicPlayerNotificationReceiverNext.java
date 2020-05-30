package aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.animation.AnimationUtils;

import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.musicPackage.PlayingFragment;

import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class MusicPlayerNotificationReceiverNext extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        mServ.nextTrack();
        try {
            PlayingFragment.getInstance().updateViewForPlayingMusic();
            PlayingFragment.getInstance().updateAndAnimeForegroundImage(AnimationUtils.loadAnimation(context, R.anim.slide_middle_left),
                    AnimationUtils.loadAnimation(context, R.anim.slide_right_middle));
        } catch (NullPointerException ignored) {
        }
    }
}
