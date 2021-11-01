package com.diet.trinity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.Adapter.SampleCollapsedViewHolder;
import com.diet.trinity.Adapter.SampleExpandedViewHolder;
import com.diet.trinity.R;
import com.diet.trinity.model.PersonalData;
import com.diet.trinity.model.SampleItem;
import com.sysdata.widget.accordion.ExpandableItemHolder;
import com.sysdata.widget.accordion.FancyAccordionView;
import com.sysdata.widget.accordion.Item;
import com.sysdata.widget.accordion.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class PolicyActivity extends AppCompatActivity {
    private static final String KEY_EXPANDED_ID = "expandedId";

    public static final int VIEW_TYPE_1 = 1;
    public static final int VIEW_TYPE_2 = 2;
    ImageView insta_img, facebook_img, twitter_img, q_img, trinity_img;
    TextView  qsa_txt;

    private Toast mToast;
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
        setContentView(R.layout.activity_policy);

        insta_img = findViewById(R.id.instagram);
        facebook_img = findViewById(R.id.facebook);
        twitter_img = findViewById(R.id.twitter);
        q_img = findViewById(R.id.q);
        qsa_txt = findViewById(R.id.QSA);
        trinity_img = findViewById(R.id.trinity);

        insta_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/aggelikizafiraki/"));
                startActivity(browserIntent);
            }
        });

        facebook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AgelikiZafiraki"));
                startActivity(browserIntent);
            }
        });

        twitter_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/a_zafiraki"));
                startActivity(browserIntent);
            }
        });

        q_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.qsa.gr"));
                startActivity(browserIntent);
            }
        });

        trinity_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dietaz.gr/"));
                startActivity(browserIntent);
            }
        });

        mRecyclerView = (FancyAccordionView) findViewById(R.id.alarms_recycler_view);
        {
            mRecyclerView.setCollapsedViewHolderFactory(SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed), mListener);
            mRecyclerView.setExpandedViewHolderFactory(SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded), mListener);
            mRecyclerView.setCollapsedViewHolderFactory(
                    SampleCollapsedViewHolder.Factory.create(R.layout.sample_layout_collapsed),
                    mListener,
                    VIEW_TYPE_1
            );
            mRecyclerView.setExpandedViewHolderFactory(
                    SampleExpandedViewHolder.Factory.create(R.layout.sample_layout_expanded),
                    mListener,
                    VIEW_TYPE_1
            );
            if (savedInstanceState != null) {
                mRecyclerView.setExpandedItemId(savedInstanceState.getLong(KEY_EXPANDED_ID, Item.INVALID_ID));
            }
        }

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(PolicyActivity.this, DailyCaleandarActivity.class);
                    startActivity(intent);

            }
        });

        loadData(PersonalData.getInstance().getMembership());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_EXPANDED_ID, mRecyclerView.getExpandedItemId());
    }

    private void loadData(int membership) {
        final int dataCount = 15;
        int index = 0;

        final List<ExpandableItemHolder> itemHolders = new ArrayList<>(7);
        Item itemModel;
        ExpandableItemHolder itemHolder;

        String[] title = getResources().getStringArray(R.array.listTitle);
        String[] content = getResources().getStringArray(R.array.listFreeContent);

        if (membership == 0) {
            title = getResources().getStringArray(R.array.listTitle);
            content = getResources().getStringArray(R.array.listFreeContent);
        }

        if (membership == 1) {
            title = getResources().getStringArray(R.array.listTitle);
            content = getResources().getStringArray(R.array.listBronzeContent);
        }

        if (membership == 2) {
            title = getResources().getStringArray(R.array.listTitle);
            content = getResources().getStringArray(R.array.listContent);
        }

        for (; index < dataCount; index++) {
            String full_content = content[index];
            if(index == 0)
                full_content = full_content + getResources().getString(R.string.string1);

            else if(index == 13)
                full_content = full_content + getResources().getString(R.string.string12a);

            else if(index == 14)
                full_content = full_content + getResources().getString(R.string.string12b);

            itemModel = SampleItem.create(title[index], full_content);
            itemHolder = new ExpandableItemHolder(itemModel, VIEW_TYPE_1);
            itemHolders.add(itemHolder);
        }

        mRecyclerView.setAdapterItems(itemHolders);
    }

}