package com.example.j_rus.flipnlearn_v2.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.j_rus.flipnlearn_v2.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button createNewCardDeck;
    private Button existingDeck;
    private Button userDeck;
    private GoogleApiClient mGoogleApiClient;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auth
        auth = FirebaseAuth.getInstance();
        createNewCardDeck = (Button) findViewById(R.id.create_new_deck_btn);
        existingDeck = (Button) findViewById(R.id.existing_deck_btn);
        userDeck = (Button) findViewById(R.id.btn_users_deck);
        userName = (TextView) findViewById(R.id.welcome_user_msg);

        FirebaseUser user = auth.getInstance().getCurrentUser();

        if(user != null){
            String name = user.getDisplayName();
            userName.setText(name);
            userDeck.setVisibility(View.VISIBLE);
        }

        createNewCardDeck.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View V) {
                     if(auth.getCurrentUser() != null) {
                         startActivity(new Intent("com.rusangiza.jean_leman.flipnlearn.CreateDeck"));
                     }else{
                         startActivity(new Intent(MainActivity.this, LoginActivity.class));
                     }
                 }
             }
        );

        userDeck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //Get Firebase auth instance
        //auth = FirebaseAuth.getInstance();
        //Signed in google user's info

        MenuItem signOut = menu.findItem(R.id.action_sign_out);
        MenuItem signIn = menu.findItem(R.id.action_sign_in);
        if(auth.getCurrentUser() != null){
            signOut.setVisible(true);
            signIn.setVisible(false);
        }else{
            signIn.setVisible(true);
            signOut.setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_sign_out) {
            auth.signOut();
            LoginManager.getInstance().logOut();
            // this listener will be called when there is change in firebase user session
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        finish();
                        startActivity(getIntent());
                    }
                }
            };

            authListener.onAuthStateChanged(auth);
            return true;
        }

        if (id == R.id.action_sign_in) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
