package com.johnbohne.samsungchallenge;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;


public class ItemDetailFragment extends Fragment {



    private List<String> mFilePaths;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilePaths = new ArrayList<>();

        if (getArguments().containsKey("fileInfo")) {
            mFilePaths = getArguments().getStringArrayList("fileInfo");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.item_list);


        if (mFilePaths.size() != 0) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter());
        }

        return rootView;
    }
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        public SimpleItemRecyclerViewAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detail_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            String filePath = mFilePaths.get(position);
            Glide.with(getContext()).load(Uri.parse("file:///android_asset/" + filePath)).into(holder.mImageView);

            //return everything that comes after the slash
            holder.mFileNameTextView.setText(filePath.substring(filePath.indexOf("/")+1));

        }

        @Override
        public int getItemCount() {
            return mFilePaths.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mFileNameTextView;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mFileNameTextView = (TextView) view.findViewById(R.id.file_name);
                mImageView = (ImageView) view.findViewById(R.id.image_view);
            }


        }
    }
}
