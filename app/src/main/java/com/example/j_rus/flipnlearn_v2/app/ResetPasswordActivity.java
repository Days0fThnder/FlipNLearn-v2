package com.example.j_rus.flipnlearn_v2.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.j_rus.flipnlearn_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.j_rus.fliplearn.util.UserManager.isValidEmail;
import static com.example.j_rus.flipnlearn_v2.app.LoginActivity.logTag;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private TextInputLayout inputLayoutErrorMsg;
    private Button btnReset, btnBack;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        inputLayoutErrorMsg = (TextInputLayout) findViewById(R.id.sign_up_error_msg);

        inputEmail.addTextChangedListener(new ResetPasswordActivity.MyTextWatcher(inputEmail));

        btnReset.setEnabled(false);
        btnReset.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnReset.setTextColor(getResources().getColor(R.color.input_login_hint));

        //animation
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);

        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputLayoutErrorMsg.setError(getString(R.string.enter_registered_email));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions " +
                                            "to reset your password!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    inputLayoutErrorMsg.setError(getString(R.string.failed_send_email));
                                    btnReset.startAnimation(animShake);
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

    }

    private boolean activateResetButton(){
        String email = inputEmail.getText().toString().trim();
        if ((!email.isEmpty() && isValidEmail(email))) {
            Log.d(logTag, "True");
            return true;
        }
        Log.d(logTag, "False");
        return false;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(activateResetButton()){
                btnReset.setEnabled(true);
                btnReset.setBackgroundColor(getResources().getColor(R.color.btn_bg));
                btnReset.setTextColor(getResources().getColor(R.color.btn_text_color));
            }else{
                btnReset.setEnabled(false);
                btnReset.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                btnReset.setTextColor(getResources().getColor(R.color.input_login_hint));
            }

        }

        public void afterTextChanged(Editable editable) {

        }
    }
}
