package edu.uga.cs.rommateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "SuperApp";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById( R.id.textView15 );
        mAuth = FirebaseAuth.getInstance();
        String email = "angel.crawford@uga.edu";
        String password = "password";

        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success
                            Log.d( TAG, "signInWithEmail:success" );
                            FirebaseUser user = mAuth.getCurrentUser();
                        }
                        else {
// If sign in fails
                            Log.d( TAG, "signInWithEmail:failure", task.getException() );
                        }
                    }
                });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference( "message" );
// Read from the database value for ”message”
        myRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
// This method is called once, initially, and when data is updated
                String message = dataSnapshot.getValue(String.class);
                textView.setText( message );
            }
            @Override
            public void onCancelled( DatabaseError error ) {
// Failed to read value
                Log.d( TAG, "Failed to read value.", error.toException() );
            }
        });
    }

}