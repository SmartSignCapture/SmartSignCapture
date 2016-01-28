package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.utils.ImageUtils;

/**
 * Created by MartinTiefengrabner on 29/07/15.
 */
public class MessageThreadListAdapter extends LastMessagesListViewAdapter {

    public MessageThreadListAdapter(Context context, List<Message> messages) {
        super(context, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        Message m = this.messages.get(position);



        if (m.isReceivedMessage()) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_message_thread_received, parent, false);
            ((TextView) convertView.findViewById(R.id.txUserName)).setText(m.getSendingFriend().getUserName());

        } else {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_message_thread_sent, parent, false);
            ((TextView) convertView.findViewById(R.id.txUserName)).setText(context.getString(R.string.me));

        }

        if(m.getSendingFriend().getLocalImage() != null) {
            ((ImageView) convertView.findViewById(R.id.ivAvatar)).setImageBitmap(ImageUtils.GetBitmapClippedCircle(BitmapFactory.decodeFile(m.getSendingFriend().getLocalImage())));
        }

        ((TextView) convertView.findViewById(R.id.txDate)).setText(this.getDateString(m.getDate()));

        return convertView;
    }
}
