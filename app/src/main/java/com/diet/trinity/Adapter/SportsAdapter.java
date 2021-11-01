package com.diet.trinity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.diet.trinity.R;

import java.util.ArrayList;

public class SportsAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<String> SportsNames;

    public SportsAdapter(Context context, ArrayList<String> SportsNames) {
        super();
        this.context = context;
        this.SportsNames = SportsNames;
    }

    public class SportHolder
    {
        TextView title;
    }

    @Override
    public int getCount() {
        return SportsNames.size();
    }

    @Override
    public Object getItem(int position) {
        return SportsNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        SportHolder holder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.sports_menu, parent, false);
            holder=new SportHolder();
            holder.title=(TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }
        else
        {
            holder=(SportHolder) convertView.getTag();
        }

        holder.title.setText(SportsNames.get(position));

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return convertView;
    }
}
