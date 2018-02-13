package com.zjl.test.largescreen.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

import com.zjl.test.R;

import java.util.ArrayList;

/**
 * Created by zengjinlong on 18-1-15.
 */

public class HeaderListAdapter extends BaseAdapter {

    ArrayList<HeaderViewHolder> mItemGroups;
    ArrayList<ViewHolder> mData = new ArrayList<ViewHolder>();
    LayoutInflater mInflater;
    OnItemClickListener mOnItemClickListener;
    int mHeaderPadingTop;

    ItemViewHolder mSelectedItem;

    public HeaderListAdapter(Context context, ArrayList<HeaderViewHolder> data,
                             OnItemClickListener onItemClickListener) {
        mInflater = LayoutInflater.from(context);
        mItemGroups = data;
        mOnItemClickListener = onItemClickListener;
        mHeaderPadingTop = (int) context.getResources().getDimension(R.dimen.nav_list_header_padding_top);
        updateData();
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, ItemViewHolder holder);
    }

    public void setData(ArrayList<HeaderViewHolder> data) {
        mItemGroups = data;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        updateData();
        super.notifyDataSetChanged();
    }

    public void updateData() {
        mData.clear();
        int headerCount = mItemGroups.size();
        for (int i = 0; i < headerCount; i++) {
            if (i > 0) {
                // add empty view between groups.
                mData.add(new EmptyViewHolder());
            }
            HeaderViewHolder header = mItemGroups.get(i);
            mData.add(header);
            if (header.mShowChildren && header.mChildren != null) {
                mData.addAll(header.mChildren);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mData.size()) {
            return mData.get(position).getType();
        }
        return ViewHolder.TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return ViewHolder.TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = (ViewHolder) getItem(i);
        if (view == null || ((ViewHolder)view.getTag()).getType() != holder.getType()) {
            view = holder.createView(mInflater);
            if (holder.getType() == ViewHolder.TYPE_EMPTY_ITEM) {
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderPadingTop * 2);
                view.setLayoutParams(lp);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do nothing
                    }
                });
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        holder.onItemClicked(view);
                        if (holder.getType() < ViewHolder.TYPE_SIMPLE_HEADER) {
                            // click on the item
                            if (mSelectedItem != null && mSelectedItem.getId() != holder.getId()) {
                                mSelectedItem.setSelected(false);
                            }
                            mSelectedItem = (ItemViewHolder) holder;
                            mOnItemClickListener.onItemClicked(view, (ItemViewHolder)holder);
                        } else {
                            // click on the header, expand or fold children
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
        view.setTag(holder);
        holder.bindView(i, view);
        return view;
    }
}
