package at.fhs.smartsigncapture.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.SignController;
import at.fhs.smartsigncapture.model.Sign;
import at.fhs.smartsigncapture.view.activity.UnityNewDictionaryEntryActivity;
import at.fhs.smartsigncapture.view.activity.UnityPlayActivity;
import at.fhs.smartsigncapture.view.adapter.DictionaryEntriesListViewAdpater;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment {

    private ListView entrieListView;

    private DictionaryEntriesListViewAdpater listViewAdpater;

    private SignController signController;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DictionaryFragment.
     */
    public static DictionaryFragment newInstance() {
        DictionaryFragment fragment = new DictionaryFragment();

        return fragment;
    }


    public DictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFragment();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        if(visible && isResumed()){
            this.listViewAdpater.setEntries(this.signController.getAllSigns());
        }
    }


    private void initFragment() {

        this.signController = SignController.getInstance(this.getActivity());

        entrieListView = (ListView) getView().findViewById(R.id.lvDictionaryEntries);
        entrieListView.setEmptyView(getView().findViewById(R.id.txEmptylistView));

        this.entrieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sign s = (Sign) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), UnityPlayActivity.class);
                i.putExtra(UnityPlayActivity.EXTRAS_KEY_SIGN_DATA,s.getSign());

                startActivity(i);
            }
        });

        this.listViewAdpater = new DictionaryEntriesListViewAdpater(this.getActivity(),this.signController.getAllSigns());

        this.entrieListView.setAdapter(this.listViewAdpater);

        getView().findViewById(R.id.fabNewEntry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(getActivity(), UnityNewDictionaryEntryActivity.class);
                startActivity(i);
            }
        });
    }

}
