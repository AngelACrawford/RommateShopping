package edu.uga.cs.rommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{

    public static final String TAG = "Sign in Activity";

    private FirebaseAuth mAuth;
    private Button login;

    private EditText emailText;
    private EditText passwordText;

    /**
     * Creates the basic information for the login activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.loginButton);
        emailText = findViewById(R.id.emailText2);
        passwordText = findViewById(R.id.passwordText2);

        //listener for the login button
        login.setOnClickListener(new LoginActivity.ButtonClickListener());
    }

    /**
     * On start, gets the current user
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /**
     * Listener for the login button.
     */
    private class ButtonClickListener implements View.OnClickListener {

        /**
         * on click, gets the email and password from the
         * edit views and calls the signInUser method
         */
        @Override
        public void onClick(View v) {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            signInUser(email, password);
        }
    }

    /**
     * Attempts to sign in a user based on the information
     * they have provided to the app.
     *
     * @param email the email that was retrieved from the user input
     * @param password the password retrieved from the user's input
     */
    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser:" + email);

        //attempts a sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /**
                     * Attempts a sign in. If successful sign in, display successful message
                     * and then calls updateUI method. If unsuccessful, does not allow
                     * the user to go on to the next screen and displays an error.
                     *
                     * @param task the sign-in attempt
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    /**
     * This is called on a successful sign in. When someone signs in,
     * this method will update the UI to now go to the MainNavigationActivity and display a
     * successful sign in message.
     *
     * @param account the current FirebaseUser's information
     */
    public void updateUI(FirebaseUser account){

        if(account != null){
            Toast.makeText(this,"Signed in successfully.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainNavigationActivity.class));

        }else {
            Toast.makeText(this,"Error signing in: Could not find this user.",Toast.LENGTH_LONG).show();
        }

    }


}
