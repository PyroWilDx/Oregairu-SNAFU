package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.ListviewAdapterImageText;

public class PlaylistFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_playlist_fragment, container, false);

        int[] songImages = {R.drawable.music_yukino_piano, R.drawable.music_harumodoki_cover, R.drawable.music_ost_cover, R.drawable.music_ostzoku_cover,
                R.drawable.music_cd_3_cover, R.drawable.music_charasong_cover, R.drawable.music_charasongzoku_cover, R.drawable.music_outtrack_cover,
                R.drawable.music_festival_music};
        String[] songTitles = {"Your custom playlist", "Openings & Endings", "OST", "OST Zoku", "Drama CDs", "Character songs", "Character songs Zoku",
                "Mini Soundtracks |Matsuri Out Tracks|", "Covers"};


        ListView playlistListView = view.findViewById(R.id.fragment_playlist_listview);
        playlistListView.setAdapter(new ListviewAdapterImageText(getContext(), songImages, songTitles));

        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container_music_bottomnav,
                                new CustomPlaylistFragment()).addToBackStack(null).commit();
                    }
                } else {
                    PlaylistShowTracksFragment playlistShowTracksFragment = new PlaylistShowTracksFragment();
                    Bundle args = new Bundle();
                    args.putInt(getString(R.string.argsPlaylistNumber), position);
                    playlistShowTracksFragment.setArguments(args);
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container_music_bottomnav,
                                playlistShowTracksFragment).addToBackStack(null).commit();
                    }
                }
            }
        });

        return view;
    }

}

