package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import aquaaxisorder.app.oregairusnafu.R;

import static android.content.Context.MODE_PRIVATE;
import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class PlayingFragment extends Fragment {

    private TextView songTitleTextView;
    private TextView songAtWhichMinTextView;
    private TextView songMaxMinTextView;
    private CircleImageView foregroundImage;
    private Button playPauseButton, repeatButton;
    private SeekBar songSeekBar;
    private Handler handler;
    private Toast repeatToast;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private View view;
    @SuppressLint("StaticFieldLeak")
    private static PlayingFragment instance;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.music_playing_fragment, container, false);

        instance = this;
        repeatToast = new Toast(getContext());
        mServ.length = mServ.mPlayer.getCurrentPosition();

        if (getActivity() != null) {
            prefs = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
            editor = prefs.edit();
        }

        songTitleTextView = view.findViewById(R.id.fragment_musicplaying_songtitle_text);
        TextView playlistNameTextView = view.findViewById(R.id.fragment_playing_playlistname_textview);
        playlistNameTextView.setText(mServ.playlistNameList.get(mServ.chosenPlaylistNumber + 1));
        foregroundImage = view.findViewById(R.id.fragment_musicplaying_foreground_image);
        foregroundImage.setImageResource(mServ.songsImageList.get(mServ.atWhichMusicNumber));
        playPauseButton = view.findViewById(R.id.fragment_musicplaying_play_button);
        updatePlayPauseButtons();
        Button nextButton = view.findViewById(R.id.fragment_musicplaying_next_button);
        Button previousButton = view.findViewById(R.id.fragment_musicplaying_previous_button);
        Button volumeButton = view.findViewById(R.id.fragment_playing_volume_button);
        repeatButton = view.findViewById(R.id.fragment_playing_repeat_button);
        songSeekBar = view.findViewById(R.id.fragment_musicplaying_seekbar);

        buttonEffect(playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseOrResumeMediaPlayer();
            }
        });
        buttonEffect(nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextTrack();
            }
        });
        buttonEffect(previousButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousTrack();
            }
        });
        buttonEffect(volumeButton);
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    if (audio != null) {
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                    }
                }
                showVolumeSeekbarAlertDialog();
            }
        });

        if (mServ.mPlayer.isLooping()) {
            repeatButton.setBackgroundResource(R.drawable.icon_repeat_pink);
        } else {
            repeatButton.setBackgroundResource(R.drawable.icon_repeat_blue);
        }

        buttonEffect(repeatButton);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                repeatToast.cancel();
                if (!mServ.mPlayer.isLooping()) {
                    mServ.mPlayer.setLooping(true);
                    repeatButton.setBackgroundResource(R.drawable.icon_repeat_pink);
                    repeatToast = Toast.makeText(getContext(), R.string.repeatON, Toast.LENGTH_SHORT);
                } else {
                    mServ.mPlayer.setLooping(false);
                    repeatButton.setBackgroundResource(R.drawable.icon_repeat_blue);
                    repeatToast = Toast.makeText(getActivity(), R.string.repeatOFF, Toast.LENGTH_SHORT);
                }
                repeatToast.show();
            }

        });

        handler = new Handler();
        songSeekBar = view.findViewById(R.id.fragment_musicplaying_seekbar);
        songAtWhichMinTextView = view.findViewById(R.id.fragment_musicplaying_whichmin_textview);
        songMaxMinTextView = view.findViewById(R.id.fragment_musicplaying_maxmin_textview);

        updateViewForPlayingMusic();

        return view;
    }

    public void updateViewForPlayingMusic() {
        try {
            updatePlayPauseButtons();
        } catch (NullPointerException ignored) {
        }
        songTitleTextView.setText(mServ.songsNameList.get(mServ.atWhichMusicNumber));
        if (mServ != null && mServ.mPlayer != null) {
            songSeekBar.setMax(mServ.mPlayer.getDuration());
            songMaxMinTextView.setText(convertMStoMM_SS(mServ.mPlayer.getDuration()));
        }
        playCycle();

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if (input) {
                    mServ.mPlayer.seekTo(progress);
                    mServ.length = progress;
                    songAtWhichMinTextView.setText(convertMStoMM_SS(mServ.mPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        if (mServ != null && mServ.mPlayer != null) {
            mServ.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mServ.nextTrack();
                    try {
                        updateAndAnimeForegroundImage(AnimationUtils.loadAnimation(getContext(), R.anim.slide_middle_left),
                                AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_middle));
                    } catch (NullPointerException ignored) {
                    }
                    updateViewForPlayingMusic();
                }
            });
        }
    }

    private void playCycle() {
        if (mServ != null) {
            if (songSeekBar != null) {
                if (mServ.mPlayer != null) {
                    songSeekBar.setProgress(mServ.mPlayer.getCurrentPosition());
                    songAtWhichMinTextView.setText(convertMStoMM_SS(mServ.mPlayer.getCurrentPosition()));

                    if (mServ.mPlayer.isPlaying()) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                playCycle();
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                    }
                }
            }
        }
    }

    @NonNull
    private String convertMStoMM_SS(int millis) {
        return String.format(Locale.getDefault(), "%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private void playNextTrack() {
        repeatButton.setBackgroundResource(R.drawable.icon_repeat_blue);
        mServ.nextTrack();
        updateAndAnimeForegroundImage(AnimationUtils.loadAnimation(getContext(), R.anim.slide_middle_left),
                AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_middle));
        updateViewForPlayingMusic();
    }

    private void playPreviousTrack() {
        repeatButton.setBackgroundResource(R.drawable.icon_repeat_blue);
        mServ.previousTrack();
        try {
            updateAndAnimeForegroundImage(AnimationUtils.loadAnimation(getContext(), R.anim.slide_middle_right),
                    AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_middle));
        } catch (NullPointerException ignored) {
        }
        updateViewForPlayingMusic();
    }

    private void pauseOrResumeMediaPlayer() {
        if (mServ.mPlayer.isPlaying()) {
            mServ.pauseMusic();
            editor.putBoolean(getString(R.string.prefsMusicPaused), true);
        } else {
            mServ.resumeMusic();
            editor.putBoolean(getString(R.string.prefsMusicPaused), false);
            playCycle();
        }
        updatePlayPauseButtons();
        editor.apply();
    }

    private void updatePlayPauseButtons() {
        if (mServ.mPlayer.isPlaying()) {
            playPauseButton.setBackgroundResource(R.drawable.play_to_pause);
            if (getContext() != null) {
                playPauseButton.setBackgroundTintList(ContextCompat.getColorStateList(
                        getContext(), R.color.colorOregairuPink));
            }
        } else {
            playPauseButton.setBackgroundResource(R.drawable.pause_to_play);
            if (getContext() != null) {
                playPauseButton.setBackgroundTintList(ContextCompat.getColorStateList(
                        getContext(), R.color.colorOregairuBlue));
            }
        }
        startAnimateVectorDrawable(playPauseButton.getBackground());
        mServ.updateNotifPlayPauseButtons();
    }

    private void startAnimateVectorDrawable(Drawable drawable) {
        AnimatedVectorDrawableCompat avdCompat;
        AnimatedVectorDrawable avd;
        if (drawable instanceof AnimatedVectorDrawableCompat) {
            avdCompat = (AnimatedVectorDrawableCompat) drawable;
            avdCompat.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            avd = (AnimatedVectorDrawable) drawable;
            avd.start();
        }
    }

    public void updateAndAnimeForegroundImage(Animation firstAnimation, final Animation secondAnimation) {
        foregroundImage.clearAnimation();
        foregroundImage.startAnimation(firstAnimation);
        firstAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foregroundImage.setImageResource(mServ.songsImageList.get(mServ.atWhichMusicNumber));
                foregroundImage.startAnimation(secondAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showVolumeSeekbarAlertDialog() {
        if (getActivity() != null && getContext() != null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View layout = inflater.inflate(R.layout.seekbar_alertdialog, (ViewGroup) view.findViewById(R.id.seekbar_alertdialog_root));
                AlertDialog.Builder volumeAlert = new AlertDialog.Builder(getContext()).setView(layout);
                final AlertDialog alertDialog = volumeAlert.create();
                alertDialog.setTitle("Volume : ");
                alertDialog.setMessage(prefs.getInt(getString(R.string.prefsVolumeLevel), 100) + "%");
                alertDialog.show();
                final SeekBar volumeSeekBar = (SeekBar) layout.findViewById(R.id.alertdialog_volumeseekbar);
                volumeSeekBar.setProgress(prefs.getInt(getString(R.string.prefsVolumeLevel), 100));
                volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeLevel = progress * 0.01f;
                        mServ.mPlayer.setVolume(volumeLevel, volumeLevel);
                        alertDialog.setMessage(progress + "%");
                        editor.putInt(getString(R.string.prefsVolumeLevel), progress);
                        editor.apply();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                Button okButton = (Button) layout.findViewById(R.id.alertdialog_volume_ok_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        }
    }

    private static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setAlpha((float) 0.6);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.setAlpha(1);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public static PlayingFragment getInstance() {
        return instance;
    }

}
