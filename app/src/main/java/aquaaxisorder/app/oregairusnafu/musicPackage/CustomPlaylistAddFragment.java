package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.TinyDB;

public class CustomPlaylistAddFragment extends Fragment {

    private ArrayList<Integer> customTracksList;
    private ListView allTracksListView;
    private TinyDB tinyDB;
    private Toast addedOrRemovedToast;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_addtracks_fragment, container, false);

        HomeActivity.backPressInMusic = true;

        tinyDB = new TinyDB(getContext());
        customTracksList = new ArrayList<>(tinyDB.getListInt(getString(R.string.prefsCustomTracksList)));
        addedOrRemovedToast = new Toast(getContext());
        addedOrRemovedToast = Toast.makeText(getContext(), "Click on the song you want to add!", Toast.LENGTH_SHORT);
        addedOrRemovedToast.show();
        allTracksListView = view.findViewById(R.id.fragment_music_addtrakcs_listview);
        allTracksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item,
                    R.id.textColorForListView, HomeActivity.mServ.addTracksTitlesToList(-1));
            allTracksListView.setAdapter(adapter);
            ViewGroup.LayoutParams params = allTracksListView.getLayoutParams();
            params.height = 50000;
            allTracksListView.setLayoutParams(params);
            allTracksListView.requestLayout();
            allTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!customTracksList.contains(position)) {
                        view.setBackgroundResource(R.color.colorOregairuPink);
                        customTracksList.add(position);
                        addedOrRemovedToast.cancel();
                        addedOrRemovedToast = Toast.makeText(getContext(),
                                "Added \"" + (allTracksListView.getItemAtPosition(position).toString()) + "\"", Toast.LENGTH_SHORT);
                        addedOrRemovedToast.show();
                    } else {
                        view.setBackgroundResource(0);
                        customTracksList.remove(Integer.valueOf(position));
                        addedOrRemovedToast.cancel();
                        addedOrRemovedToast = Toast.makeText(getContext(),
                                "Removed \"" + (allTracksListView.getItemAtPosition(position).toString()) + "\"", Toast.LENGTH_SHORT);
                        addedOrRemovedToast.show();
                    }
                    tinyDB.putListInt(getString(R.string.prefsCustomTracksList), customTracksList);
                }
            });
        }

        try {
            return view;
        } finally {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int trueHeight = 0;
                    for (int number = 0; number < allTracksListView.getCount(); number++) {
                        trueHeight += allTracksListView.getChildAt(number).getHeight();
                    }
                    ViewGroup.LayoutParams params = allTracksListView.getLayoutParams();
                    params.height = trueHeight + (allTracksListView.getDividerHeight() * (allTracksListView.getCount() - 1));
                    allTracksListView.setLayoutParams(params);
                    allTracksListView.requestLayout();
                    for (int number = 0; number < customTracksList.size(); number++) {
                        allTracksListView.getChildAt(customTracksList.get(number)).setBackgroundResource(R.color.colorOregairuPink);
                    }
                }
            }, 0);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.backPressInMusic = false;
    }
}