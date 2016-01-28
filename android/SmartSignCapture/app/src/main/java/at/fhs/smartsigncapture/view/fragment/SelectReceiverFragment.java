package at.fhs.smartsigncapture.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.ContactsController;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.view.OnRecyclerItemClickListener;
import at.fhs.smartsigncapture.view.activity.UnityNewMessageActivity;
import at.fhs.smartsigncapture.view.adapter.ContactsRecyclerViewAdpater;


public class SelectReceiverFragment extends DialogFragment  {


    private ContactsController contactsController;

    private RecyclerView contactsRecyclerView;
    private ContactsRecyclerViewAdpater recyclerViewAdapter;
    private LinearLayoutManager layoutManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectReceiverFragment newInstance() {
        SelectReceiverFragment fragment = new SelectReceiverFragment();

        return fragment;
    }

    public SelectReceiverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_receiver, container, false);

        if(this.getShowsDialog()){
            this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.initFragment();

    }


    @Override
    public void onResume() {
        super.onResume();
        this.recyclerViewAdapter.setFriends(this.contactsController.getFriends());
    }

    private void initFragment() {
        contactsController = ContactsController.getInstance(this.getActivity());

        this.contactsRecyclerView = (RecyclerView) getView().findViewById(R.id.rvContacts);
        this.contactsRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.contactsRecyclerView.setLayoutManager(this.layoutManager);


        this.recyclerViewAdapter = new ContactsRecyclerViewAdpater(this.getActivity(), contactsController.getFriends());
        this.contactsRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(this.getActivity(), new OnRecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                createMessage(recyclerViewAdapter.getFriends().get(position));
            }
        }));
        this.contactsRecyclerView.setAdapter(this.recyclerViewAdapter);
    }

    private void createMessage(Friend friend){
        Intent i = new Intent(this.getActivity(), UnityNewMessageActivity.class);
        i.putExtra(UnityNewMessageActivity.EXTRAS_KEY_RECEIVER_ID, friend.getId());

        startActivity(i);
    }

    //region Inteface Implementations

    //endregion
}