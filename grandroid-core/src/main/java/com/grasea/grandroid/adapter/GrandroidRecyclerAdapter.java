package com.grasea.grandroid.adapter;

import android.support.v7.widget.RecyclerView;
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
    private SparseBooleanArray selectedItems = new SparseBooleanArray();


    public enum ChooseMode {
        NONE, SINGLE, MULTIPLE
    }

    public ArrayList<T> list;
    private int itemIds = 0;
    private Class<VH> vhClass;
    private RecyclerItemConfig<VH> recyclerItemConfig;
    private ChooseMode chooseMode = ChooseMode.NONE;
    private int currentItem = -1;


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

    @Override
    public abstract void onItemClick(VH holder, int index, T item);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            return vhClass.getDeclaredConstructor(View.class).newInstance(LayoutInflater.from(parent.getContext()).inflate(itemIds, parent, false));
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

    public T getItem(int position) {
        return list.get(position);
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        setList(list, false);
    }

    public void setList(ArrayList<T> list, boolean withoutNotify) {
        this.list = list;
        if (!withoutNotify) {
            notifyDataSetChanged();
        }
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
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    public void onItemChoose(VH holder, int position, boolean isChoosed) {
        holder.itemView.setActivated(isChoosed);
    }

    public abstract void fillItem(VH holder, int position, T data);

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setChooseMode(ChooseMode chooseMode) {
        this.chooseMode = chooseMode;
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
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
                if (isSingleChoose) {
                    currentItem = -1;
                }
            } else {
                if (isSingleChoose) {
                    selectedItems.clear();
                }
                selectedItems.put(position, true);
            }
            if (isSingleChoose && currentItem != -1) {
                notifyItemChanged(currentItem);
            }
            notifyItemChanged(position);
            if (isSingleChoose) {
                currentItem = position;
            }
        }
    }

    @Override
    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
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

}
