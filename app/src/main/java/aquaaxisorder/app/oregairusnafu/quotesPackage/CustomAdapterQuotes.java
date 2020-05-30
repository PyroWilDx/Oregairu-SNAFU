package aquaaxisorder.app.oregairusnafu.quotesPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import aquaaxisorder.app.oregairusnafu.R;

public class CustomAdapterQuotes extends BaseAdapter {
    private final String[] quoteList;
    private final String[] timingList;
    private final Context mContext;

    CustomAdapterQuotes(Context mContext, String[] quoteList, String[] timingList) {
        this.mContext = mContext;
        this.quoteList = quoteList;
        this.timingList = timingList;
    }

    private class ViewHolder {

        private final TextView quoteTextView;
        private final TextView timingTextView;

        private ViewHolder(View row) {
            quoteTextView = row.findViewById(R.id.quotes_textview);
            timingTextView = row.findViewById(R.id.quotes_timing_textview);
        }
    }

    @Override
    public int getCount() {
        return quoteList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.quotes_listview_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.quoteTextView.setText(this.quoteList[position]);
        holder.timingTextView.setText(this.timingList[position]);

        return view;
    }
}
