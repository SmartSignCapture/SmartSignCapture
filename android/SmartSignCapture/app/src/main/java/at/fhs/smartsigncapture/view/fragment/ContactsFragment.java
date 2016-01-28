package at.fhs.smartsigncapture.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.ContactsController;
import at.fhs.smartsigncapture.model.Friend;
import at.fhs.smartsigncapture.view.activity.MessageThreadActivity;
import at.fhs.smartsigncapture.view.adapter.FiendContactsRecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment implements FiendContactsRecyclerViewAdapter.ContactRecyclerCallback {


    private OnFragmentInteractionListener mListener;

    private ContactsController contactsController;

    private RecyclerView contactsRecyclerView;
    private FiendContactsRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;

    private EditText searchEditText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();

        return fragment;
    }

    public ContactsFragment() {
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
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contactsController = ContactsController.getInstance(this.getActivity());

        this.contactsRecyclerView = (RecyclerView) getView().findViewById(R.id.rvContacts);
        this.contactsRecyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.contactsRecyclerView.setLayoutManager(this.layoutManager);


        this.recyclerViewAdapter = new FiendContactsRecyclerViewAdapter(this.getActivity(), this, contactsController.getFriends());
        this.contactsRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.searchEditText = (EditText) getView().findViewById(R.id.edSearchTermInput);

        this.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = s.toString();

                if (text.length() >= 2) {
                    contactsController.searchContacts(text, new Callback<List<Friend>>() {
                        @Override
                        public void success(List<Friend> friends) {
                            recyclerViewAdapter.setFriends(friends);
                        }

                        @Override
                        public void failure(String errorMessage) {
                            recyclerViewAdapter.setFriends(new ArrayList<Friend>());
                        }
                    });
                } else if (text.length() == 0) {
                    recyclerViewAdapter.setFriends(contactsController.getFriends());
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        if (visible && isResumed()) {
            this.recyclerViewAdapter.setFriends(this.contactsController.getContacts());
            this.searchEditText.setText("");
        }

    }

    @Override
    public void onItemClicked(Friend friend) {
        if (friend.getState() != Friend.FriendshipState.FRIENDS) {
            this.contactsController.changeContactState(friend, new Callback<Friend>() {
                @Override
                public void success(Friend friend) {
                    recyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(String errorMessage) {

                }
            });
        }
        else{
            Intent i = new Intent(this.getActivity(), MessageThreadActivity.class);
            i.putExtra(MessageThreadActivity.EXTRAS_BUNDLE_KEY_CONTACT_ID,friend.getId());
            startActivity(i);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.recyclerViewAdapter.setFriends(this.contactsController.getContacts());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
