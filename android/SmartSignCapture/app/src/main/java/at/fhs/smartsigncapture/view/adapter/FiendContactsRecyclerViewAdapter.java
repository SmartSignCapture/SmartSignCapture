package at.fhs.smartsigncapture.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import at.fhs.smartsigncapture.model.Friend;

/**
 * Created by MartinTiefengrabner on 21/07/15.
 */
public class FiendContactsRecyclerViewAdapter extends ContactsRecyclerViewAdpater{

    public interface ContactRecyclerCallback{
        public void onItemClicked(Friend friend);
    }

    private ContactRecyclerCallback listener;

    public FiendContactsRecyclerViewAdapter(Context context, ContactRecyclerCallback listener, List<Friend> friends){
        super(context, friends);
        this.listener = listener;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int i) {

        FiendContactsRecyclerViewAdapter.ViewHolder viewHolder = (FiendContactsRecyclerViewAdapter.ViewHolder) vh;
        super.onBindViewHolder(viewHolder, i);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(friends.get((int) v.getTag()));
            }
        });
    }

}
