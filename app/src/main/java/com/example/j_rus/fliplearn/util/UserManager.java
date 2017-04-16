package com.example.j_rus.fliplearn.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.j_rus.flipnlearn_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by j_rus on 2/11/2017.
 */

public class UserManager {

    private FirebaseAuth auth;
    private boolean isUpdated = false;
    public static boolean isGoogleAccount = false;
    public static boolean isFacebookAccount = false;

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean updateUserName(FirebaseUser user,  String name) {

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        isUpdated = true;
                    }
                }
            });
        }
        return isUpdated;
    }

    public boolean validateEmail(EditText inputEmail, TextInputLayout inputLayoutEmail,
                                  Context context, Activity activity) {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(context.getString(R.string.error_invalid_email));
            requestFocus(inputEmail, activity);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
            requestFocus(inputEmail, activity);
        }

        return true;
    }

    public boolean validatePassword(EditText inputPassword, TextInputLayout inputLayoutPassword,
                                     Context context, Activity activity) {
        if (inputPassword.getText().toString().trim().isEmpty() || !inputPassword.getText().toString()
                .matches(Constants.REGEX_PW)) {
            inputLayoutPassword.setError(context.getString(R.string.minimum_password));
            requestFocus(inputPassword, activity);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isNameEmptyOrNotChanged(FirebaseUser user, EditText inputName, TextInputLayout inputLayoutName,
                                           Context context, Activity activity) {
        if (inputName.getText().toString().trim().isEmpty() || inputName.getText().toString().trim().equals(user.getDisplayName())) {
            inputLayoutName.setError(context.getString(R.string.hint_change_name));
            requestFocus(inputName, activity);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view, Activity activity) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
