package aquaaxisorder.app.oregairusnafu;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        if (getActivity() != null) {
            prefs = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
            editor = prefs.edit();
        }

        SwitchCompat splashScreenSwitch = view.findViewById(R.id.fragment_settings_splashscreen_switch);
        splashScreenSwitch.setChecked(prefs.getBoolean(getString(R.string.prefsSplashScreen), true));
        splashScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(getString(R.string.prefsSplashScreen), true);
                } else {
                    editor.putBoolean(getString(R.string.prefsSplashScreen), false);
                }
                editor.apply();
            }
        });

        return view;
    }


}
