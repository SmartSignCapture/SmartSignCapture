package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.utils.ImageUtils;

/**
 * Created by MartinTiefengrabner on 27/07/15.
 */
public class ContactsRecyclerViewAdpater extends RecyclerView.Adapter {


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public ImageView friendShipStateImageView;
        public TextView userNameTextView;

        public ViewHolder(View v) {
            super(v);

            this.avatarImageView = (ImageView) v.findViewById(R.id.ivAvatar);
            this.friendShipStateImageView = (ImageView) v.findViewById(R.id.ivStatus);
            this.userNameTextView = (TextView) v.findViewById(R.id.txUserName);
        }
    }

    protected List<Friend> friends;

    protected Context context;


    public ContactsRecyclerViewAdpater(Context context, List<Friend> friends) {
        this.friends = friends;
        this.context = context;
    }

    @Override
    public ContactsRecyclerViewAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int i) {
        Friend c = this.friends.get(i);

        FiendContactsRecyclerViewAdapter.ViewHolder viewHolder = (FiendContactsRecyclerViewAdapter.ViewHolder) vh;

        viewHolder.userNameTextView.setText(c.getUserName());

        int stateResID = R.drawable.ic_friends_add;

        viewHolder.friendShipStateImageView.setVisibility(View.VISIBLE);

        switch (c.getState()) {
            case FRIENDS:
                stateResID = R.drawable.ic_friends;
                viewHolder.friendShipStateImageView.setVisibility(View.GONE);
                break;
            case NEEDS_APPROVAL:
                stateResID = R.drawable.ic_friends_accept;
                break;
            case WAITING_FOR_APPROVAL:
                stateResID = R.drawable.ic_friends_approving;
                break;
        }

        if (c.getLocalImage() != null) {
            Bitmap bmp = BitmapFactory.decodeFile(c.getLocalImage());
            if (bmp != null) {

                viewHolder.avatarImageView.setImageBitmap(ImageUtils.GetBitmapClippedCircle(bmp));
            }
        }

        viewHolder.friendShipStateImageView.setImageDrawable(context.getResources().getDrawable(stateResID));
        viewHolder.friendShipStateImageView.setTag(i);
        viewHolder.itemView.setTag(i);

    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        this.notifyDataSetChanged();
        ;
    }

    public List<Friend> getFriends() {
        return this.friends;
    }
}
