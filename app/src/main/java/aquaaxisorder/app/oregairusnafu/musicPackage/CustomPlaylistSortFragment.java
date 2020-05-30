package aquaaxisorder.app.oregairusnafu.musicPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.InputFilterMinMax;
import aquaaxisorder.app.oregairusnafu.R;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.TinyDB;

public class CustomPlaylistSortFragment extends Fragment {

    private TinyDB tinyDB;
    private ArrayList<Integer> customTracksList;
    private ArrayAdapter<String> adapter;
    private int chosePosition, selectedPosition;
    private Toast movedToast;
    private ListView customTracksListView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_sortcustomplaylist_fragment, container, false);

        HomeActivity.backPressInMusic = true;

        movedToast = Toast.makeText(getContext(), R.string.clickToSort, Toast.LENGTH_SHORT);
        movedToast.show();

        tinyDB = new TinyDB(getContext());
        customTracksList = new ArrayList<>(tinyDB.getListInt(getString(R.string.prefsCustomTracksList)));

        customTracksListView = view.findViewById(R.id.fragment_music_sortcustomplaylist_listview);


        if (getContext() != null) {
            adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item, R.id.textColorForListView,
                    setNumberForTracks());
            customTracksListView.setAdapter(adapter);

            customTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    selectedPosition = position;
                    chosePosition = 0;
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    final EditText choosePositionEditText = new EditText(getContext());
                    choosePositionEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] filterArray = new InputFilter[]{new InputFilterMinMax("1", String.valueOf(customTracksList.size()))};
                    choosePositionEditText.setFilters(filterArray);
                    alert.setTitle("Write a number to move the song at this place!");
                    alert.setMessage("Position :");
                    alert.setView(choosePositionEditText);
                    alert.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                chosePosition = Integer.parseInt(choosePositionEditText.getText().toString());
                                int subStringNumber = 3;
                                if (selectedPosition >= 9) {
                                    subStringNumber = 4;
                                }
                                String trackTitle = customTracksListView.getItemAtPosition(selectedPosition).toString().substring(subStringNumber);
                                movedToast.cancel();
                                movedToast = Toast.makeText(getContext(), "\"" + trackTitle + "\" moved to : " + chosePosition, Toast.LENGTH_SHORT);
                                movedToast.show();
                                chosePosition -= 1;
                                int trackToMove = customTracksList.get(selectedPosition);
                                customTracksList.remove(selectedPosition);
                                customTracksList.add(chosePosition, trackToMove);
                                tinyDB.putListInt(getString(R.string.prefsCustomTracksList), customTracksList);
                                if (getContext() != null) {
                                    adapter = new ArrayAdapter<>(getContext(), R.layout.music_listview_item, R.id.textColorForListView,
                                            setNumberForTracks());
                                    adapter.notifyDataSetChanged();
                                    customTracksListView.setAdapter(adapter);
                                    customTracksListView.invalidate();
                                }
                            } catch (NumberFormatException ignored) {
                                movedToast.cancel();
                                movedToast = Toast.makeText(getContext(), "Song didn't move.", Toast.LENGTH_SHORT);
                                movedToast.show();
                            }
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.show();
                }
            });
        }

        return view;
    }

    private ArrayList<String> setNumberForTracks() {

        ArrayList<String> customTracksTitleList = new ArrayList<>(HomeActivity.mServ.addTracksTitlesToList(0));
        ArrayList<String> returnedList = new ArrayList<>();
        for (int number = 0; number < customTracksTitleList.size(); number++) {
            String trackTitle = customTracksTitleList.get(number);
            trackTitle = (number + 1) + ") " + trackTitle;
            returnedList.add(trackTitle);
        }
        return returnedList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.backPressInMusic = false;
    }
}
