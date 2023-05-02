package edu.uga.cs.rommateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList extends AppCompatActivity{

    public static final String DEBUG_TAG = "ShoppingList_DEBUG";

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String userID;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ThreeColumn_Adapter adapter;

    String realCurrentRoom;
    String roomNumber;

    private ArrayList<Item> itemsList;

    /**
     * on create, gets all the current information and grabs data from the
     * database to make sure that this room's shopping list information gets displayed
     * for each user.
     *
     * @param savedInstanceState the bundled saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        itemsList = new ArrayList<Item>();
        listView = findViewById(R.id.list_view);
        //arrayAdapter = new ArrayAdapter(ShoppingList.this, android.R.layout.simple_list_item_1, itemsList);
        adapter =  new ThreeColumn_Adapter(ShoppingList.this,R.layout.activity_three_column_adapter, itemsList);
        listView.setAdapter(adapter);

        // Get database reference
        myRef = FirebaseDatabase.getInstance().getReference();

        // Get user and User ID
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        // database stuff
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {

            /**
             *
             *
             * @param snapshot a snapshot of our current database
             */
            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                DataSnapshot lists = null;
                String realCurrentRoom = "0";

                for(DataSnapshot ds : snapshot.getChildren()) {
                    // Gets the room number value
                    String currentRoom = ds.child(userID).child("room").getValue(String.class);
                    roomNumber = ds.child(userID).child("room").getValue(String.class);
                    Log.d(DEBUG_TAG, "Room Num: " + roomNumber);

                    if (currentRoom != null) {
                        realCurrentRoom = currentRoom;
                    } else
                        lists = snapshot.child("lists");
                }

                // For each item in the Shopping List, get the information
                for(DataSnapshot ds : lists.child(realCurrentRoom).getChildren()){
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
                    //arrayAdapter = new ArrayAdapter(ShoppingList.this, android.R.layout.simple_list_item_1, itemsList);
                    adapter =  new ThreeColumn_Adapter(ShoppingList.this,R.layout.activity_three_column_adapter, itemsList);
                    listView.setAdapter(adapter);
                }
                catch(NullPointerException e){
                    ArrayList array = new ArrayList<String>();
                    array.add("There are no roommates.");
                    //arrayAdapter = new ArrayAdapter(ShoppingList.this, android.R.layout.simple_list_item_1, array);
                    adapter =  new ThreeColumn_Adapter(ShoppingList.this,R.layout.activity_three_column_adapter, array);
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
                PopupMenu popupMenu = new PopupMenu(ShoppingList.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.itemUpdate:
                                // case for update
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);
                                View v = LayoutInflater.from(ShoppingList.this).inflate(R.layout.item_dialog, null, false);
                                builder.setTitle("Update Item");

                                EditText editName = v.findViewById(R.id.itemText);
                                EditText editQuantity = v.findViewById(R.id.quantityText);
                                EditText editPrice = v.findViewById(R.id.priceText);
                                editName.setText(itemsList.get(position).getName());
                                editQuantity.setText(itemsList.get(position).getQuantity().toString());
                                editPrice.setText(itemsList.get(position).getPrice().toString());

                                builder.setView(v);

                                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!editName.getText().toString().isEmpty()) {
                                            myRef.child("lists").child(roomNumber).child(itemsList.get(position).getName()).removeValue();
                                            itemsList.get(position).setName(editName.getText().toString().trim());
                                        }
                                        else {
                                            editName.setError("Update the Item!");
                                        }
                                        if (!editQuantity.getText().toString().isEmpty()){
                                            itemsList.get(position).setQuantity(Integer.parseInt(editQuantity.getText().toString().trim()));
                                        }
                                        else {
                                            editQuantity.setError("Update the Item!");
                                        }
                                        if (!editPrice.getText().toString().isEmpty()){
                                            itemsList.get(position).setPrice(Double.parseDouble(editPrice.getText().toString().trim()));
                                        }
                                        else {
                                            editPrice.setError("Update the Item!");
                                        }

                                        // update database
                                        myRef.child("lists").child( roomNumber ).child( itemsList.get(position).getName()).setValue( itemsList.get(position));
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(ShoppingList.this, "Item Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();

                                break;

                            case R.id.itemDelete:
                                // case for delete
                                Toast.makeText(ShoppingList.this, "Item Deleted", Toast.LENGTH_SHORT).show();

                                // delete from database
                                Log.d(DEBUG_TAG, "Room Number: " + roomNumber);
                                Log.d(DEBUG_TAG, "Item: " + itemsList.get(position).getName());
                                myRef.child("lists").child(roomNumber).child(itemsList.get(position).getName()).removeValue();
                                adapter.notifyDataSetChanged();

                                // update local list
                                itemsList.remove(position);
                                break;

                            case R.id.itemPurchased:
                                // case for purchased item

                                // add to user's purchased list
                                myRef.child("users").child(userID).child("purchased").child(itemsList.get(position).getName()).setValue(itemsList.get(position));
                                // remove from group shopping list
                                myRef.child("lists").child(roomNumber).child(itemsList.get(position).getName()).removeValue();
                                // update local list
                                itemsList.remove(position);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                //function to add
                addItem();
                break;

        }
        return true;
    }

    private void addItem() {
        // Create an "Add item" pop-up alert
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);
        builder.setTitle("Add New Item");

        View v = LayoutInflater.from(ShoppingList.this).inflate(R.layout.item_dialog, null, false);
        builder.setView(v);

        Item item = new Item();
        EditText etItem = v.findViewById(R.id.itemText);
        EditText etQuantity = v.findViewById(R.id.quantityText);
        EditText etPrice = v.findViewById(R.id.priceText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(etItem.getText().toString().isEmpty() || etQuantity.getText().toString().isEmpty()) {
                    etItem.setError("Fill in available fields");
                }
                else{
                    //create item to be added
                    item.setName(etItem.getText().toString().trim());
                    item.setQuantity(Integer.parseInt(etQuantity.getText().toString()));
                    item.setPrice(Double.parseDouble(etPrice.getText().toString()));
                    Log.d(DEBUG_TAG, "Item name: " + item.getName());
                    Log.d(DEBUG_TAG, "Item quantity: " + item.getQuantity());
                    Log.d(DEBUG_TAG, "Item price: " + item.getPrice());

                    // add item to database here
                    Log.d(DEBUG_TAG, "Room: " + realCurrentRoom);
                    Log.d(DEBUG_TAG, "Name: " + item.getName());
                    myRef.child("lists").child( roomNumber ).child( item.getName() ).setValue( item );

                    // add item to local list
                    itemsList.add(item);

                    // update the adapter
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
