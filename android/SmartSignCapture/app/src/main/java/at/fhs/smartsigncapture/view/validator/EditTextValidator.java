package at.fhs.smartsigncapture.view.validator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by MartinTiefengrabner on 15/07/15.
 */
public class EditTextValidator implements TextWatcher, View.OnFocusChangeListener {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validate();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            validate();
        }
    }

    public interface ExternalValidator {
        public void validateTextField(EditText editText, EditTextValidator sender);
    }

    private static final String PASSWORD_PATTERN = "[0-9a-zA-Z@#$%\\s]{6,20}";

    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

    private EditText editText;

    private String error;
    private String emptyError;
    private String invalidError;

    private ExternalValidator externalValidator;

    private boolean valid;

    private boolean mandatory;

    private Context context;

    public EditTextValidator(Context context, EditText editText, boolean automaticValidation, boolean mandatory, String emptyErrorMsg, String invalidErrorMsg, ExternalValidator externalValidator) {
        this.context = context;
        this.emptyError = emptyErrorMsg;
        this.invalidError = invalidErrorMsg;
        this.editText = editText;
        this.externalValidator = externalValidator;
        this.valid = false;
        this.mandatory = mandatory;

        if (automaticValidation) {
            //this.editText.addTextChangedListener(this);
            this.editText.setOnFocusChangeListener(this);
        }
    }

    public boolean validate() {

        this.valid = this.checkMandatoryField();

        if (this.valid) {
            switch (this.editText.getInputType()) {
                case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | EditorInfo.TYPE_CLASS_TEXT:
                    this.valid = Patterns.EMAIL_ADDRESS.matcher(this.editText.getText()).matches();
                    break;
                case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD | EditorInfo.TYPE_CLASS_TEXT:
                    this.valid = passwordPattern.matcher(this.editText.getText()).matches();
                    break;
                default:
                    this.valid = true;
            }
            if (this.valid) {
                if (this.externalValidator != null) {
                    this.externalValidator.validateTextField(this.editText, this);
                }
            } else {
                this.error = this.invalidError;
            }
        }

        if (!this.valid) {
            this.editText.setError(this.error);
        }

        return this.valid;
    }

    public boolean checkMandatoryField() {

        boolean result = false;

        if (this.mandatory) {
            if (this.editText.getText().length() > 0) {
                result = true;
            } else {
                this.error = this.emptyError;
                result = false;
            }
        } else {
            result = true;
        }

        return result;
    }

    public void onExternalValidationOK() {
        this.valid = true;
        this.editText.setError(null);
    }

    public void onExternalValidationError(String errorMessage) {
        this.valid = false;

        if (!this.valid) {
            this.error = errorMessage;
            this.editText.setError(error);
        }
    }

    public boolean isValid() {
        return this.valid;
    }
}
