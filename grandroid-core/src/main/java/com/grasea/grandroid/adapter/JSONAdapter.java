/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public class JSONAdapter extends UniversalAdapter<JSONObject> implements ItemClickable<JSONObject>, Filterable {

    /**
     *
     */
    protected JSONArray array;
    protected boolean cycle;

    /**
     *
     * @param context
     * @param array
     */
    public JSONAdapter(Context context, JSONArray array) {
        super(context);
        this.array = array;
    }

    public JSONAdapter(Context context, JSONArray array, boolean cycle) {
        super(context);
        this.array = array;
        this.cycle = cycle;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public int getCycleIndex() {
        return 3000 - (3000 % array.length());
    }

    /**
     *
     * @return
     */
    public JSONArray getArray() {
        return array;
    }

    /**
     *
     * @param array
     */
    public void setArray(JSONArray array) {
        this.array = array;
        this.notifyDataSetInvalidated();
    }

    /**
     *
     * @return
     */
    public int getCount() {
        if (array != null) {
            if (cycle) {
                return Integer.MAX_VALUE;
            } else {
                return array.length();
            }
        } else {
            return 0;
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public Object getItem(int index) {
        if (array != null) {
            try {
                if (cycle) {
                    return array.getJSONObject(index % array.length());
                } else {
                    return array.getJSONObject(index);
                }
            } catch (JSONException ex) {
                Log.e("facepaper-friendadapter", ex.getMessage());
            }
        }
        return null;
    }

    /**
     *
     * @param index
     * @return
     */
    public long getItemId(int index) {
        return index;
    }

    /**
     *
     * @param index
     * @param cellRenderer
     * @param parent
     * @return
     */
    public View getView(int index, View cellRenderer, ViewGroup parent) {
        try {
            JSONObject obj = array.getJSONObject(cycle ? index % array.length() : index);
            if (cellRenderer == null) {
                // create the cell renderer
                cellRenderer = createRowView(index, obj);
            }
            fillRowView(index, cellRenderer, obj);
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }

        return cellRenderer;
    }

    /**
     *
     * @param index
     * @param item
     * @return
     */
    public View createRowView(int index, JSONObject item) {
        if (rowAdapter != null) {
            return rowAdapter.createRowView(context, index, item);
        } else {
            return new TextView(context);
        }
    }

    /**
     *
     * @param index
     * @param cellRenderer
     * @param item
     * @throws JSONException
     */
    public void fillRowView(int index, View cellRenderer, JSONObject item) throws Exception {
        if (rowAdapter != null) {
            rowAdapter.fillRowView(context, index, cellRenderer, item);
        } else {
            ((TextView) cellRenderer).setText(item.getString("name"));
        }
    }

    /**
     *
     * @param index
     * @param view
     * @param item
     */
    public void onClickItem(int index, View view, JSONObject item) {
        if (rowAdapter != null && ItemClickable.class.isInstance(rowAdapter)) {
            ((ItemClickable) rowAdapter).onClickItem(index, view, item);
        }
    }

    /**
     *
     * @param index
     * @param view
     * @param item
     */
    public void onLongPressItem(int index, View view, JSONObject item) {
        if (rowAdapter != null && ItemClickable.class.isInstance(rowAdapter)) {
            ((ItemClickable) rowAdapter).onLongPressItem(index, view, item);
        }
    }

    public boolean isMatch(CharSequence prefix, JSONObject item) {
        if (prefix == null || prefix.length() == 0) {
            return false;
        }
        if (item.toString().contains(prefix)) {
            return true;
        } else {
            return false;
        }
    }

    public JSONArray getFilterSource() {
        return array;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults r = new FilterResults();
                //ArrayList<String> list = new ArrayList<String>();
                JSONArray source = getFilterSource();
                JSONArray arr = new JSONArray();
                for (int i = 0; i < source.length(); i++) {
                    try {
                        if (isMatch(prefix, source.getJSONObject(i))) {
                            arr.put(source.getJSONObject(i));
                            //list.add(getLabel(source.getJSONObject(i)));
                        }
                    } catch (JSONException ex) {
                        Log.e("grandroid", null, ex);
                    }
                }
                r.count = arr.length();
                r.values = arr;
                return r;
            }

            @Override
            protected void publishResults(CharSequence arg0, FilterResults result) {
                setArray((JSONArray) result.values);
                //JSONAdapter.this.notifyDataSetChanged();
            }
        };
    }
}
