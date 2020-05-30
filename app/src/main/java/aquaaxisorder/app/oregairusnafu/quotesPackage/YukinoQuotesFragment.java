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

public class YukinoQuotesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quotes_yukino_fragment, container, false);

        String[] yukinoQuotesList = {getString(R.string.yukinoQuotesTitle), getString(R.string.yukinoQuotes1),
                getString(R.string.yukinoQuotes2), getString(R.string.yukinoQuotes3), getString(R.string.yukinoQuotes4),
                getString(R.string.yukinoQuotes5), getString(R.string.yukinoQuotes6), getString(R.string.yukinoQuotes7),
                getString(R.string.yukinoQuotes8), getString(R.string.yukinoQuotes9), getString(R.string.yukinoQuotes10),
                getString(R.string.yukinoQuotes11), getString(R.string.yukinoQuotes12), getString(R.string.yukinoQuotes13),
                getString(R.string.yukinoQuotes14)};
        String[] timingList = {yukinoQuotesList.length - 1 + " quotes", "S1/EP1 – 12min 28s", "S1/EP1 – 16min 02s", "S1/EP1 – 16min 55s",
                "S1/EP2 – 7min 33s", "S1/EP3 – 8min 31s", "S1/EP6 – 9min 28s", "S1/EP6 – 20min 16s", "S1/EP7 – 19min 10s", "S2/EP3 – 6min 14s",
                "S2/EP3 – 12min 25s & 21min 24s", "S2/EP7 – 21min 32s", "S2/EP10 – 7min 11s", "S2/EP11 – 7min 04s", "https://www.youtube.com/watch?v=DfV5yMQ5H6E"};

        ListView yukinoListView = (ListView) view.findViewById(R.id.fragment_quotes_yukino_listview);
        yukinoListView.setAdapter(new CustomAdapterQuotes(getContext(), yukinoQuotesList, timingList));

        return view;
    }

}
