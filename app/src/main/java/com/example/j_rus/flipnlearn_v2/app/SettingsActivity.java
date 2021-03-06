package com.example.j_rus.flipnlearn_v2.app;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j_rus.fliplearn.util.UserManager;
import com.example.j_rus.flipnlearn_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Header hd;

    private static Activity mActivity;
    private static Context mContext;
    static final String logTag = "SettingActivity";

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            final String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        setupActionBar();


    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AccountPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {
        private Button btnChangeName, btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
                changeName, changeEmail, changePassword, sendEmail, btnRemove, btnLogOut, btnCancel;

        private TextView oldEmailTextView, oldNameTextView;
        private EditText newNameEditText, newEmailEditText, passNameChangeEditText, oldPasswordEditText, newPasswordEditText, confNewPasswordEditText;
        private TextInputLayout inputLayoutNewName, inputLayoutNewEmail, inputLayoutPasswordForName,
                inputLayoutOldPassword, inputLayoutNewPassword, inputLayoutConfNewPassword, inputLayoutNameErrorMsg, inputLayoutErrorMsg;
        private ProgressBar progressBar;
        private FirebaseAuth auth;
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final UserManager userManager = new UserManager();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_account);
            //get firebase auth instance
            auth = FirebaseAuth.getInstance();

            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        // launch login activity
                        mActivity.finish();
                        startActivity(new Intent(mActivity, LoginActivity.class));


                    }
                }
            };
            authListener.onAuthStateChanged(auth);
            setHasOptionsMenu(true);

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View accountView = inflater.inflate(R.layout.fragment_account_settings, container, false);
            IntializeFields(accountView);
            //Set visible field's values
            oldNameTextView.setText(user.getDisplayName().toString());
            oldEmailTextView.setText(user.getEmail());
            //animation
            final Animation animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);

            setIntialView();
            btnChangeName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    oldNameTextView.setVisibility(View.VISIBLE);
                    newNameEditText.setVisibility(View.VISIBLE);
                    passNameChangeEditText.setVisibility(View.VISIBLE);
                    inputLayoutNewName.setVisibility(View.VISIBLE);
                    inputLayoutPasswordForName.setVisibility(View.VISIBLE);
                    oldPasswordEditText.setVisibility(View.GONE);
                    newPasswordEditText.setVisibility(View.GONE);
                    confNewPasswordEditText.setVisibility(View.GONE);
                    changeName.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.GONE);
                    sendEmail.setVisibility(View.GONE);
                    btnRemove.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    toogleChangeButtons();
                }
            });
            changeName.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(!userManager.isNameEmptyOrNotChanged(user, newNameEditText, inputLayoutNewName, mContext, mActivity)){
                        changeName.startAnimation(animShake);
                        return;
                    }
                    if (!userManager.validatePassword(passNameChangeEditText, inputLayoutPasswordForName, mContext, mActivity)) {
                        changeName.startAnimation(animShake);
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    final String name = newNameEditText.getText().toString().trim();
                    if(user != null && !name.equals("")){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    // the only way to show the name change is to re-authenticate user
                                    AuthCredential credential = EmailAuthProvider
                                            .getCredential(user.getEmail(), passNameChangeEditText.getText().toString().trim());
                                    user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Log.d(logTag, "User re-authenticated.");
                                                        oldNameTextView.setText(user.getDisplayName());
                                                        newNameEditText.setText("");
                                                        passNameChangeEditText.setText("");
                                                        Toast.makeText(mActivity, "Your user name has " +
                                                                "been updated", Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        inputLayoutNameErrorMsg.setVisibility(View.GONE);
                                                    }else{
                                                        Log.d(logTag, "User could not be re-authenticated.");
                                                        Toast.makeText(mActivity, "Log back in to see " +
                                                                "your new name " , Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                    }
                }
            });

            btnChangeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldEmailTextView.setVisibility(View.VISIBLE);
                    newEmailEditText.setVisibility(View.VISIBLE);
                    inputLayoutNewEmail.setVisibility(View.VISIBLE);
                    oldPasswordEditText.setVisibility(View.GONE);
                    newPasswordEditText.setVisibility(View.GONE);
                    confNewPasswordEditText.setVisibility(View.GONE);
                    changeEmail.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.GONE);
                    sendEmail.setVisibility(View.GONE);
                    btnRemove.setVisibility(View.GONE);
                    btnChangeEmail.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    toogleChangeButtons();
                }
            });

            btnChangePassword.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    inputLayoutOldPassword.setVisibility(View.VISIBLE);
                    inputLayoutNewPassword.setVisibility(View.VISIBLE);
                    inputLayoutConfNewPassword.setVisibility(View.VISIBLE);
                    oldEmailTextView.setVisibility(View.GONE);
                    newEmailEditText.setVisibility(View.GONE);
                    oldPasswordEditText.setVisibility(View.VISIBLE);
                    newPasswordEditText.setVisibility(View.VISIBLE);
                    confNewPasswordEditText.setVisibility(View.VISIBLE);
                    changeEmail.setVisibility(View.GONE);
                    changePassword.setVisibility(View.VISIBLE);
                    sendEmail.setVisibility(View.GONE);
                    btnRemove.setVisibility(View.GONE);
                    btnChangeEmail.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    toogleChangeButtons();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener(){
                 @Override
                 public void onClick(View v) {
                     toogleChangeButtons();
                 }
            } );

            changeEmail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   if( !userManager.validateEmail(newEmailEditText, inputLayoutNewEmail, mContext, mActivity)){
                       changeEmail.startAnimation(animShake);
                       return;
                   }
                    progressBar.setVisibility(View.VISIBLE);
                    if (user != null && !newEmailEditText.getText().toString().trim().equals("")) {
                        user.updateEmail(newEmailEditText.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            oldEmailTextView.setText(user.getEmail());
                                            newEmailEditText.setText("");
                                            Toast.makeText(mActivity, "Email address is updated. An email was " +
                                                    "sent to your new address", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(mActivity, "Failed to update email!", Toast.LENGTH_LONG).show();
                                            Log.d(logTag, task.getException().getMessage());
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else if (newEmailEditText.getText().toString().trim().equals("")) {
                        newEmailEditText.setError("Enter email");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!userManager.validatePasswordFields(oldPasswordEditText, newPasswordEditText,
                            confNewPasswordEditText, inputLayoutOldPassword, inputLayoutNewPassword,
                            inputLayoutConfNewPassword, mContext, mActivity)){
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    if(userManager.validatePassword(oldPasswordEditText, inputLayoutOldPassword, mContext, mActivity)){
                        if(user != null && user.getEmail() != null){
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPasswordEditText.getText().toString().trim());
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                if (!userManager.validatePassword(newPasswordEditText, inputLayoutNewPassword, mContext, mActivity)) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return;
                                                } else {
                                                    user.updatePassword(newPasswordEditText.getText().toString().trim())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(mActivity, "Password is updated, sign in with new old Password!", Toast.LENGTH_LONG).show();
                                                                        signOut();
                                                                        progressBar.setVisibility(View.GONE);
                                                                    } else {
                                                                        Toast.makeText(mActivity, "Failed to update old Password!", Toast.LENGTH_LONG).show();
                                                                        progressBar.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }else{
                                                Toast.makeText(mActivity, "Failed to update password!", Toast.LENGTH_LONG).show();
                                                Log.d(logTag, task.getException().getMessage());
                                                progressBar.setVisibility(View.GONE);
                                                inputLayoutOldPassword.setError("Your password is not valid, enter your current password");

                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(mActivity, "Your email address was not found: Contact your Admin", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }else{
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
            btnSendResetEmail.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mActivity, ResetPasswordActivity.class));
                }
            });

            btnRemoveUser.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.delete_account_title)
                            .setMessage(R.string.delete_acoount_msg)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (user != null) {
                                        user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(mActivity, "Your profile is deleted:( Create a account now!", Toast.LENGTH_LONG).show();
                                                        mActivity.finish();
                                                        startActivity(new Intent(mActivity, MainActivity.class));
                                                    } else {
                                                        Toast.makeText(mActivity, "Failed to delete your account!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                    }
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
            return accountView;
        }

        //btnCancel.

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        //sign out method
        public void signOut() {
            auth.signOut();
            // this listener will be called when there is change in firebase user session
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        // launch login activity
                        mActivity.finish();
                        startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                }
            };
            authListener.onAuthStateChanged(auth);
        }

        private void toogleChangeButtons(){
            if(btnCancel.isPressed()){
                oldNameTextView.setVisibility(View.GONE);
                newNameEditText.setVisibility(View.GONE);
                passNameChangeEditText.setVisibility(View.GONE);
                oldEmailTextView.setVisibility(View.GONE);
                newEmailEditText.setVisibility(View.GONE);
                oldPasswordEditText.setVisibility(View.GONE);
                newPasswordEditText.setVisibility(View.GONE);
                confNewPasswordEditText.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changeName.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                btnRemove.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

                inputLayoutNewName.setVisibility(View.GONE);
                inputLayoutNewEmail.setVisibility(View.GONE);
                inputLayoutErrorMsg.setVisibility(View.GONE);
                inputLayoutPasswordForName.setVisibility(View.GONE);
                inputLayoutNameErrorMsg.setVisibility(View.GONE);
                inputLayoutOldPassword.setVisibility(View.GONE);
                inputLayoutNewPassword.setVisibility(View.GONE);
                inputLayoutConfNewPassword.setVisibility(View.GONE);

                btnChangeName.setVisibility(View.VISIBLE);
                btnChangeEmail.setVisibility(View.VISIBLE);
                btnChangePassword.setVisibility(View.VISIBLE);
                btnSendResetEmail.setVisibility(View.VISIBLE);
                btnRemoveUser.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);

                if(newNameEditText.getText() != null)
                    newNameEditText.setText("");
                if(passNameChangeEditText.getText() != null)
                    passNameChangeEditText.setText("");
                if(oldEmailTextView.getText() != null)
                    oldEmailTextView.setText("");
                if(newEmailEditText.getText() != null)
                    newEmailEditText.setText("");
                if(oldPasswordEditText.getText() != null)
                    oldPasswordEditText.setText("");
                if(newPasswordEditText.getText() != null)
                    newPasswordEditText.setText("");
                if(confNewPasswordEditText.getText() != null)
                    confNewPasswordEditText.setText("");
                if(inputLayoutOldPassword.getError() != null)
                    inputLayoutOldPassword.setError("");
                if(inputLayoutNewPassword.getError() != null)
                    inputLayoutNewPassword.setError("");
                if(inputLayoutConfNewPassword.getError() != null)
                    inputLayoutConfNewPassword.setError("");
            }else {
                btnChangeName.setVisibility(View.GONE);
                btnChangeEmail.setVisibility(View.GONE);
                btnChangePassword.setVisibility(View.GONE);
                btnSendResetEmail.setVisibility(View.GONE);
                btnRemoveUser.setVisibility(View.GONE);
            }
        }

        private void IntializeFields(View accountView){
            btnChangeName = (Button) accountView.findViewById(R.id.change_name_button);
            btnChangeEmail = (Button) accountView.findViewById(R.id.change_email_button);
            btnChangePassword = (Button) accountView.findViewById(R.id.change_password_button);
            btnSendResetEmail = (Button) accountView.findViewById(R.id.sending_pass_reset_button);
            btnRemoveUser = (Button) accountView.findViewById(R.id.remove_user_button);
            btnCancel = (Button) accountView.findViewById(R.id.btn_cancel);
            changeEmail = (Button) accountView.findViewById(R.id.changeEmail);
            changeName = (Button) accountView.findViewById(R.id.changeName);
            changePassword = (Button) accountView.findViewById(R.id.changePass);
            sendEmail = (Button) accountView.findViewById(R.id.send);
            btnRemove = (Button) accountView.findViewById(R.id.remove);
            btnLogOut = (Button) accountView.findViewById(R.id.sign_out);

            inputLayoutNewName = (TextInputLayout)accountView.findViewById(R.id.input_layout_new_name);
            inputLayoutNewEmail = (TextInputLayout)accountView.findViewById(R.id.input_layout_new_email);
            inputLayoutPasswordForName = (TextInputLayout) accountView.findViewById(R.id.input_layout_password_new_name);

            inputLayoutOldPassword = (TextInputLayout)accountView.findViewById(R.id.input_layout_old_password);
            inputLayoutNewPassword = (TextInputLayout)accountView.findViewById(R.id.input_layout_new_password);
            inputLayoutConfNewPassword = (TextInputLayout)accountView.findViewById(R.id.input_layout_conf_new_password);

            inputLayoutNameErrorMsg = (TextInputLayout)accountView.findViewById(R.id.auth_name_error_msg);
            inputLayoutErrorMsg = (TextInputLayout)accountView.findViewById(R.id.auth_error_msg);


            oldNameTextView = (TextView) accountView.findViewById(R.id.old_name);
            oldEmailTextView = (TextView) accountView.findViewById(R.id.old_email);

            newNameEditText =  (EditText) accountView.findViewById(R.id.new_name);
            newEmailEditText = (EditText) accountView.findViewById(R.id.new_email);
            passNameChangeEditText = (EditText) accountView.findViewById(R.id.password_for_new_name);
            newEmailEditText = (EditText) accountView.findViewById(R.id.new_email);
            oldPasswordEditText = (EditText) accountView.findViewById(R.id.old_password);
            newPasswordEditText = (EditText) accountView.findViewById(R.id.new_Password);
            confNewPasswordEditText = (EditText) accountView.findViewById(R.id.conf_new_Password);

            progressBar = (ProgressBar) accountView.findViewById(R.id.progressBar);
        }

        private void setIntialView(){
            if(UserManager.isGoogleAccount || UserManager.isFacebookAccount) {
                btnChangeName.setVisibility(View.GONE);
                btnChangeEmail.setVisibility(View.GONE);
                btnChangePassword.setVisibility(View.GONE);
                btnSendResetEmail.setVisibility(View.GONE);
            }
            inputLayoutNewName.setVisibility(View.GONE);
            inputLayoutNewEmail.setVisibility(View.GONE);
            inputLayoutErrorMsg.setVisibility(View.GONE);
            inputLayoutPasswordForName.setVisibility(View.GONE);
            inputLayoutNameErrorMsg.setVisibility(View.GONE);
            inputLayoutOldPassword.setVisibility(View.GONE);
            inputLayoutNewPassword.setVisibility(View.GONE);
            inputLayoutConfNewPassword.setVisibility(View.GONE);

            oldNameTextView.setVisibility(View.GONE);
            newNameEditText.setVisibility(View.GONE);
            passNameChangeEditText.setVisibility(View.GONE);
            oldEmailTextView.setVisibility(View.GONE);
            newEmailEditText.setVisibility(View.GONE);
            oldPasswordEditText.setVisibility(View.GONE);
            newPasswordEditText.setVisibility(View.GONE);
            confNewPasswordEditText.setVisibility(View.GONE);
            changeEmail.setVisibility(View.GONE);
            changeName.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
            sendEmail.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        /*@Override
        public void onResume() {
            super.onResume();
            auth.addAuthStateListener(authListener);
        }

        @Override
        public void onStart() {
            super.onStart();
            auth.addAuthStateListener(authListener);
        }

        @Override
        public void onStop(){
            super.onStop();
            if(authListener != null){
                auth.removeAuthStateListener(authListener);
            }
        }*/
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"), "placeHolder", );
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"), "placeHolder", );
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


}
