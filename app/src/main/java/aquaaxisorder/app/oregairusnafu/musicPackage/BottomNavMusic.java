package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;

public class BottomNavMusic extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_bottomnav_fragment, container, false);

        BottomNavigationView bottomNav = (BottomNavigationView) view.findViewById(R.id.bottom_navigation_nav);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_music_bottomnav,
                    new PlayingFragment()).commit();
        }

        return view;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = new PlayingFragment();
                    switch (menuItem.getItemId()) {
                        case R.id.nav_playing:
                            fragment = new PlayingFragment();
                            break;
                        case R.id.nav_tracks:
                            fragment = new TracksFragment();
                            break;
                        case R.id.nav_playlists:
                            fragment = new PlaylistFragment();
                            break;
                    }
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(
                                R.id.fragment_container_music_bottomnav, fragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.backPressInMusic = false;
    }
}
