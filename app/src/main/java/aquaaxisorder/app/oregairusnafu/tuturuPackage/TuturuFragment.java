package aquaaxisorder.app.oregairusnafu.tuturuPackage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;
import aquaaxisorder.app.oregairusnafu.R;

import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class TuturuFragment extends Fragment {

    private TextView pressedNumberTextView, pressYuiTextView;
    private ImageView yuiImageView;
    private GifImageView animImageView;
    private Button helpButton;
    private SharedPreferences.Editor editor;
    private String prefsString;
    private int pressCounter, yahalloNo = 0;
    private long animDuration;
    private MediaPlayer yahalloPlayer;
    private Toast yahalloToast;
    private Random random;
    private Handler handler;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tuturu_fragment, container, false);

        yahalloToast = new Toast(getContext());
        random = new Random();
        handler = new Handler();
        if (getActivity() != null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences(getString(R.string.prefsName), Context.MODE_PRIVATE);
            editor = prefs.edit();
            prefsString = getString(R.string.yuiPressedTimes);
            pressCounter = prefs.getInt(prefsString, 0);
        }

        pressYuiTextView = view.findViewById(R.id.fragment_tuturu_pressyui_textview);
        pressedNumberTextView = view.findViewById(R.id.fragment_tuturu_number_pressed_textview);
        pressedNumberTextView.setText(String.valueOf(pressCounter));
        yuiImageView = view.findViewById(R.id.fragment_tuturu_yui_button);
        animImageView = view.findViewById(R.id.fragment_tuturu_anim_image);

        yuiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopYahalloPlayer();
                cancelAllListener();
                yuiImageView.clearAnimation();
                yuiImageView.setImageResource(R.drawable.yahallo_yui);
                mServ.pauseMusic();
                updateCounter();
                yahalloNo = random.nextInt(200);
                startYahalloAnimationPlayerToast(yahalloNo);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        yuiImageView.clearAnimation();
                        yuiImageView.setImageResource(R.drawable.yahallo_yui);
                        if (yahalloNo > 189 && yahalloNo < 200) {
                            setViewsVisibility(View.VISIBLE, true, 0);
                        }
                        mServ.resumeMusic();
                    }
                }, animDuration);
            }
        });

        helpButton = view.findViewById(R.id.fragment_tuturu_help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Probabilities :");
                alert.setMessage(
                        "Yahallo! - 45%\n" +
                                "Yahallo! Haruno - 15%\n" +
                                "Yahallo! Komachi - 15%\n" +
                                "Yahallo! Sparta Gamma - 12.5%\n" +
                                "Yahallo! EARRAPE - 10%\n" +
                                "Yahallo! Totsuka <3 - 2.5%");
                alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        buttonEffect(helpButton);

        return view;
    }

    private void stopYahalloPlayer() {
        if (yahalloPlayer != null) {
            yahalloPlayer.stop();
            yahalloPlayer.reset();
            yahalloPlayer.release();
            yahalloPlayer = null;
        }
    }

    private void updateCounter() {
        pressCounter++;
        editor.putInt(prefsString, pressCounter);
        editor.apply();
        pressedNumberTextView.setText(String.valueOf(pressCounter));
    }

    private void cancelAllListener() {
        setViewsVisibility(View.VISIBLE, true, 0);
        handler.removeCallbacksAndMessages(null);
        if (growAnim != null) {
            growAnim.setAnimationListener(null);
        }
    }

    private Animation growAnim;

    private void startYahalloAnimationPlayerToast(int yahalloNumber) {
        yuiImageView.clearAnimation();
        yahalloToast.cancel();
        if (yahalloNumber < 90) {
            yuiImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_shake));
            yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo);
            yahalloToast = Toast.makeText(getContext(), "Yahallo!", Toast.LENGTH_SHORT);
            yahalloToast.show();
            animDuration = 2000;

        } else if (yahalloNumber < 120) {
            yuiImageView.setImageResource(R.drawable.yahallo_haruno);
            yuiImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_shake));
            yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo_haruno);
            yahalloToast = Toast.makeText(getContext(), "Yahallo! Haruno", Toast.LENGTH_SHORT);
            yahalloToast.show();
            animDuration = 2000;

        } else if (yahalloNumber < 150) {
            yuiImageView.setImageResource(R.drawable.yahallo_komachi);
            yuiImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_shake));
            yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo_komachi);
            yahalloToast = Toast.makeText(getContext(), "Yahallo! Komachi", Toast.LENGTH_SHORT);
            yahalloToast.show();
            animDuration = 2000;

        } else if (yahalloNumber < 175) {
            yuiImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_sparta_gamma));
            yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo_yui_sparta);
            yahalloToast = Toast.makeText(getContext(), "Yahallo! Sparta Gamma", Toast.LENGTH_SHORT);
            yahalloToast.show();
            animDuration = 17000;

        } else if (yahalloNumber < 195) {
            growAnim = AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_grow);
            yuiImageView.startAnimation(growAnim);
            yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo_earrape);
            growAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        AnimationSet yahalloAnimationSet = new AnimationSet(false);
                        yahalloAnimationSet.addAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.tuturu_grow_big));
                        yahalloAnimationSet.addAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.tuturu_grow_big_move));
                        yuiImageView.startAnimation(yahalloAnimationSet);
                        yahalloToast = Toast.makeText(getContext(), "Yahallo! EARRAPE", Toast.LENGTH_SHORT);
                        yahalloToast.show();
                    } catch (NullPointerException ignored) {
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            animDuration = 4000;

        } else if (yahalloNumber < 200) {
            setViewsVisibility(View.INVISIBLE, false, R.drawable.yahallo_totsuka_gif);
            Animation totsukaAnim = AnimationUtils.loadAnimation(getContext(), R.anim.tuturu_tosuka);
            animImageView.startAnimation(totsukaAnim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    yahalloPlayer = MediaPlayer.create(getContext(), R.raw.yahallo_totsuka);
                    yahalloPlayer.start();
                    yahalloToast = Toast.makeText(getContext(), "Yahallo! Totsuka <3", Toast.LENGTH_SHORT);
                    yahalloToast.show();
                }
            }, 500);
            animDuration = 3000;

        } else {
            yahalloToast = Toast.makeText(getContext(), "Sorry, an error occurred.", Toast.LENGTH_SHORT);
            yahalloToast.show();
        }

        if (yahalloPlayer != null) {
            yahalloPlayer.start();
        }
    }

    private void setViewsVisibility(int visibility, boolean enabled, int animImage) {
        pressYuiTextView.setVisibility(visibility);
        pressedNumberTextView.setVisibility(visibility);
        yuiImageView.setVisibility(visibility);
        yuiImageView.setEnabled(enabled);
        helpButton.setVisibility(visibility);
        helpButton.setEnabled(enabled);
        animImageView.setImageResource(animImage);
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

}
