package at.fhs.smartsigncapture.view.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.view.fragment.ContactsFragment;
import at.fhs.smartsigncapture.view.fragment.DictionaryFragment;
import at.fhs.smartsigncapture.view.fragment.MessagesFragment;

public class HomeActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener, ContactsFragment.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment result = null;
            switch (i) {
                case 0:
                    result = MessagesFragment.newInstance();
                    break;
                case 1:
                    result = ContactsFragment.newInstance();
                    break;
                case 2:
                    result = DictionaryFragment.newInstance();
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String result = "";
            switch (position) {
                case 0:
                    result = getResources().getString(R.string.home_tab_title_messages);
                    break;
                case 1:
                    result = getResources().getString(R.string.home_tab_title_messages);
                    break;
            }

            return result;
        }
    }


    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        this.initActionBar();

    }

    private void initActionBar() {
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        //Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };


        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_actionbar_messages).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_actionbar_contacts).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_actionbar_dictionary).setTabListener(tabListener));
    }

}
