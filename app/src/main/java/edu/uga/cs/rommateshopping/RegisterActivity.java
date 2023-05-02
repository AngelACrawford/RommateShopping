package edu.uga.cs.rommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{

    public static final String TAG = "Sign in Activity";

    private FirebaseAuth mAuth;
    private Button register;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText roomNumber;
    private FirebaseDatabase database;

    /**
     * Sets buttons and views to appropriate values. Sets onClickListener for register button
     *
     * @param savedInstanceState the bundled saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.registerButton);
        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        roomNumber = findViewById(R.id.roomNumberText);

        register.setOnClickListener(new ButtonClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Registers a user using the information they gave us in the app
     */
    private class ButtonClickListener implements View.OnClickListener {

        /**
         * When clicked, registers a user
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            //gets values from information entered
            String firstName = firstNameText.getText().toString();
            String lastName = lastNameText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            //if is roomNumber is empty, assign random room num 1000 -> 9999
            String roomNum = roomNumber.getText().toString();
            if(TextUtils.isEmpty(roomNum)) {
                Integer randomNumber =  (int)((Math.random() * (9999 - 1001)) + 1000);
                roomNum = "" + randomNumber;
            }

            // Create new user object to be pushed into db
            final User curUser = new User(firstName, lastName, email, roomNum);
            createAccount(email, password, curUser);
        }
    }

    /**
     *
     *
     * @param email the email address the user has provided for this registration
     * @param password the password the user has provided for this registration
     * @param curUser the current user's FirebaseUser information
     */
    private void createAccount(String email, String password, User curUser) {
        Log.d(TAG, "createAccount: " + email);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userID = user.getUid();

                            // Get database instance
                            database = FirebaseDatabase.getInstance();

                            // Add user
                            DatabaseReference myRef = database.getReference("users");
                            myRef.child( userID ).setValue( curUser )
                                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Show a quick confirmation
                                            Toast.makeText(getApplicationContext(), "User Created :  " + curUser.firstName,
                                                    Toast.LENGTH_SHORT).show();

                                            // Clear the EditTexts for next use.
                                            firstNameText.setText("");
                                            lastNameText.setText("");
                                            emailText.setText("");
                                            passwordText.setText("");
                                            roomNumber.setText("");
                                        }
                                    })
                                    .addOnFailureListener( new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText( getApplicationContext(), "Failed to create a User for " + curUser.firstName,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Get room if it exists, add new roommate to database
                            myRef = database.getReference("rooms");
                            myRef.child( curUser.room ).child( "roommates" ).child( curUser.firstName ).setValue( curUser )
                                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Show a quick confirmation
                                            Toast.makeText(getApplicationContext(), "User Created :  " + curUser.firstName,
                                                    Toast.LENGTH_SHORT).show();

                                            // Clear the EditTexts for next use.
                                            firstNameText.setText("");
                                            lastNameText.setText("");
                                            emailText.setText("");
                                            passwordText.setText("");
                                            roomNumber.setText("");
                                        }
                                    })
                                    .addOnFailureListener( new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText( getApplicationContext(), "Failed to create a User for " + curUser.firstName,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }

    /**
     * This is called on a successful registration. When someone registers,
     * this method will update the UI to now go to the MainNavigationActivity and display a
     * successful registration message.
     *
     * @param account the current FirebaseUser's information
     */
    public void updateUI(FirebaseUser account){

        if(account != null){
            Toast.makeText(this,"Registered successfully.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainNavigationActivity.class));

        }else {
            Toast.makeText(this,"Error signing in: Could not find this user.",Toast.LENGTH_LONG).show();
        }

    }
}
