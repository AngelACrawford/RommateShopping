package edu.uga.cs.rommateshopping;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PurchasedList extends Activity {
    public static final String DEBUG_TAG = "PurchasedList_DEBUG";

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String userID;
    private ListView listView;
    private ThreeColumn_Adapter adapter;

    private Button settleCostButton;
    private TextView totalText;

    private Double total = 0.0;
    private Long numRoommates;
    String realRoom = "0";
    private ArrayList<Item> itemsList;
    String name = "";

    /**
     * On create, gets current user's list of items they have decided to move into their
     * purchase list. If an item is un-purchased, it gets moved back to the group shopping list.
     * If we decide to settle the cost of our purchases, the payment information will be
     * sent to all of the other roommates in that room.
     *
     * @param savedInstanceState the bundled saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased);

        // Initialize all relevant variables
        itemsList = new ArrayList<Item>();
        listView = findViewById(R.id.list_view);
        adapter =  new ThreeColumn_Adapter(PurchasedList.this,R.layout.activity_three_column_adapter, itemsList);
        totalText = findViewById(R.id.totalText);
        settleCostButton = findViewById(R.id.settleCostButton);
        listView.setAdapter(adapter);

        // Settle Cost Click Listener
        settleCostButton.setOnClickListener(new SettleCostClickListener());

        myRef = FirebaseDatabase.getInstance().getReference();

        // Get user and User ID
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        // Get purchased items list from user
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            /**
             * Goes through the database snapshot and picks out all of the items
             * that this user has moved to their purchased items list to display.
             * If nothing is there for a user, it just will not display anything.
             *
             * @param snapshot a snapshot of our current database
             */
            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.

                DataSnapshot rooms = null;
                realRoom = "0";

                // Get room num
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String currentRoom = ds.child(userID).child("room").getValue(String.class);
                    if (currentRoom != null) {
                        realRoom = currentRoom;
                    } else
                        rooms = ds;
                }

                numRoommates = snapshot.child("rooms").child(realRoom).child("roommates").getChildrenCount();

                for(DataSnapshot ds : snapshot.child("users").child(userID).child("purchased").getChildren()){
                    Log.d(DEBUG_TAG, "DS Key: " + ds.getKey());

                    // Get item information
                    String name = ds.getValue(Item.class).getName();
                    Integer quantity = ds.getValue(Item.class).getQuantity();
                    Double price = ds.getValue(Item.class).getPrice();
                    Log.d(DEBUG_TAG, "Name: " + name + ", Quantity: " + quantity + ", Price: " + price);

                    // Add item to list
                    Item newItem = new Item(name, quantity, price);
                    itemsList.add(newItem);
                    Log.d(DEBUG_TAG, "Items List: " + itemsList);
                }
                try {
                    calculateTotal(itemsList);
                    adapter =  new ThreeColumn_Adapter(PurchasedList.this,R.layout.activity_three_column_adapter, itemsList);
                    listView.setAdapter(adapter);
                }
                catch(NullPointerException e){
                    ArrayList array = new ArrayList<String>();
                    array.add("There are no purchased Items");
                    adapter =  new ThreeColumn_Adapter(PurchasedList.this,R.layout.activity_three_column_adapter, array);
                    listView.setAdapter(adapter);
                }
            }

            /**
             * On a read failed or cancelled, this throws us an error
             *
             * @param databaseError the error our database throws
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        } );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(PurchasedList.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.purchased_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    /**
                     * if the un-purchase menu option is clicked, we need to remove that item
                     * in the purchased list back into the group shopping list
                     *
                     * @param item the item we have decided to take off the purchased list
                     * @return
                     */
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.itemShop:
                                // case for moving item to shopping list

                                // add item to shopping list
                                Log.d(DEBUG_TAG, "Room Number: " + realRoom);
                                Log.d(DEBUG_TAG, "Item Name: " + itemsList.get(position).getName());
                                Log.d(DEBUG_TAG, "Item: " + itemsList.get(position));
                                myRef.child("lists").child(realRoom).child(itemsList.get(position).getName()).setValue(itemsList.get(position));
                                // remove item from purchased list
                                myRef.child("users").child(userID).child("purchased").child(itemsList.get(position).getName()).removeValue();

                                itemsList.remove(position);
                                calculateTotal(itemsList);

                                adapter.notifyDataSetChanged();
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();

            }
        });


    }

    /**
     * Determines the total price of all items in our list
     *
     * @param itemsList our list of current items in our purchased list
     */
    private void calculateTotal(ArrayList<Item> itemsList) {
        for(Item item : itemsList) {
            total += item.getPrice();
        }
        totalText.setText("" + total);
    }

    /**
     * If settle cost is selected, settles the cost per roommate and sends it out to the
     * other users in the same room number.
     */
    private class SettleCostClickListener implements View.OnClickListener {
        /**
         * When clicked, settles the cost per roommate and sends the information
         * to every other roommate.
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {

            Log.d(DEBUG_TAG, "Num Roommates: " + numRoommates);
            //cost per roommate in the same room number as the current user
            Double individualCost = total / numRoommates;
            Log.d(DEBUG_TAG, "Indiv Cost: " + individualCost);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                /**
                 * Goes through and gets the name of the current user and then
                 * sends out to the rest of the roommates the person who they owe money to
                 * and the payment amount
                 *
                 * @param snapshot a snapshot of our current database
                 */
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> array = new ArrayList<>();

                    //gets the name of the current user from the users database
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.getKey().equals("users")) {
                            name = snapshot.child(userID).child("firstName").getValue(String.class);
                        }
                    }

                    //this one gets the roommate names that are in the current room
                    for(DataSnapshot ds : dataSnapshot.child("rooms")
                            .child(realRoom).child("roommates").getChildren()){
                        array.add(ds.getKey());
                    }

                    //this sets the payment owed for each roommate that's not the current user
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(DEBUG_TAG, "room: " + realRoom);
                        Log.d(DEBUG_TAG, "name: " + name);
                        if(snapshot.getKey().equals("rooms")) {
                            //goes through every roommate in the array of roommates we found above
                            for(int i = 0; i < array.size(); i++) {
                                //if the roommate is not the current user
                                if (!(snapshot.child(realRoom).child("roommates").child(array.get(i)).getKey().toString().equals(name))) {
                                    Log.d(DEBUG_TAG, "name in array: " + array.get(i));
                                    //record how much they owe the current user who purchased something
                                    snapshot.getRef().child(realRoom).child("roommates").child(array.get(i)).child("payments").child(name).child("moneyOwed").setValue(individualCost);
                                }
                            }
                        }
                    }

                    //clear the purchased list since it's settled now
                    //this will just delete everything from purchased list
                    //and reset the total at the top to 0
                    myRef.child("users").child(userID).child("purchased").removeValue();
                    adapter.clear();
                    totalText.setText("0.0");
                    adapter.notifyDataSetChanged();
                    //message for successful cost settled
                    Toast.makeText(getApplicationContext(), "Cost Settled: " + individualCost,
                            Toast.LENGTH_SHORT).show();

                }
                /**
                 * On a read failed or cancelled, this throws us an error
                 *
                 * @param databaseError the error our database throws
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
