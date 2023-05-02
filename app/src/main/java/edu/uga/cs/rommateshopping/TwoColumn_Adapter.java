package edu.uga.cs.rommateshopping;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TwoColumn_Adapter extends ArrayAdapter<Payment> {
    private static final String DEBUG_TAG = "TwoColumn_DEBUG";
    private LayoutInflater mInflater;
    private ArrayList<Payment> payments;
    private int mViewResourceId;

    public TwoColumn_Adapter(Context context, int textViewResourceId, ArrayList<Payment> payments) {
        super(context, textViewResourceId, payments);
        this.payments = payments;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        Payment person = payments.get(position);

        if (person != null) {
            TextView name = convertView.findViewById(R.id.textName);
            TextView payment = convertView.findViewById(R.id.textPayment);
            if (name != null) {
                name.setText(payments.get(position).getPerson());
            }
            if (payment != null) {
                Log.d(DEBUG_TAG, "Payment: " + payments.get(position).getPayment());
                payment.setText("" + payments.get(position).getPayment());
            }
        }

        return convertView;
    }

}
