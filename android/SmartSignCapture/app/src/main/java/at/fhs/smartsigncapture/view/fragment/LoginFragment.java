package at.fhs.smartsigncapture.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.UserController;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements  View.OnClickListener{



    public interface OnFragmentInteractionListener {
        public void onSuccessFullyLoggedIn();
        public void onRegisterButtonClicked();
    }

    private OnFragmentInteractionListener mListener;

    private EditText emailTextView;
    private EditText passwordTextView;
    private ViewGroup loginForm;
    private ProgressBar loginSpinner;

    private UserController userController;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment login.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(UserController userController) {
        LoginFragment fragment = new LoginFragment();
        fragment.userController = userController;
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    //region Override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //endregion

    //region Private

    private void initFragment(){

        this.emailTextView = (EditText) getView().findViewById(R.id.edEmail);
        this.passwordTextView = (EditText) getView().findViewById(R.id.edPasword);
        this.loginForm = (ViewGroup) getView().findViewById(R.id.llLoginForm);
        this.loginSpinner = (ProgressBar) getView().findViewById(R.id.pbLoginSpinner);

        hideLoadingState();

        getView().findViewById(R.id.btnLogin).setOnClickListener(this);
        getView().findViewById(R.id.btnRegister).setOnClickListener(this);
    }

    //endregion

    private void showLoadingState(){
        this.loginForm.setEnabled(false);
        for(int i = 0 ; i < this.loginForm.getChildCount(); i++){
            this.loginForm.getChildAt(i).setEnabled(false);
        }
        this.loginSpinner.setVisibility(View.VISIBLE);

    }

    private void hideLoadingState(){
        this.loginForm.setEnabled(true);
        for(int i = 0 ; i < this.loginForm.getChildCount(); i++){
            this.loginForm.getChildAt(i).setEnabled(true);
        }
        this.loginSpinner.setVisibility(View.GONE);

    }

    //region Interface Implementation
    //region  View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:

                showLoadingState();

                this.userController.loginUser(this.emailTextView.getText().toString(), this.passwordTextView.getText().toString(), new Callback<Void>() {
                    @Override
                    public void success(Void aVoid) {
                        hideLoadingState();
                        mListener.onSuccessFullyLoggedIn();
                    }

                    @Override
                    public void failure(String errorMessage) {
                        if(errorMessage == UserController.ERROR_MESSAGE_CREDENTIALS_WRONG){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoadingState();
                                    emailTextView.setError(getResources().getString(R.string.login_error_wrong_credentials));
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.btnRegister:
                this.mListener.onRegisterButtonClicked();
                break;
        }
    }
    //endregion
    //endregion
}
