package com.diet.trinity.activity;

import android.content.Intent;
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
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.model.SampleItem;
import com.google.android.material.tabs.TabLayout;
import com.sysdata.widget.accordion.ExpandableItemHolder;
import com.sysdata.widget.accordion.FancyAccordionView;
import com.sysdata.widget.accordion.Item;
import com.sysdata.widget.accordion.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrialNotifyActivity extends AppCompatActivity {
    private static final int VIEW_TYPE_1 = 1;
    private static final String KEY_EXPANDED_ID = "expandedId";
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

        TextView txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        addEventListener();
        mRecyclerView = (FancyAccordionView) findViewById(R.id.alarms_recycler_view);
        {
            mRecyclerView.setCollapsedViewHolderFactory(SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed), mListener);
            mRecyclerView.setExpandedViewHolderFactory(SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded), mListener);
            mRecyclerView.setCollapsedViewHolderFactory(
                    SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed),
                    mListener
            );
            mRecyclerView.setExpandedViewHolderFactory(
                    SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded),
                    mListener
            );
            if (savedInstanceState != null) {
                mRecyclerView.setExpandedItemId(savedInstanceState.getLong(KEY_EXPANDED_ID, Item.INVALID_ID));
            }
        }

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

            }
        });

        final ImageView next = findViewById(R.id.imgNext2);

        next.setEnabled(false);
        next.setAlpha(0.3f);
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
                        next.setAlpha(0.3f);
                    }
                }
            }
        );
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
            itemHolder = new ExpandableItemHolder(itemModel);
            itemHolders.add(itemHolder);
        }

        mRecyclerView.setAdapterItems(itemHolders);
    }
}