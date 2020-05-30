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

import java.util.ArrayList;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;

import static android.content.Context.MODE_PRIVATE;
import static aquaaxisorder.app.oregairusnafu.HomeActivity.mServ;

public class PlaylistShowTracksFragment extends Fragment {

    private int playlistNumber = 1;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_showplaylist_fragment, container, false);

        if (getActivity() != null) {
            editor = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE).edit();
        }

        HomeActivity.backPressInMusic = true;

        ArrayList<String> playlistSongTitleList = new ArrayList<>();

        if (getArguments() != null) {
            playlistNumber = getArguments().getInt(getString(R.string.argsPlaylistNumber));
        }

        switch (playlistNumber) {
            case 1:
                playlistSongTitleList = mServ.addTracksTitlesToList(1);
                break;
            case 2:
                playlistSongTitleList = mServ.addTracksTitlesToList(2);
                break;
            case 3:
                playlistSongTitleList = mServ.addTracksTitlesToList(3);
                break;
            case 4:
                playlistSongTitleList = mServ.addTracksTitlesToList(4);
                break;
            case 5:
                playlistSongTitleList = mServ.addTracksTitlesToList(5);
                break;
            case 6:
                playlistSongTitleList = mServ.addTracksTitlesToList(6);
                break;
            case 7:
                playlistSongTitleList = mServ.addTracksTitlesToList(7);
                break;
            case 8:
                playlistSongTitleList = mServ.addTracksTitlesToList(8);
                break;
        }

        FloatingActionButton shuffleFloatingActionButton = view.findViewById(R.id.fragment_music_showplaylist_floatingbutton);
        shuffleFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServ.stopMusic();
                addWhichPlaylist();
                editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                editor.apply();
                mServ.createShuffleList();
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new BottomNavMusic()).addToBackStack(null).commit();
                }
            }
        });

        ListView playlistSongListView = view.findViewById(R.id.fragment_showplaylistmusic_song_listview);

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item, R.id.textColorForListView, playlistSongTitleList);
            playlistSongListView.setAdapter(adapter);

            playlistSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mServ.stopMusic();
                    addWhichPlaylist();
                    editor.putBoolean(getString(R.string.prefsMusicPaused), false);
                    editor.apply();
                    mServ.getWantedMusicToPlay(position);
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new BottomNavMusic()).commit();
                    }
                }
            });
        }

        return view;
    }

    private void addWhichPlaylist() {
        int playlistNum = 1;
        switch (playlistNumber) {
            case 1:
                mServ.addOpeningsEndings();
                playlistNum = 1;
                break;
            case 2:
                mServ.addOsts();
                playlistNum = 2;
                break;
            case 3:
                mServ.addOstsZoku();
                playlistNum = 3;
                break;
            case 4:
                mServ.addDramaCD();
                playlistNum = 4;
                break;
            case 5:
                mServ.addCharaSongs();
                playlistNum = 5;
                break;
            case 6:
                mServ.addCharaSongsZoku();
                playlistNum = 6;
                break;
            case 7:
                mServ.addOutTracks();
                playlistNum = 7;
                break;
            case 8:
                mServ.addCovers();
                playlistNum = 8;
                break;
        }
        mServ.chosenPlaylistNumber = playlistNum;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.backPressInMusic = false;
    }
}

