package at.fhs.smartsigncapture.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.model.Message;
import at.fhs.smartsigncapture.view.activity.MessageThreadActivity;
import at.fhs.smartsigncapture.view.adapter.LastMessagesListViewAdapter;
import at.fhs.smartsigncapture.view.adapter.MessageRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class MessagesFragment extends Fragment {


    private RecyclerView messagesRecyclerView;

    private MessageRecyclerViewAdapter messageRecyclerViewAdapter;

    private LinearLayoutManager layoutManager;

    private ListView messagesListView;
    private LastMessagesListViewAdapter messageListViewAdapter;

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();

        return fragment;
    }

    private MessageController messageController;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageController = MessageController.getInstance(this.getActivity());
        List<Message> newestMessages = messageController.newestMessagesPerUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFragment();
        //initRecyclerView();
        initListView();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        if(visible && isResumed()){
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                getFragmentManager().beginTransaction().remove(prev).commit();
            }
        }

    }

    private void initListView() {
        this.messagesListView = (ListView) getView().findViewById(R.id.lvMessages);

        this.messagesListView.setEmptyView(getView().findViewById(R.id.txEmptylistView));

        this.messageListViewAdapter = new LastMessagesListViewAdapter(this.getActivity(), this.messageController.newestMessagesPerUser());

        this.messagesListView.setAdapter(this.messageListViewAdapter);

        this.messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = (Message) messageListViewAdapter.getItem(position);
                showMessageThread(msg);
            }
        });
    }

    private void initFragment() {
        getView().findViewById(R.id.fabNewMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }


                // Create and show the dialog.
                SelectReceiverFragment newFragment = SelectReceiverFragment.newInstance();
                newFragment.show(ft, "dialog");
            }
        });
    }

    private void showMessageThread(Message msg){
        Intent i = new Intent(this.getActivity(), MessageThreadActivity.class);

        if(msg.isReceivedMessage()) {
            i.putExtra(MessageThreadActivity.EXTRAS_BUNDLE_KEY_CONTACT_ID, msg.getSenderID());
        }
        else{
            i.putExtra(MessageThreadActivity.EXTRAS_BUNDLE_KEY_CONTACT_ID, msg.getReceiverID());
        }

        startActivity(i);

    }

    private void initRecyclerView() {
        //this.messagesRecyclerView = (RecyclerView) getView().findViewById(R.id.rvMessages);
        this.messagesRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.messagesRecyclerView.setLayoutManager(this.layoutManager);


        this.messageRecyclerViewAdapter = new MessageRecyclerViewAdapter(this.getActivity(), messageController.newestMessagesPerUser());
        this.messagesRecyclerView.setAdapter(this.messageRecyclerViewAdapter);
    }

}
