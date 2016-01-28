package at.fhs.smartsigncapture.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.MessageController;
import at.fhs.smartsigncapture.controller.SignController;
import at.fhs.smartsigncapture.controller.UserController;
import at.fhs.smartsigncapture.data.API.adapter.SSCUserAPIAdapter;
import at.fhs.smartsigncapture.data.GCM.SSCInstanceIDListenerService;
import at.fhs.smartsigncapture.view.fragment.HomeFragment;
import at.fhs.smartsigncapture.view.fragment.LoginFragment;
import at.fhs.smartsigncapture.view.fragment.UserRegistrationFragment;


public class MainActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener, UserRegistrationFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener {

    //region Attributes

    private Fragment currentFragment = null;
    private UserController userController;

    //endregion

    //region Override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SSCSharedPreferncesHandler.cleanSharedPreferences(this);

        this.checkGooglePlayServices();

        startService(new Intent(this, SSCInstanceIDListenerService.class));

        setContentView(R.layout.activity_main);
        SSCUserAPIAdapter adapter = new SSCUserAPIAdapter();
        this.userController = UserController.getInstance(this);
        MessageController.getInstance(this.getApplicationContext());
        SignController.getInstance(this.getApplicationContext());
        if (this.userController.getCurrentUser() == null) {
            showLoginFragment();
        } else {
            showHomeFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.checkGooglePlayServices();
    }

    //endregion

    //region Private

    private void checkGooglePlayServices() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(result, this, 123).show();
        }
    }

    private void showLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance(this.userController);
        showFragment(loginFragment);
    }

    private void showRegisterFragment() {
        UserRegistrationFragment registrationFragment = UserRegistrationFragment.newInstance(this.userController);
        showFragment(registrationFragment);
    }

    private void showHomeFragment() {
        //HomeFragment homeFragment = HomeFragment.newInstance();
        //showFragment(homeFragment);
        Intent i = new Intent(this, HomeActivity.class);

        startActivity(i);
        finish();
    }

    private void showFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.replace(R.id.fragmentWrapper, fragment)
                    //.remove(currentFragment)
                    //.add(fragment, "")
                    .addToBackStack(null)
                            //.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out)
                    .commit();
        } else {
            transaction
                    .add(R.id.fragmentWrapper, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        this.currentFragment = fragment;
    }

    //endregion

    //region Interface Implementations

    //region LoginFragment.OnFragmentInteractionListener

    @Override
    public void onSuccessFullyLoggedIn() {
        showHomeFragment();
    }

    @Override
    public void onRegisterButtonClicked() {
        showRegisterFragment();
    }


    //endregion

    //region UserRegistrationFragment.OnFragmentInteractionListener

    @Override
    public void onCancel() {

    }

    @Override
    public void onUserRegistered() {
        showHomeFragment();
    }
    //endregion

    //region  HomeFragment.OnFragmentInteractionListener
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //endregion


    //endregion
}
