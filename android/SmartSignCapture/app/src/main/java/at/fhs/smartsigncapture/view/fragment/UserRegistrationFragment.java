package at.fhs.smartsigncapture.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import at.fhs.smartsigncapture.Callback;
import at.fhs.smartsigncapture.R;
import at.fhs.smartsigncapture.controller.UserController;
import at.fhs.smartsigncapture.view.validator.EditTextValidator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserRegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserRegistrationFragment extends Fragment implements View.OnClickListener, EditTextValidator.ExternalValidator {


    public interface OnFragmentInteractionListener {
        public void onCancel();
        public void onUserRegistered();
    }

    private UserController userController;

    private OnFragmentInteractionListener mListener;

    private List<EditTextValidator> validators;

    private EditText userNameEditText;
    private EditText firstNameEditText;
    private EditText passwordEditText;
    private EditText passwordRetypeEditText;
    private EditText emailEditText;
    private EditText lastNameEditText;
    private ViewGroup formWrapper;
    private ViewGroup spinnerWrapper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserRegistrationFragment.
     */
    public static UserRegistrationFragment newInstance(UserController userController) {
        UserRegistrationFragment fragment = new UserRegistrationFragment();
        fragment.userController = userController;
        return fragment;
    }

    public UserRegistrationFragment() {
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
        return inflater.inflate(R.layout.fragment_user_registration, container, false);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFragment();
    }
    //endregion

    //region Private


    private void initFragment() {

        this.getView().findViewById(R.id.btnRegister).setOnClickListener(this);

        userNameEditText = (EditText) this.getView().findViewById(R.id.edUserName);
        firstNameEditText = (EditText) this.getView().findViewById(R.id.edFirstName);
        passwordEditText = (EditText) this.getView().findViewById(R.id.edPassword);
        passwordRetypeEditText = (EditText) this.getView().findViewById(R.id.edPasswordRetype);
        emailEditText = (EditText) this.getView().findViewById(R.id.edEmail);
        lastNameEditText = (EditText) this.getView().findViewById(R.id.edLastName);


        this.formWrapper = (ViewGroup) this.getView().findViewById(R.id.formWrapper);
        this.spinnerWrapper = (ViewGroup) this.getView().findViewById(R.id.spinnerWrapper);

        this.hideSpinner();

        this.validators = new ArrayList<EditTextValidator>();
        validators.add(
                new EditTextValidator(
                        this.getActivity(),
                        this.emailEditText,
                        true,
                        true,
                        getResources().getString(R.string.validation_error_email_address_empty),
                        getResources().getString(R.string.validation_error_email_address),
                        this)
        );

        validators.add(
                new EditTextValidator(
                        this.getActivity(),
                        this.passwordEditText,
                        true,
                        true,
                        getResources().getString(R.string.validation_error_password_empty),
                        getResources().getString(R.string.validation_error_password),
                        this)
        );

        validators.add(
                new EditTextValidator(
                        this.getActivity(),
                        this.userNameEditText,
                        true,
                        true,
                        getResources().getString(R.string.validation_error_user_name_empty),
                        null,
                        this)
        );

        validators.add(
                new EditTextValidator(
                        this.getActivity(),
                        this.firstNameEditText,
                        true,
                        true,
                        getResources().getString(R.string.validation_error_first_name_empty),
                        null,
                        this)
        );

        validators.add(
                new EditTextValidator(
                        this.getActivity(),
                        this.lastNameEditText,
                        true,
                        true,
                        getResources().getString(R.string.validation_error_last_name_empty),
                        null,
                        this)
        );
    }

    //endregion

    //region Private

    private void showSpinner() {
        this.spinnerWrapper.setVisibility(View.VISIBLE);

        for(int i=0; i < this.formWrapper.getChildCount(); i++){
            this.formWrapper.getChildAt(i).setEnabled(false);
        }
    }

    private void hideSpinner() {
        this.spinnerWrapper.setVisibility(View.GONE);

        for(int i=0; i < this.formWrapper.getChildCount(); i++){
            this.formWrapper.getChildAt(i).setEnabled(true);
        }
    }

    private void registerUser() {

        boolean valid = true;

        for (EditTextValidator validator : this.validators) {
            valid &= validator.validate();
        }

        if(!valid){
            return;
        }

        if (!this.passwordEditText.getText().toString().equals(this.passwordRetypeEditText.getText().toString())){
            ((EditText) getView().findViewById(R.id.edPasswordRetype)).setError(getResources().getString(R.string.validation_error_password_retype));
            return;
        }

        showSpinner();

        this.userController.registerUser(
                this.emailEditText.getText().toString(),
                this.userNameEditText.getText().toString(),
                this.firstNameEditText.getText().toString(),
                this.lastNameEditText.getText().toString(),
                this.passwordEditText.getText().toString(),
                new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {

                        hideSpinner();
                        mListener.onUserRegistered();
                    }

                    @Override
                    public void failure(String errorMessage) {
                        hideSpinner();
                    }
                }
        );
    }

    //endregion

    //region Interface Implementation

    //region OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                this.registerUser();
                break;
        }
    }

    //endregion

    //region EditTextValidator.ExternalValidator
    @Override
    public void validateTextField(EditText editText, final EditTextValidator sender) {
        switch (editText.getId()){
            case R.id.edEmail:
                this.userController.isEmailAddressAvailable(editText.getText().toString(), new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        if(aBoolean) {
                            sender.onExternalValidationOK();
                        }
                        else{
                            sender.onExternalValidationError(getResources().getString(R.string.validation_error_email_address_taken));
                        }
                    }

                    @Override
                    public void failure(String errorMsg) {
                        sender.onExternalValidationError(getResources().getString(R.string.validation_error_server_not_reachable));
                    }
                });
                break;
            case R.id.edUserName:
                this.userController.isUserNameAvailable(editText.getText().toString(), new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean) {
                        if(aBoolean) {
                            sender.onExternalValidationOK();
                        }
                        else{
                            sender.onExternalValidationError(getResources().getString(R.string.validation_error_email_address_taken));
                        }
                    }

                    @Override
                    public void failure(String errorMsg) {
                        sender.onExternalValidationError(getResources().getString(R.string.validation_error_server_not_reachable));
                    }
                });
                break;
            default:
                sender.onExternalValidationOK();
        }
    }


    //endregion


    //endregion
}
