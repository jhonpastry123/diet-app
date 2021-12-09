package com.diet.trinity.Adapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.diet.trinity.R;
import com.diet.trinity.activity.RecipeSingleActivity;
import com.diet.trinity.model.Recipe;

import java.util.ArrayList;


/**
 * Created by User on 2/12/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    public Context context;
    public ArrayList<Recipe> RecipeItems;

    public RecyclerViewAdapter(Context context, ArrayList<Recipe> RecipeItems) {
        super();
        this.context = context;
        this.RecipeItems = RecipeItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_listitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //holder._photo.setImageResource(RecipeItems.get(position).getPhotoResId());

        //holder._name.setText(RecipeItems.get(position).getName());

        //holder._point.setText(String.valueOf(RecipeItems.get(position).getPoints()) + " points");

        holder._card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, RecipeSingleActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return RecipeItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView _photo;
        TextView _name;
        TextView _point;
        CardView _card;

        public ViewHolder(View itemView) {
            super(itemView);
            _photo = itemView.findViewById(R.id.imgPhoto);
            _name = itemView.findViewById(R.id.name);
            _point = itemView.findViewById(R.id.point);
            _card = itemView.findViewById(R.id.card);
        }
    }

//    public class Holder
//    {
//        ImageView _photo;
//        TextView _name;
//        TextView _point;
//        CardView _card;
//    }


//    @Override
//    public int getCount() {
//        return RecipeItems.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return RecipeItems.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Holder holder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.recycler_listitem, parent, false);
//            holder = new Holder();
//            holder._photo = (ImageView)convertView.findViewById(R.id.imgPhoto);
//            holder._name =  convertView.findViewById(R.id.name);
//            holder._point = (TextView) convertView.findViewById(R.id.point);
//            holder._card =  convertView.findViewById(R.id.card);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder)convertView.getTag();
//        }
//
//        holder._photo.setImageResource(RecipeItems.get(position).getPhotoResId());
//
//        holder._name.setText(RecipeItems.get(position).getName());
//
//        holder._point.setText(String.valueOf(RecipeItems.get(position).getPoints()) + " points");
//
//        holder._card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            Intent intent=new Intent(context, RecipieSingleActivity.class);
//            context.startActivity(intent);
//            }
//        });
//
//        return convertView;
//    }
}
