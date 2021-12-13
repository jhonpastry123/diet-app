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

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.model.Listmodel;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMealAdapter extends BaseAdapter  {
    public Context context;
    public ArrayList<Listmodel> FoodList;

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
        Listmodel fooditem = FoodList.get(position);

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

        holder.title.setText(fooditem.getListTitle());
        holder.gram_txt.setText(fooditem.getListGram());
        holder.point_txt.setText(FoodList.get(position).getListPoint());
        holder.id_btn.setId(FoodList.get(position).getListId());

        holder.id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REST rest = MainApplication.getContainer().get(REST.class);
                rest.MealsDelete(v.getId())
                        .enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                Boolean flag = response.body();
                                if (flag) {
                                    FoodList.remove(position);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {

                            }
                        });
            }
        });
        return convertView;
    }
}
