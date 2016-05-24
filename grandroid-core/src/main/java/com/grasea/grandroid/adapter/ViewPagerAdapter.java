/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author Rovers
 */
public class ViewPagerAdapter extends PagerAdapter {

    protected UniversalAdapter adapter;
    //private View currentView;

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        currentView = (View) object;
//        super.setPrimaryItem(container, position, object);
//    }
    public ViewPagerAdapter(UniversalAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        Log.d("grandroid", "create viewpager view " + position);
        View root = adapter.getView(position, null, null);
        ((ViewGroup) collection).addView(root, 0);

        return root;
    }

    public Object getItem(int index) {
        return adapter.getItem(index);
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Remove a page for the given position. The adapter is responsible for
     * removing the view from its container, although it only must ensure this
     * is done by the time it returns from {@link #finishUpdate()}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * {@link #instantiateItem(View, int)}.
     */
    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewGroup) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    /**
     * Called when the a change in the shown pages has been completed. At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     *
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }
}
