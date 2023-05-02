package edu.uga.cs.rommateshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainNavigationActivity extends AppCompatActivity {

    private static final String TAG = "Main Nav Activity";
    private Button roommates;
    private Button groupShop;
    private Button purchased;
    private Button cashOut;
    private Button logout;

    /**
     * Creates the navigation page and sets every button and assigns listeners.
     *
     * @param savedInstanceState the bundled saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        //set buttons
        roommates = findViewById(R.id.roommatesListButton);
        groupShop = findViewById(R.id.groupShoppingButton);
        purchased = findViewById(R.id.purchasesButton);
        cashOut = findViewById(R.id.paybackButton);
        logout = findViewById(R.id.logoutButton);

        //make listeners
        roommates.setOnClickListener(new RoommatesClickListener());
        groupShop.setOnClickListener(new GroupListClickListener());
        purchased.setOnClickListener(new PurchasesClickListener());
        cashOut.setOnClickListener(new CashOutClickListener());
        logout.setOnClickListener(new LogOutClickListener());
    }

    /**
     * This class defines the onClickListener class for the roommate list button.
     * Opens RoommateViewActivity.
     */
    private class RoommatesClickListener implements View.OnClickListener {

        /**
         * Display Roommates List
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), RoommateViewActivity.class);
            v.getContext().startActivity(intent);

        }
    }

    /**
     * This class defines the onClickListener class for the group shopping list button.
     * Opens ShoppingList.
     */
    private class GroupListClickListener implements View.OnClickListener {

        /**
         * Display Group Shopping List
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ShoppingList.class);
            v.getContext().startActivity(intent);
        }
    }

    /**
     * This class defines the onClickListener class for the purchases button.
     * Opens PurchasedList.
     */
    private class PurchasesClickListener implements View.OnClickListener {

        /**
         * Display Purchased List
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PurchasedList.class);
            v.getContext().startActivity(intent);
        }
    }

    /**
     * This class defines the onClickListener class for the payments button.
     * Opens PaymentsListActivity.
     */
    private class CashOutClickListener implements View.OnClickListener {

        /**
         * Display Payments screen
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PaymentsListActivity.class);
            v.getContext().startActivity(intent);
        }
    }

    /**
     * This class defines the onClickListener class for the logout button.
     * Signs the current user out and returns to MainActivity.
     */
    private class LogOutClickListener implements View.OnClickListener {

        /**
         * Signs out the current user and returns to the MainActivity login/register screen.
         *
         * @param v the button view that has called the method
         */
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Logged out");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            v.getContext().startActivity(intent);
        }
    }
}
