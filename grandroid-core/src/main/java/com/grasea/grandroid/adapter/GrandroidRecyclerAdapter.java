package com.grasea.grandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alan Ding on 2016/5/27.
 */
public abstract class GrandroidRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements MultipleSelector, OnClickable<T, VH> {
    public enum ChooseMode {
        NONE, SINGLE, SINGLE_RADIO, MULTIPLE
    }
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 0;
    public ArrayList<T> list;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int itemIds = 0;
    private Class<VH> vhClass;
    private RecyclerItemConfig<VH> recyclerItemConfig;
    private RecyclerItemConfig<VH> headerItemConfig;
    private RecyclerItemConfig<VH> footerItemConfig;

    private ChooseMode chooseMode = ChooseMode.NONE;
    private int currentItem = -1;

    @Deprecated
    /**
     * Use GrandroidRecyclerAdapter(ArrayList,RecyclerItemConfig).
     */
    public GrandroidRecyclerAdapter(ArrayList<T> list, Class recyclerItemConfigClass) {
        this.list = list;
        try {
            this.recyclerItemConfig = (RecyclerItemConfig<VH>) recyclerItemConfigClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        itemIds = recyclerItemConfig.itemIds;
        vhClass = recyclerItemConfig.vhClass;
    }

    public GrandroidRecyclerAdapter(ArrayList<T> list, RecyclerItemConfig recyclerItemConfig) {
        this.list = list;
        this.recyclerItemConfig = recyclerItemConfig;
        itemIds = this.recyclerItemConfig.itemIds;
        vhClass = this.recyclerItemConfig.vhClass;
    }

    @Override
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public RecyclerItemConfig createFooterRecyclerItemConfig(ViewGroup parent, int viewType) {
        return null;
    }

    public RecyclerItemConfig createHeaderRecyclerItemConfig(ViewGroup parent, int viewType) {
        return null;
    }

    public abstract void fillItem(VH holder, int position, T data);

    public RecyclerItemConfig<VH> getFooterItemConfig() {
        return footerItemConfig;
    }

    public RecyclerItemConfig<VH> getHeaderItemConfig() {
        return headerItemConfig;
    }

    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        setList(list, false);
    }

    public RecyclerItemConfig<VH> getRecyclerItemConfig() {
        return recyclerItemConfig;
    }

    @Override
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    @Override
    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public VH getViewHolder(ViewGroup parent, int viewType) {

        try {
            switch (viewType) {
                case TYPE_ITEM: {
                    VH vh = vhClass.getDeclaredConstructor(View.class).newInstance(LayoutInflater.from(parent.getContext()).inflate(itemIds, parent, false));
                    return vh;
                }
                case TYPE_HEADER: {
                    RecyclerItemConfig headerRecyclerItemConfig = createHeaderRecyclerItemConfig(parent, viewType);
                    headerItemConfig = headerRecyclerItemConfig;
                    Class<VH> vhClass = headerRecyclerItemConfig.vhClass;
                    int itemIds = headerRecyclerItemConfig.itemIds;
                    return vhClass.getDeclaredConstructor(View.class).newInstance(LayoutInflater.from(parent.getContext()).inflate(itemIds, parent, false));
                }
                case TYPE_FOOTER: {
                    RecyclerItemConfig footerRecyclerItemConfig = createFooterRecyclerItemConfig(parent, viewType);
                    footerItemConfig = footerRecyclerItemConfig;
                    Class<VH> vhClass = footerRecyclerItemConfig.vhClass;
                    int itemIds = footerRecyclerItemConfig.itemIds;
                    return vhClass.getDeclaredConstructor(View.class).newInstance(LayoutInflater.from(parent.getContext()).inflate(itemIds, parent, false));
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final T itemData = list.get(position);
        fillItem(holder, position, itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseMode != ChooseMode.NONE) {
                    toggleSelection(position);
                }
                onItemClick(holder, position, itemData);
            }
        });
        if (chooseMode != ChooseMode.NONE) {
            onItemChoose(holder, position, selectedItems.get(position, false));
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH viewHolder = getViewHolder(parent, viewType);
        return viewHolder;
    }

    public void onItemChoose(VH holder, int position, boolean isChoosed) {
        holder.itemView.setActivated(isChoosed);
    }

    @Override
    public abstract void onItemClick(VH holder, int index, T item);

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    public void setChooseMode(ChooseMode chooseMode) {
        this.chooseMode = chooseMode;
    }

    public void setList(ArrayList<T> list, boolean withoutNotify) {
        this.list = list;
        if (!withoutNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * Working on Choose Mode is not Choose.NONE .
     *
     * @param position
     */
    @Override
    public void toggleSelection(int position) {
        if (chooseMode != ChooseMode.NONE) {
            boolean isSingleChoose = (chooseMode == ChooseMode.SINGLE);
            boolean isMultipleChoose = (chooseMode == ChooseMode.MULTIPLE);

            if (selectedItems.get(position, false)) {
                if (ChooseMode.SINGLE_RADIO != chooseMode) {
                    selectedItems.delete(position);
                    if (isSingleChoose) {
                        currentItem = -1;
                    }
                } else {
                    return;
                }
            } else {
                if (!isMultipleChoose) {
                    selectedItems.clear();
                    Log.e("grandroid", "將HashSet清空:" + selectedItems.size());
                }
                selectedItems.put(position, true);
                Log.e("grandroid", "選擇:" + position);
            }
            if (!isMultipleChoose && currentItem != -1) {
                notifyItemChanged(currentItem);
                Log.e("grandroid", "Notify變更:" + currentItem + ", " + selectedItems.get(currentItem, false));
            }
            notifyItemChanged(position);
            Log.e("grandroid", "Notify變更:" + position + ", " + selectedItems.get(position, false));
            if (!isMultipleChoose) {
                currentItem = position;
                Log.e("grandroid", "目前currentItem:" + currentItem);
            }
        }
    }
}

