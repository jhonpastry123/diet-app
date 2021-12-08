package com.diet.trinity.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.Adapter.SampleCollapsedViewHolder;
import com.diet.trinity.Adapter.SampleExpandedViewHolder;
import com.diet.trinity.R;
import com.diet.trinity.Utility.TrialTimeHelper;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.model.SampleItem;
import com.google.android.material.tabs.TabLayout;
import com.sysdata.widget.accordion.ExpandableItemHolder;
import com.sysdata.widget.accordion.FancyAccordionView;
import com.sysdata.widget.accordion.Item;
import com.sysdata.widget.accordion.ItemAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrialNotifyActivity extends AppCompatActivity {
    private static final int VIEW_TYPE_1 = 1;
    private static final String KEY_EXPANDED_ID = "expandedId";
    SQLiteDatabase db_trial, db_purchase;
    SQLiteOpenHelper openHelper_trial, openHelper_purchase;
    private FancyAccordionView mRecyclerView;
    private ItemAdapter.OnItemClickedListener mListener = new ItemAdapter.OnItemClickedListener() {
        @Override
        public void onItemClicked(ItemAdapter.ItemViewHolder<?> viewHolder, int id) {
            ItemAdapter.ItemHolder itemHolder = viewHolder.getItemHolder();
            SampleItem item = ((SampleItem) itemHolder.item);
            if(PersonalData.getInstance().getMembership() == 0)
            {
            }
            else {
                switch (id) {
                    case ItemAdapter.OnItemClickedListener.ACTION_ID_COLLAPSED_VIEW:
                        break;
                    case ItemAdapter.OnItemClickedListener.ACTION_ID_EXPANDED_VIEW:
                        break;
                    default:
                        break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_notify);
        openHelper_trial = new TrialTimeHelper(TrialNotifyActivity.this);
        db_trial = openHelper_trial.getWritableDatabase();

        TextView txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra("activity", "trial");
                startActivity(intent);
                finish();
            }
        });
        addEventListener();
        mRecyclerView = (FancyAccordionView) findViewById(R.id.alarms_recycler_view);
        {
            // bind the factory to create view holder for item collapsed
            mRecyclerView.setCollapsedViewHolderFactory(SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed), mListener);

            // bind the factory to create view holder for item expanded
            mRecyclerView.setExpandedViewHolderFactory(SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded), mListener);
            // bind the factory to create view holder for item collapsed of type 1
            mRecyclerView.setCollapsedViewHolderFactory(
                    SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed),
                    mListener,
                    VIEW_TYPE_1
            );
            // bind the factory to create view holder for item collapsed of type 2
//            mRecyclerView.setCollapsedViewHolderFactory(
//                    SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed),
//                    mListener,
//                    VIEW_TYPE_2
//            );

            // bind the factory to create view holder for item expanded of type 1
            mRecyclerView.setExpandedViewHolderFactory(
                    SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded),
                    mListener,
                    VIEW_TYPE_1
            );
            if (savedInstanceState != null) {
                mRecyclerView.setExpandedItemId(savedInstanceState.getLong(KEY_EXPANDED_ID, Item.INVALID_ID));
            }
        }

        // populate RecyclerView with mock data
        loadData();
    }

    private void addEventListener(){
        final RelativeLayout _trial = findViewById(R.id.linTrial);
        final RelativeLayout _premium = findViewById(R.id.linPremium);
        TabLayout _tab = findViewById(R.id.tabMembership);
        _tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    _trial.setVisibility(View.VISIBLE);
                    _premium.setVisibility(View.GONE);
                }
                else{
                    _trial.setVisibility(View.GONE);
                    _premium.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.imgNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Cursor cursor = db_trial.rawQuery("SELECT *FROM " + TrialTimeHelper.TABLE_NAME,  null);

                long trialTime = 0;

                if(cursor.getCount() == 0 || cursor == null)
                {
                    insertTrial(Calendar.getInstance().getTime().getTime()+"");
                }
                else
                {
                    if (cursor.moveToFirst()){
                        do{
                            trialTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TrialTimeHelper.COL_2)));
                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                }
                if((Calendar.getInstance().getTime().getTime() - trialTime) < (1000*60*60*24) || trialTime==0) {
                    Intent intent = new Intent(getBaseContext(), DailyCaleandarActivity.class);
                    startActivity(intent);
                }
            }
        });

        final ImageView next = findViewById(R.id.imgNext2);

        next.setEnabled(false);
        next.setAlpha(0.5f);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PaypalLinkActivity.class);
                startActivity(intent);
            }
        });

        CheckBox checkBox = findViewById(R.id.accepted);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (isChecked) {
                        next.setEnabled(true);
                        next.setAlpha(1f);
                    }
                    else {
                        next.setEnabled(false);
                        next.setAlpha(0.5f);
                    }
                }
            }
        );
    }

    public void insertTrial(String trial_time){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TrialTimeHelper.COL_2, trial_time);
        db_trial.insert(TrialTimeHelper.TABLE_NAME,null,contentValues);
    }

    private void loadData() {
        final int dataCount = 2;
        int index = 0;

        final List<ExpandableItemHolder> itemHolders = new ArrayList<>(7);
        Item itemModel;
        ExpandableItemHolder itemHolder;

        String[] title = getResources().getStringArray(R.array.premiumListTitle);
        String[] content = {getResources().getString(R.string.string12a), getResources().getString(R.string.string12b)};

        for (; index < dataCount; index++) {
            String full_content = content[index];
            itemModel = SampleItem.create(title[index], full_content);
            itemHolder = new ExpandableItemHolder(itemModel, VIEW_TYPE_1);
            itemHolders.add(itemHolder);
        }

        mRecyclerView.setAdapterItems(itemHolders);
    }
}