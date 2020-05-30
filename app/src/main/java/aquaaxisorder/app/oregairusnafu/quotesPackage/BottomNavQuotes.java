package aquaaxisorder.app.oregairusnafu.quotesPackage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import aquaaxisorder.app.oregairusnafu.R;

public class BottomNavQuotes extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quotes_bottomnav_fragment, container, false);

        BottomNavigationView bottomNav = (BottomNavigationView) view.findViewById(R.id.bottom_navigation_nav_quotes);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_quotes_bottomnav,
                    new HachimanQuotesFragment()).commit();
        }

        return view;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = new HachimanQuotesFragment();
                    switch (menuItem.getItemId()) {
                        case R.id.nav_hachiman:
                            fragment = new HachimanQuotesFragment();
                            break;
                        case R.id.nav_yukino:
                            fragment = new YukinoQuotesFragment();
                            break;
                        case R.id.nav_shizuka:
                            fragment = new ShizukaQuotesFragment();
                            break;
                        case R.id.nav_otherchars:
                            fragment = new OtherCharsQuotesFragment();
                            break;
                    }
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container_quotes_bottomnav,
                                fragment).commit();
                    }
                    return true;
                }
            };

}
