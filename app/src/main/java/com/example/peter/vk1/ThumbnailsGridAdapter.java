package com.example.peter.vk1;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Peter on 16.09.2017.
 */

public class ThumbnailsGridAdapter extends BaseAdapter {

        private ArrayList<String> paths;
        private int pageNumber;
        Context mContext;

        com.example.peter.vk1.ThumbnailsGridAdapter.KeyClickListener mListener;

        public ThumbnailsGridAdapter(Context context, ArrayList<String> paths, int pageNumber, com.example.peter.vk1.ThumbnailsGridAdapter.KeyClickListener listener) {
            this.mContext = context;
            this.paths = paths;
            this.pageNumber = pageNumber;
            this.mListener = listener;
        }
        public View getView(int position, View convertView, ViewGroup parent){

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.thumbnails_item, null);
            }

            final String path = paths.get(position);

            ImageView image = (ImageView) v.findViewById(R.id.thumbnails_item);
            image.setImageBitmap(getImage(path));

            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.keyClickedIndex(path);
                }
            });

            return v;
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public String getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public int getPageNumber () {
            return pageNumber;
        }

        private Bitmap getImage (String path) {
            AssetManager mngr = mContext.getAssets();
            InputStream in = null;

            try {
                in = mngr.open("thumbnails/" + path);
            } catch (Exception e){
                e.printStackTrace();
            }

            Bitmap temp = BitmapFactory.decodeStream(in ,null ,null);
            return temp;
        }

        public interface KeyClickListener {

            void keyClickedIndex(String index);
        }
    }

