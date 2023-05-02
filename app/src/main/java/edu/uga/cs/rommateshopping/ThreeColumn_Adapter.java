package edu.uga.cs.rommateshopping;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ThreeColumn_Adapter extends ArrayAdapter<Item> {
    private LayoutInflater mInflater;
    private ArrayList<Item> items;
    private int mViewResourceId;

    public ThreeColumn_Adapter(Context context, int textViewResourceId, ArrayList<Item> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        Item item = items.get(position);

        if (item != null) {
            TextView name = convertView.findViewById(R.id.textName);
            TextView quantity = convertView.findViewById(R.id.textQuantity);
            TextView price = convertView.findViewById(R.id.textPrice);
            if (name != null) {
                name.setText(item.getName());
            }
            if (quantity != null) {
                quantity.setText("" + item.getQuantity());
            }
            if (price != null) {
                price.setText("" + item.getPrice());
            }
        }

        return convertView;
    }
}
