package edu.uga.cs.rommateshopping;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoommateViewActivity extends AppCompatActivity {

    private static final String TAG = "RoommateViewActivity";

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String userID;
    private ListView mListView;
    private TextView title;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * on create, sets the values for the different views and then
     * makes the addValueEventListener that will display the data in
     * our roommates list.
     *
     * @param savedInstanceState the bundled saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate_view);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        mListView = findViewById(R.id.listview);
        title = findViewById(R.id.roomNumTitle);


        myRef.addValueEventListener(new ValueEventListener() {

            /**
             * Shows the room and roommate data for the current user
             *
             * @param snapshot a snapshot of our current database
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                showData(snapshot);
            }

            /**
             * On a read failed or cancelled, this throws us an error
             *
             * @param error the error our database throws
             */
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    /**
     * Gets the current room number from database and sets it to display.
     * From that room number, also grabs the rest of the roommates in that
     * same room number and displays them to the user
     *
     * @param dataSnapshot a snapshot of our current database
     */
    private void showData(DataSnapshot dataSnapshot)
    {
        ArrayList<String> array = new ArrayList<>();
        DataSnapshot rooms = null;
        String realCurrentRoom = "0";

        //gets the current room number
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            User uInfo = new User();
            Log.d(TAG, "getRoommateData from database.");
            Log.d(TAG, "Current datasnap: " + ds);
            String currentRoom = ds.child(userID).child("room").getValue(String.class);
            if (currentRoom != null) {
                realCurrentRoom = currentRoom;
                Log.d(TAG, "currentRoom: " + realCurrentRoom);
                title.setText("Room Number: " + realCurrentRoom);

            } else
                rooms = ds;

        }

        //gets every name of every roommate in the current room number
        for(DataSnapshot ds : rooms.child(realCurrentRoom).child("roommates").getChildren()){
            array.add(ds.getKey());
        }
        try {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textview, array);
            mListView.setAdapter(adapter);
        }
        catch(NullPointerException e){
            array.add("There are no roommates.");
            ArrayAdapter adapt = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textview, array);
            mListView.setAdapter(adapt);
        }
    }
}
