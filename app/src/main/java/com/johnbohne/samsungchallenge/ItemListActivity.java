package com.johnbohne.samsungchallenge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





public class ItemListActivity extends AppCompatActivity {

    private static final String TAG = "ItemListActivity";

    HashMap<String, List<String>> mFilePathHashMap;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setVisibility(View.GONE);


        mFilePathHashMap = new HashMap<>();

        listAssetFiles("");

        mContext = this;


        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


    }
    private boolean listAssetFiles(String path) {

        String[] list;
        try {


            list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String folderName : list) {
                    if (!TextUtils.isEmpty(folderName) && !(folderName.equals("images") ||
                            folderName.equals("sounds") || folderName.equals("webkit"))) {
                        String[] secondList = getAssets().list(folderName);
                        List<String> filePaths = new ArrayList<>();
                        StringBuilder sb = new StringBuilder(128);

                        for (String s : secondList) {
                            sb.append(folderName).append("/").append(s);

                            filePaths.add(sb.toString());
                            //reset sb
                            sb.setLength(0);
                        }
                        mFilePathHashMap.put(folderName, filePaths);
                    }
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "IOException happened");
            return false;
        }

        return true;

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter());
    }



    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        List<String> mKeys = new ArrayList<>(mFilePathHashMap.keySet());



        public SimpleItemRecyclerViewAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            //just take the first image
            String filePath = mFilePathHashMap.get(mKeys.get(position)).get(0);
            Glide.with(mContext).load(Uri.parse("file:///android_asset/" + filePath)).into(holder.mImageView);
            holder.mTitleView.setText(mKeys.get(position));
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);


                    ViewPager pager = (ViewPager) dialog.findViewById(R.id.container);

                    pager.setAdapter(new SectionsPagerAdapter(mFilePathHashMap.get(mKeys.get(position))));


                    dialog.show();
                    return true;
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        List<String> fileInfo = mFilePathHashMap.get(mKeys.get(position));
                        //convert from list to arraylist...
                        //only need the filePaths for that particular view, so not passing map
                        intent.putStringArrayListExtra("fileInfo", new ArrayList<>(fileInfo));


                        context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mKeys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.info_text);
                mImageView = (ImageView) view.findViewById(R.id.image_view);
            }


        }
    }
    //FragmentPagerAdapter didn't seem to work because of ViewPager ID error, so using PagerAdapter here
    public class SectionsPagerAdapter extends PagerAdapter {
        List<String> values;
        LayoutInflater inflater;

        public SectionsPagerAdapter(List<String> values) {

            this.values = values;

            inflater = ((Activity)mContext).getLayoutInflater();

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View rootView = inflater.inflate(R.layout.dialog_pager_contents, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);


            rootView.setTag(position);

            container.addView(rootView);

            Glide.with(mContext).load(Uri.parse("file:///android_asset/" + values.get(position))).into(imageView);

            return rootView;

        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;

        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);

        }

        @Override
        public int getCount() {
            return values.size();
        }
    }
}
