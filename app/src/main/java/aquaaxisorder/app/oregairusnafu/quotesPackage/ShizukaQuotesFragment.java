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

public class ShizukaQuotesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quotes_shizuka_fragment, container, false);

        String[] shizukaQuotesList = {getString(R.string.shizukaQuotesTitle), getString(R.string.shizukaQuotes1),
                getString(R.string.shizukaQuotes2), getString(R.string.shizukaQuotes3), getString(R.string.shizukaQuotes4),
                getString(R.string.shizukaQuotes5), getString(R.string.shizukaQuotes6), getString(R.string.shizukaQuotes7),
                getString(R.string.shizukaQuotes8), getString(R.string.shizukaQuotes9), getString(R.string.shizukaQuotes10),
                getString(R.string.shizukaQutoes11), getString(R.string.shizukaQutoes12)};
        String[] timingList = {shizukaQuotesList.length - 1 + " quotes", "S1/EP1 – 3min 23s", "S1/EP12 – 17min 17s", "S1/EP12 – 17min 32s",
                "S2/EP1 – 20min 08s", "S2/EP3 – 13min 00s", "S2/EP8 – 4min 37s", "S2/EP8 – 5min 20s", "S2/EP8 – 5min 50s", "S2/EP8 – 7min 49s",
                "S2/EP8 – 9min 57s", "S2/EP12 – 17min 07s", "S2/EP12 – 17min 20s"};

        ListView shizukaListView = view.findViewById(R.id.fragment_quotes_shizuka_listview);
        shizukaListView.setAdapter(new CustomAdapterQuotes(getContext(), shizukaQuotesList, timingList));

        return view;
    }

}
