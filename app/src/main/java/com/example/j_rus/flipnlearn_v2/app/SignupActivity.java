package com.example.j_rus.flipnlearn_v2.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.j_rus.fliplearn.util.Constants;
import com.example.j_rus.flipnlearn_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.j_rus.fliplearn.util.UserManager.isValidEmail;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private TextInputLayout inputLayoutErrorMsg, inputLayoutEmail, inputLayoutPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private String email;
    private String password;
    static final String logTag = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutErrorMsg = (TextInputLayout) findViewById(R.id.sign_up_error_msg);


        btnSignUp.setEnabled(false);
        btnSignUp.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnSignUp.setTextColor(getResources().getColor(R.color.input_login_hint));

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        //animation
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(logTag, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    btnSignUp.startAnimation(animShake);
                                    inputLayoutErrorMsg.setError(getString(R.string.sign_up_error_msg));
                                    Log.d(logTag,"Authentication failed." + task.getException());
                                } else {
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                    });
            }
        });

    }

    private boolean validateEmail() {
        email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.error_invalid_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
            requestFocus(inputEmail);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty() || !inputPassword.getText().toString().matches(Constants.REGEX_PW)) {
            inputLayoutPassword.setError(getString(R.string.minimum_password));
            requestFocus(inputPassword);

            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean activateSignUpButton(){
        String email = inputEmail.getText().toString().trim();
        if ((!inputPassword.getText().toString().trim().isEmpty() &&
                inputPassword.getText().toString().matches(Constants.REGEX_PW)) &&
                (!email.isEmpty() && isValidEmail(email))) {
            Log.d(logTag, "True");
            return true;
        }
        Log.d(logTag, "False");
        return false;
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(activateSignUpButton()){
                btnSignUp.setEnabled(true);
                btnSignUp.setBackgroundColor(getResources().getColor(R.color.btn_bg));
                btnSignUp.setTextColor(getResources().getColor(R.color.btn_text_color));
            }else{
                btnSignUp.setEnabled(false);
                btnSignUp.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                btnSignUp.setTextColor(getResources().getColor(R.color.input_login_hint));
            }

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
