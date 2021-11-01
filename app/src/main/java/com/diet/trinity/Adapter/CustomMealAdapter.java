package com.diet.trinity.Adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diet.trinity.R;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.model.Listmodel;

import org.json.JSONException;

import java.util.ArrayList;

public class CustomMealAdapter extends BaseAdapter  {
    ArrayList listItem = new ArrayList<>();
    public Context context;
    public ArrayList<Listmodel> FoodList;
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;
    public DataSetObservable mDataSetObservable = new DataSetObservable();

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
    public CustomMealAdapter(Context context, ArrayList<Listmodel> FoodList) {
        super();
        this.context = context;
        this.FoodList = FoodList;
    }

    public static class FoodHolder
    {
        public TextView title;
        public TextView gram_txt;
        public TextView point_txt;
        public ImageView id_btn;
    }

    @Override
    public int getCount() {
        return FoodList.size();
    }

    @Override
    public Object getItem(int position) {
        return FoodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FoodHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.meal_list_item, parent, false);
            holder = new FoodHolder();
            holder.title = (TextView) convertView.findViewById(R.id.listitem_title);
            holder.gram_txt = (TextView) convertView.findViewById(R.id.gram_txt);
            holder.point_txt = (TextView) convertView.findViewById(R.id.point_txt);
            holder.id_btn = (ImageView) convertView.findViewById(R.id.imgAddMeal);
            convertView.setTag(holder);
        } else {
            holder = (FoodHolder) convertView.getTag();
        }

        try {
            holder.title.setText(FoodList.get(position).getListTitle());
            holder.gram_txt.setText(FoodList.get(position).getListGram());
            holder.point_txt.setText(FoodList.get(position).getListPoint());
            holder.id_btn.setId(FoodList.get(position).getListId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelper = new MealDatabaseHelper(v.getContext());
                db = openHelper.getWritableDatabase();
                String sql = "DELETE FROM "+MealDatabaseHelper.TABLE_NAME +" WHERE ID = ?";
                db.execSQL(sql, new Integer[]{(v.getId())});

                holder.title.setVisibility(View.GONE);
                holder.gram_txt.setVisibility(View.GONE);
                holder.point_txt.setVisibility(View.GONE);
                holder.id_btn.setVisibility(View.GONE);

            }
        });
        return convertView;
    }
}
