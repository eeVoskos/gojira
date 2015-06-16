package com.gojira.util;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 10/06/2015
 */
public abstract class DataRecyclerAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    private List<T> data;

    public DataRecyclerAdapter() {
        this.data = null;
    }

    public DataRecyclerAdapter(List<T> data) {
        this.data = data;
    }

    public void addItem(T item) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(item);
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public boolean isEmpty() {
        return data != null && data.size() > 0;
    }

    @Override
    public abstract V onCreateViewHolder(ViewGroup parent, int position);

    @Override
    public abstract void onBindViewHolder(V holder, int position);

}
