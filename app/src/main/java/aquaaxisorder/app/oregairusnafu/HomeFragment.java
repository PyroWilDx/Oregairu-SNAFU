package aquaaxisorder.app.oregairusnafu;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        TextView animeTitleTextView = view.findViewById(R.id.fragment_home_animeTitle_textview);

        String animeTitle = getString(R.string.animeName);
        SpannableString ssAnimeTitle;
        ssAnimeTitle = new SpannableString(animeTitle);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuBlue)), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuPink)), 7, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuBlue)), 11, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuPink)), 14, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuBlue)), 35, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssAnimeTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorOregairuPink)), 45, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        animeTitleTextView.setText(ssAnimeTitle);

        return view;
    }

}
