package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.model.Tag;
import at.fhs.smartsigncapture.utils.DateUtils;

/**
 * Created by MartinTiefengrabner on 18/09/15.
 */
public class DictionaryEntriesListViewAdpater extends BaseAdapter {

    public static class ViewHolder {

        public TextView nameTextView;
        public TextView dateTextView;
        public FlowLayout tagsWrapper;

    }


    protected Context context;
    protected List<Sign> entries;

    public DictionaryEntriesListViewAdpater(Context context, List<Sign> entries) {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return this.entries.size();
    }

    @Override
    public Object getItem(int position) {
        return this.entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.entries.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        Sign s = this.entries.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_dictionary_entry, parent, false);

            holder = new ViewHolder();

            holder.dateTextView = (TextView) convertView.findViewById(R.id.txDate);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.txName);
            holder.tagsWrapper = (FlowLayout) convertView.findViewById(R.id.flTags);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }


        holder.dateTextView.setText(this.getDateString(s.getDate()));
        holder.nameTextView.setText(s.getName());

        holder.tagsWrapper.removeAllViews();

        int i = 0;



        for (Tag t : s.getTags()) {
            //TextView tagTextView = new TextView(context,null,R.style.Token);
            TextView tagTextView = new TextView(new ContextThemeWrapper(context, R.style.Token), null, 0);


            tagTextView.setText(t.getTag());
            //tagTextView.setLayoutParams(params);
            holder.tagsWrapper.addView(tagTextView);


            i++;


        }

        return convertView;
    }

    protected CharSequence getDateString(Date date) {
        CharSequence dateString;
        Date today = new Date();

        dateString = DateUtils.formatSameDayTime(date.getTime(), today.getTime(), java.text.DateFormat.LONG, java.text.DateFormat.SHORT);

        return dateString;
    }

    public void setEntries(List<Sign> entries) {
        this.entries = entries;
        this.notifyDataSetChanged();
    }
}
