package com.example.daniel.meetkai_test.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.daniel.meetkai_test.Interfaces.OnChangeFragmentListener;
import com.example.daniel.meetkai_test.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountFragment extends Fragment {
    // Fragment Views
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView weakPasswordTextView;
    private TextView confirmPasswordErrorTextView;
    private TextView errorMessageTextView;
    private TextView usernameTakenTextView;
    // Fragment Variables
    private boolean isWeakPasswordEnable;
    private boolean isPasswordMatch;
    private boolean usernameFilled, passwordFilled, confirmedPasswordFilled;
    // Interfaces for Authentication Container to implement
    private OnChangeFragmentListener onChangeFragmentListener;
    public interface CreateAccountFragmentListener {
        void onEnableCreateAccountButton(boolean enable);
    }
    private CreateAccountFragmentListener createAccountFragmentListener;


    /***
     * instantiates CreateAccountFragment object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        //Initialize variable
        isWeakPasswordEnable = false;
        isPasswordMatch = false;
        usernameFilled = false;
        passwordFilled = false;
        confirmedPasswordFilled = false;

        //Initialize all of the views
        usernameEditText = (EditText) view.findViewById(R.id.user_name_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirm_password_edit_text);
        weakPasswordTextView = (TextView) view.findViewById(R.id.weak_password_error);
        confirmPasswordErrorTextView = (TextView) view.findViewById(R.id.confirm_password_error);
        usernameTakenTextView = (TextView) view.findViewById(R.id.user_name_error_text_view);
        errorMessageTextView = view.findViewById(R.id.error_text_view);

        // Method that sets all of the OnFocusChanged listeners for the edit texts
        setTextEditFocusListeners();
        setTextChangeListeners();

        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    /**
     * Allows the Fragment and the main activity to communicate for any necessary methods
     *
     * @param createAccountFragmentListener The listener from the AuthenticationContainer for communication
     * */
    public void setCreateAccountFragmentListener(CreateAccountFragmentListener createAccountFragmentListener) {
        this.createAccountFragmentListener = createAccountFragmentListener;
    }

    /**
     * Create focus listener for edit text for username and password
     */
    private void setTextEditFocusListeners() {

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * check if text field for username is empty
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!usernameEditText.getText().toString().isEmpty()) {
                        usernameFilled = true;
                    }
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * check if password is weak or has no text in text field
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (passwordEditText.getText().toString().isEmpty()) {
                        weakPasswordTextView.setVisibility(View.INVISIBLE);
                        passwordFilled = false;
                    } else if (passwordGuidelineCheck(passwordEditText.getText().toString())) {
                        weakPasswordTextView.setVisibility(View.INVISIBLE);
                        passwordFilled = true;
                        isWeakPasswordEnable = false;
                    } else {
                        weakPasswordTextView.setVisibility(View.VISIBLE);
                        isWeakPasswordEnable = true;
                    }
                }
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * check if text fields for password confirmation and password are equal
             * @param v - view Object for CreateAccountFragment
             * @param hasFocus - verifies if cursor is focused on second text field
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
                        isPasswordMatch = true;
                        confirmedPasswordFilled = true;
                    } else {
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * Create text change listener for all of the edit texts in this Fragment
     */
    private void setTextChangeListeners() {
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * when user changes their input
             * @param s - user input sequence
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().toString().trim().isEmpty()) {
                    usernameFilled = true;
                } else {
                    usernameFilled = false;
                }
                enableCreateAccountButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isPasswordMatch = false;

                //Check if the password and confirm password match
                if (confirmedPasswordFilled) {
                    if (passwordEditText.getText().toString().equals(s.toString())) {
                        isPasswordMatch = true;
                    }
                }

                //Check if the user entered a password that follow the guide line
                if (passwordGuidelineCheck(s.toString())) {
                    passwordFilled = true;
                    isWeakPasswordEnable = false;
                } else {
                    passwordFilled = false;
                    isWeakPasswordEnable = true;
                }
                enableCreateAccountButton();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isPasswordMatch = false;

                //Check if password and confirm password match
                if (passwordEditText.getText().toString().equals(s.toString())) {
                    confirmedPasswordFilled = true;
                    isPasswordMatch = true;
                    confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);

                } else if (s.toString().trim().isEmpty()) {
                    confirmedPasswordFilled = false;
                }
                enableCreateAccountButton();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Checks the user's password to make sure it is strong enough to prevent dictionary attacks
     *
     * @param password The user's desired password
     * @return True if the password is strong enough and false otherwise
     */
    private boolean passwordGuidelineCheck(String password) {
        /**
         * Passwords must contain at least one a-z character, one A-Z character
         * one 0-9 character,and be 8 characters long minimum
         */
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }

    /**
     * Enable create account button to change button color to black
     */
    private void enableCreateAccountButton() {
        if (usernameFilled && passwordFilled && confirmedPasswordFilled && !isWeakPasswordEnable &&
                isPasswordMatch) {
            createAccountFragmentListener.onEnableCreateAccountButton(true);
        } else {
            createAccountFragmentListener.onEnableCreateAccountButton(false);
        }
    }
}
