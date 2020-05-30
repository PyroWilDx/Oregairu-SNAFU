package aquaaxisorder.app.oregairusnafu.lightNovelPackage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;


import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;

import static android.content.Context.MODE_PRIVATE;

public class ChooseNovelFragment extends Fragment {

    private int startPage = 1;
    private boolean nightLightMode = true, landScape;
    private String volumeNo = "Oregairu1.pdf";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.novel_choose_fragment, container, false);

        setupUI(view.findViewById(R.id.parent_choosenovelfrag));
        toast = new Toast(getContext());

        if (getActivity() != null) {
            prefs = getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
            editor = prefs.edit();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        final SwitchCompat orientationSwitch = view.findViewById(R.id.fragment_lightnovel_orientation_switch);

        if (prefs.getBoolean(getString(R.string.prefsScape), true)) {
            landScape = true;
            orientationSwitch.setText(getString(R.string.orientationLandscapeLocked));
        } else {
            landScape = false;
            orientationSwitch.setText(getString(R.string.orientationUnlocked));
        }

        orientationSwitch.setChecked(prefs.getBoolean("Scape", true));
        orientationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    landScape = true;
                    editor.putBoolean(getString(R.string.prefsScape), true);
                    editor.apply();
                    orientationSwitch.setText(getString(R.string.orientationLandscapeLocked));
                } else {
                    landScape = false;
                    editor.putBoolean(getString(R.string.prefsScape), false);
                    editor.apply();
                    orientationSwitch.setText(getString(R.string.orientationUnlocked));
                }
            }
        });

        final SwitchCompat nightModeSwitch = view.findViewById(R.id.fragment_lightnovel_nightMode_switch);

        if (prefs.contains(getString(R.string.prefsNightMode)))
            nightLightMode = prefs.getBoolean("nightMode", true);
        else {
            editor.putBoolean(getString(R.string.prefsNightMode), true);
            editor.apply();
        }

        if (nightLightMode) {
            nightModeSwitch.setText(getString(R.string.nightModeON));
        } else {
            nightModeSwitch.setText(getString(R.string.nightModeOFF));
        }

        nightModeSwitch.setChecked(nightLightMode);
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(getString(R.string.prefsNightMode), true);
                    editor.apply();
                    nightModeSwitch.setText(getString(R.string.nightModeON));
                    nightLightMode = true;
                } else {
                    editor.putBoolean(getString(R.string.prefsNightMode), false);
                    editor.apply();
                    nightModeSwitch.setText(getString(R.string.nightModeOFF));
                    nightLightMode = false;
                }
            }
        });

        final EditText whichPageEditText = view.findViewById(R.id.fragment_lightnovel_whichpage_edittext);
        whichPageEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorWhiteNightLight), PorterDuff.Mode.SRC_ATOP);

        if (prefs.contains(getString(R.string.prefsStoredPage))) {
            whichPageEditText.setText(MessageFormat.format("{0}", prefs.getInt(getString(R.string.prefsStoredPage), 1)));
        } else {
            whichPageEditText.setText("1");
        }

        GridView volumesGridView = view.findViewById(R.id.fragment_lightnovel_volume_gridview);
        final ArrayList<String> volumeNumList = new ArrayList<>();
        volumeNumList.add("Download");
        volumeNumList.add("Volume 1");
        volumeNumList.add("Volume 2");
        volumeNumList.add("Volume 3");
        volumeNumList.add("Volume 4");
        volumeNumList.add("Volume 5");
        volumeNumList.add("Volume 6");
        volumeNumList.add("Volume 6.5");
        volumeNumList.add("Volume 7.5");
        volumeNumList.add("Volume 7");
        volumeNumList.add("Volume 8");
        volumeNumList.add("Volume 9");
        volumeNumList.add("Volume 10");
        volumeNumList.add("Volume 10.5");
        volumeNumList.add("Volume 11");
        volumeNumList.add("Volume 12");
        volumeNumList.add("Volume 13");
        volumeNumList.add("Volume 14");

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    R.layout.music_listview_item, R.id.textColorForListView, volumeNumList);
            volumesGridView.setAdapter(adapter);
            volumesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int editTextPageValue = 1;
                    try {
                        editTextPageValue = Integer.valueOf(whichPageEditText.getText().toString());
                        if (whichPageEditText.getText().toString().trim().length() != 0) {
                            if (editTextPageValue <= 1000 && editTextPageValue > 0) {
                                startPage = Integer.valueOf(whichPageEditText.getText().toString());
                                editor.putInt(getString(R.string.prefsStoredPage), startPage);
                                editor.apply();
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    boolean loadReader = true;
                    switch (position) {
                        case 0:
                            loadReader = false;
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/folders/16aIw10AFJIweUYtPpe0Vi6clXcEssnYR")));
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            volumeNo = "Oregairu" + (position) + ".pdf";
                            break;
                        case 7:
                            volumeNo = "Oregairu6.5.pdf";
                            break;
                        case 8:
                            volumeNo = "Oregairu7.5.pdf";
                            break;
                        case 9:
                            volumeNo = "Oregairu7.pdf";
                            break;
                        case 10:
                            volumeNo = "Oregairu8.pdf";
                            break;
                        case 11:
                            volumeNo = "Oregairu9.pdf";
                            break;
                        case 12:
                            volumeNo = "Oregairu10.pdf";
                            break;
                        case 13:
                            volumeNo = "Oregairu10.5.pdf";
                            break;
                        case 14:
                            volumeNo = "Oregairu11.pdf";
                            break;
                        case 15:
                            volumeNo = "Oregairu12.pdf";
                            break;
                        case 16:
                            volumeNo = "Oregairu13.pdf";
                            break;
                        case 17:
                            loadReader = false;
                            toast.cancel();
                            toast = Toast.makeText(getActivity(), "Unfortunately, Vol.14 hasn't been released yet !",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            break;
                    }
                    if (loadReader) {
                        readSelectedVolume();
                        if (whichPageEditText.getText().toString().trim().length() == 0 || editTextPageValue == 0) {
                            toast.cancel();
                            toast = Toast.makeText(getContext(), "Sorry the page number that you set is not valid, starting at page 1...",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
            });
        }

        return view;
    }

    private void readSelectedVolume() {

        ReadNovelFragment fragment = new ReadNovelFragment();
        Bundle args = new Bundle();
        args.putBoolean("LightNovelNightMode", nightLightMode);
        args.putBoolean("LightNovelLandScape", landScape);
        args.putString("LightNovelVolumeNO", volumeNo);
        args.putInt("LightNovelStartPage", startPage);
        fragment.setArguments(args);
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        }
    }

    private void setupUI(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    if (getActivity() != null) {
                        ((HomeActivity) getActivity()).closeKeyboard();
                    }
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
