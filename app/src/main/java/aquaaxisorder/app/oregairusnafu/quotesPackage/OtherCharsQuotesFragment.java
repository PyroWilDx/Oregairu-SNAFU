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

public class OtherCharsQuotesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quotes_otherchars_fragment, container, false);

        String[] othersQuotesList = {getString(R.string.othersQuotesTitle), getString(R.string.othersQuotes1),
                getString(R.string.othersQuotes2), getString(R.string.othersQuotes3), getString(R.string.othersQuotes4),
                getString(R.string.othersQuotes5), getString(R.string.othersQuotes6), getString(R.string.othersQuotes7),
                getString(R.string.othersQuotes8), getString(R.string.othersQuotes9), getString(R.string.othersQuotes10),
                getString(R.string.othersQuotes11), getString(R.string.othersQuotes12), getString(R.string.othersQuotes13),
                getString(R.string.othersQuotes14), getString(R.string.othersQuotes15)};
        String[] charactersList = {othersQuotesList.length - 1 + " quotes", "Yuigahama Yui (S2/EP2 – 19min 25s)", "Yuigahama Yui (S2/EP4 – 20min 37s)",
                "Hayama Hayato (S2/EP2 – 11min 43s)", "Hayama Hayato (S2/EP10 – 0min 30s)", "Hayama Hayato (S2/EP11 – 21min 26s)",
                "Hikigaya Komachi (S2/EP5 – 3min 26s)", "Hikigaya Komachi (S2/EP10 – 16min 35s)", "Hikigaya Komachi – https://www.youtube.com/watch?v=theDGySseCM", "Yukinoshita Haruno (S2/EP4 – 13min 39s)",
                "Isshiki Iroha (S2/EP10 – 2min 57s)", "Kawasaki Saki (S1/EP5 – 9min 14s)", "Ebina Hina (S1/EP7 – 18min 07s)", "Ebina Hina (S1/EP10 – 12min 26s)",
                "Orimoto Kaori – https://www.youtube.com/watch?v=9gcDlwJTa4k", "^^\' – https://www.youtube.com/watch?v=jyijnQFn5lA"};

        ListView othersListView = view.findViewById(R.id.fragment_quotes_others_listview);
        othersListView.setAdapter(new CustomAdapterQuotes(getContext(), othersQuotesList, charactersList));

        return view;
    }

}
