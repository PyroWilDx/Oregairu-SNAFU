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
import android.widget.TextView;
import android.widget.Toast;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.TinyDB;

import static android.content.Context.MODE_PRIVATE;
import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class CustomPlaylistFragment extends Fragment {

    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_customplaylist_fragment, container, false);

        HomeActivity.backPressInMusic = true;

        if (getActivity() != null) {
            editor = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE).edit();
        }

        TinyDB tinyDB = new TinyDB(getContext());
        Toast noAddedTracksToast = new Toast(getContext());

        FloatingActionButton addFloatingActionButton = view.findViewById(R.id.fragment_music_customplaylist_add_button);
        addFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_music_bottomnav,
                            new CustomPlaylistAddFragment()).addToBackStack(null).commit();
                }
            }
        });

        FloatingActionButton sortFloatingActionButton = view.findViewById(R.id.fragment_music_customplaylist_sort_button);
        sortFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_music_bottomnav,
                            new CustomPlaylistSortFragment()).addToBackStack(null).commit();
                }
            }
        });

        FloatingActionButton shuffleFloatingActionButton = view.findViewById(R.id.fragment_music_customplaylist_shuffle_button);
        shuffleFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServ.stopMusic();
                mServ.addCustomSongs();
                editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                editor.apply();
                mServ.createShuffleList();
                mServ.chosenPlaylistNumber = 0;
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new BottomNavMusic()).commit();
                }
            }
        });

        ListView customTracksListView = view.findViewById(R.id.fragment_music_customplaylist_listview);
        TextView noAddedSongTextView = view.findViewById(R.id.fragment_music_customplaylist_noaddedmusic_textview);
        if (tinyDB.getListInt(getString(R.string.prefsCustomTracksList)).size() == 0) {
            noAddedSongTextView.setVisibility(View.VISIBLE);
            customTracksListView.setVisibility(View.INVISIBLE);
            shuffleFloatingActionButton.hide();
            shuffleFloatingActionButton.setEnabled(false);
            sortFloatingActionButton.hide();
            sortFloatingActionButton.setEnabled(false);
        } else {
            noAddedSongTextView.setVisibility(View.INVISIBLE);

            if (getContext() != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item, R.id.textColorForListView,
                        mServ.addTracksTitlesToList(0));
                customTracksListView.setAdapter(adapter);

                customTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mServ.stopMusic();
                        mServ.addCustomSongs();
                        editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                        editor.apply();
                        mServ.getWantedMusicToPlay(position);
                        mServ.chosenPlaylistNumber = 0;
                        if (getFragmentManager() != null) {
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new BottomNavMusic()).commit();
                        }
                    }
                });
            }
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.backPressInMusic = false;
    }
}
