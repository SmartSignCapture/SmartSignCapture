package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.utils.DateUtils;
import at.fhs.smartsigncapture.utils.ImageUtils;

/**
 * Created by MartinTiefengrabner on 29/07/15.
 */
public class LastMessagesListViewAdapter extends BaseAdapter{

    public static class ViewHolder {

        public ImageView avatarImageView;
        public TextView dateTextView;
        public TextView userNameTextView;

    }


    protected Context context;
    protected List<Message> messages;

    public LastMessagesListViewAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messages  =messages;
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Object getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
     return this.messages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        Message m = this.messages.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_latest_message, parent, false);

            holder = new ViewHolder();

            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.txDate);
            holder.userNameTextView = (TextView) convertView.findViewById(R.id.txUserName);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        if(m.isReceivedMessage()) {
            holder.userNameTextView.setText(m.getSendingFriend().getUserName());

            if(m.getSendingFriend().getLocalImage() != null) {
                holder.avatarImageView.setImageBitmap(ImageUtils.GetBitmapClippedCircle(BitmapFactory.decodeFile(m.getSendingFriend().getLocalImage())));
            }

        }
        else{
            holder.userNameTextView.setText(m.getReceivingFriend().getUserName());

            if(m.getReceivingFriend().getLocalImage() != null) {
                holder.avatarImageView.setImageBitmap(ImageUtils.GetBitmapClippedCircle(BitmapFactory.decodeFile(m.getReceivingFriend().getLocalImage())));
            }
        }

        holder.dateTextView.setText(this.getDateString(m.getDate()));

        return convertView;
    }

    protected CharSequence getDateString(Date date) {
        CharSequence dateString;
        Date today = new Date();

        dateString = DateUtils.formatSameDayTime(date.getTime(), today.getTime(), java.text.DateFormat.LONG, java.text.DateFormat.SHORT);

        return dateString;
    }

    public void setMessages(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();
    }
}
