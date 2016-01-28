package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Message;

/**
 * Created by MartinTiefengrabner on 29/07/15.
 */
public class MessageRecyclerViewAdapter extends RecyclerView.Adapter {


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public TextView dateTextView;
        public TextView userNameTextView;

        public ViewHolder(View v) {
            super(v);

            this.avatarImageView = (ImageView) v.findViewById(R.id.ivAvatar);
            this.dateTextView = (TextView) v.findViewById(R.id.txDate);
            this.userNameTextView = (TextView) v.findViewById(R.id.txUserName);
        }
    }

    protected List<Message> messages;

    protected Context context;




    public MessageRecyclerViewAdapter(Context context, List<Message> messages){
        this.messages = messages;
        this.context = context;
    }

    @Override
    public MessageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_latest_message, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int i) {
        Message m = this.messages.get(i);

        MessageRecyclerViewAdapter.ViewHolder viewHolder = (MessageRecyclerViewAdapter.ViewHolder) vh;

        if(m.isReceivedMessage()) {
            viewHolder.userNameTextView.setText(m.getSendingFriend().getUserName());
        }
        else{
            viewHolder.userNameTextView.setText(m.getReceivingFriend().getUserName());
        }

        viewHolder.dateTextView.setText(this.getDateString(m.getDate()));

    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public void setMessages(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();;
    }

    private CharSequence getDateString(Date date) {
        CharSequence dateString;
        Date today = new Date();

        dateString = android.text.format.DateUtils.formatSameDayTime(date.getTime(), today.getTime(), java.text.DateFormat.LONG, java.text.DateFormat.SHORT);

        return dateString;
    }

    public List<Message> getMessages(){
        return this.messages;
    }
}