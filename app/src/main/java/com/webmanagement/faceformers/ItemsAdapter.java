package com.webmanagement.faceformers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Banpot.S on 8/20/14 AD.
 */
public class ItemsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<JSONObject> itemArrayList;
    private LayoutInflater mInflater;

    private AQuery aq;

    public ItemsAdapter(Context context, ArrayList<JSONObject> itemArrayList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.itemArrayList = itemArrayList;
        aq = new AQuery(context);
    }

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView;
        JSONObject jsonItem = itemArrayList.get(position);

        if(convertView==null){
            convertView = mInflater.inflate(R.layout.choose_gridview,null);
            itemView = new ItemView();
            itemView.imageView =  (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(itemView);
        }else{
            itemView = (ItemView) convertView.getTag();
        }
        try {

            aq.id(itemView.imageView)
                    .image(jsonItem.getString("cover"),true,true,0,0,null,AQuery.FADE_IN_NETWORK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //nearByItemView.titleTextView.setText(shop.getTitle());
        //nearByItemView.addressTextView.setText(shop.getCity());

        return convertView;
    }

    class ItemView{
        ImageView imageView;
    }
}
