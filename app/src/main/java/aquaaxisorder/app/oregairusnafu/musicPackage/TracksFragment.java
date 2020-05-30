package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import aquaaxisorder.app.oregairusnafu.R;

import static android.content.Context.MODE_PRIVATE;
import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class TracksFragment extends Fragment {

    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_tracks_fragment, container, false);

        if (getActivity() != null) {
            editor = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE).edit();
        }

        FloatingActionButton shuffleFloatingActionButton = view.findViewById(R.id.fragment_musictracks_alltracks_shuffleButton);
        shuffleFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServ.stopMusic();
                mServ.addAllMusics();
                editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                editor.apply();
                mServ.createShuffleList();
                mServ.chosenPlaylistNumber = -1;
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new BottomNavMusic()).commit();
                }
            }
        });

        ListView songsListView = view.findViewById(R.id.fragment_musictracks_alltracks_listview);

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item, R.id.textColorForListView, mServ.addTracksTitlesToList(-1));
            songsListView.setAdapter(adapter);

            songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mServ.stopMusic();
                    mServ.addAllMusics();
                    editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                    editor.apply();
                    mServ.getWantedMusicToPlay(position);
                    mServ.chosenPlaylistNumber = -1;
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new BottomNavMusic()).commit();
                    }
                }
            });
        }

        return view;
    }

}
