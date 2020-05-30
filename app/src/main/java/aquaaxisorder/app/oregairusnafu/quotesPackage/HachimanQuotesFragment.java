package aquaaxisorder.app.oregairusnafu.quotesPackage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import aquaaxisorder.app.oregairusnafu.R;

public class HachimanQuotesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quotes_hachiman_fragment, container, false);

        String[] hachimanQuotesList = {getString(R.string.hachimanQuoteTitle), getString(R.string.hachimanQuotes1),
                getString(R.string.hachimanQuotes2), getString(R.string.hachimanQuotes3), getString(R.string.hachimanQuotes4),
                getString(R.string.hachimanQuotes5), getString(R.string.hachimanQuotes6), getString(R.string.hachimanQuotes7),
                getString(R.string.hachimanQuotes8), getString(R.string.hachimanQuotes9), getString(R.string.hachimanQuotes10),
                getString(R.string.hachimanQuotes11), getString(R.string.hachimanQuotes12), getString(R.string.hachimanQuotes13),
                getString(R.string.hachimanQuotes14), getString(R.string.hachimanQuotes15), getString(R.string.hachimanQuotes16),
                getString(R.string.hachimanQuotes17), getString(R.string.hachimanQuotes18), getString(R.string.hachimanQuotes19),
                getString(R.string.hachimanQuotes20), getString(R.string.hachimanQuotes21), getString(R.string.hachimanQuotes22),
                getString(R.string.hachimanQuotes23), getString(R.string.hachimanQuotes24), getString(R.string.hachimanQuotes25)};
        String[] timingList = {hachimanQuotesList.length - 1 + " quotes", "S1/EP1 – 0min 00s", "S1/EP1 – 9min 36s", "S1/EP1 – 20min 29s",
                "S1/EP2 – 0min 00s", "S1/EP5 – 21min 08s", "S1/EP8 – 8min 55s", "S1/EP8 – 14min 31s", "S1/EP9 – 21min 10s", "S1/EP10 – 19min 16s",
                "S1/EP11 – 7min 21s", "S1/EP11 – 9min 39s", "S1/EP12 – 18min 23s", "S1/EP13 – 0min 00", "S2/EP1 – 4min 47s", "S2/EP2 – 11min 35s",
                "S2/EP5 – 20min 20s", "S2/EP5 – 21min 52s", "S2/EP8 – 16min 25s", "S2/EP8 – 16min 50s", "S2/EP8 – 16min 54s", "S2/EP10 – 15min 06s",
                "S2/EP11 – 14min 17s", "S2/EP12 – 21min 19s", "S2/EP13 – 20min 29s", "S2/EP13 – 21min 47s"};

        ListView hachimanListView = view.findViewById(R.id.fragment_quotes_hachiman_listview);
        hachimanListView.setAdapter(new CustomAdapterQuotes(getContext(), hachimanQuotesList, timingList));

        return view;
    }

}
