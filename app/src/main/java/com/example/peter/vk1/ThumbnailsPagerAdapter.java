package com.example.peter.vk1;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import com.example.peter.vk1.ThumbnailsGridAdapter.KeyClickListener;

import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Peter on 16.09.2017.
 */

public class ThumbnailsPagerAdapter extends PagerAdapter{


    ArrayList<String> thumbnails;
    private static final int NO_OF_THUMBNAILS_PER_PAGE = 5;
    FragmentActivity mActivity;
    KeyClickListener mListener;

    public ThumbnailsPagerAdapter(FragmentActivity activity,
                                 ArrayList<String> thumbnails,KeyClickListener listener) {
        this.thumbnails = thumbnails;
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double) thumbnails.size()
                / (double) NO_OF_THUMBNAILS_PER_PAGE);
    }

    @Override
    public Object instantiateItem(View collection, int position) {


        View layout = mActivity.getLayoutInflater().inflate(
                R.layout.thumbnails_grid, null);

        int initialPosition = position * NO_OF_THUMBNAILS_PER_PAGE;
        ArrayList<String> thumbnailsInPage = new ArrayList<String>();

        for (int i = initialPosition; i < initialPosition
                + NO_OF_THUMBNAILS_PER_PAGE
                && i < thumbnails.size(); i++) {
            thumbnailsInPage.add(thumbnails.get(i));
        }

        GridView grid = (GridView) layout.findViewById(R.id.thumbnails_grid);

        ThumbnailsGridAdapter adapter = new ThumbnailsGridAdapter(
                mActivity.getApplicationContext(), thumbnailsInPage, position,
                mListener);
        grid.setAdapter(adapter);

        ((ViewPager) collection).addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
