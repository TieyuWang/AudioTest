package com.yezi.audiotest.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author : yezi
 * @date : 2020/3/28 15:06
 * desc   :
 * version: 1.0
 */
public abstract class BaseRecycleViewAdapter<M extends Serializable,B extends ViewDataBinding> extends RecyclerView.Adapter {
    private final String TAG = "BaseRecycleViewAdapter";
    protected List<M> mItemList;
    private Context mContext;
    private int dataVersion;

    public BaseRecycleViewAdapter(Context context){
        mContext = context;
    }

    /**
     * 使用深拷贝 防止 M指向同一地址
     * @param srcList 源list
     * @return 新地址list
     */
    private List<M> listDeepCopy(List<M> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            return (List<M>) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("StaticFieldLeak")
    public void updateList(final List<M> update){
 /*     //暴力刷新  数据量小可以使用
        mItemList = update;
        notifyDataSetChanged();*/
        dataVersion++;
        if(mItemList == null){
            if(update == null){
                return;
            }
            mItemList = listDeepCopy(update);
           // mItemList = update;
            notifyDataSetChanged();
        }else if(update == null){
            int oldSize = mItemList.size();
            mItemList = null;
            notifyItemRangeRemoved(0,oldSize);
        }else{
            final int startVersion = dataVersion;
            final List<M> oldItems = mItemList;
            AsyncTask<Void, Void, DiffUtil.DiffResult> asyncTask = new AsyncTask<Void, Void, DiffUtil.DiffResult>() {

                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            M oldItem = oldItems.get(oldItemPosition);
                            M newItem = update.get(newItemPosition);
                            return BaseRecycleViewAdapter.this.areItemsTheSame(oldItem,newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            M oldItem = oldItems.get(oldItemPosition);
                            M newItem = update.get(newItemPosition);
                            return BaseRecycleViewAdapter.this.areContentsTheSame(oldItem,newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if(startVersion != dataVersion){
                        Log.d(TAG, "update more than one time before refresh, ignore old version");
                        return;
                    }
                    mItemList = listDeepCopy(update);
                   // mItemList = update;
                    Log.d(TAG, "onPostExecute: dispatchUpdatesTo adapter");
                    diffResult.dispatchUpdatesTo(BaseRecycleViewAdapter.this);
                }
            };
            asyncTask.execute();
        }
    }

    /**
     * item的内容是否相同
     * @param oldItem oldItem
     * @param newItem newItem
     * @return 是否相同
     */
    protected abstract boolean areContentsTheSame(M oldItem, M newItem);

    /**
     * item是否相同
     * @param oldItem oldItem
     * @param newItem newItem
     * @return 是否相同
     */
    protected abstract boolean areItemsTheSame(M oldItem, M newItem);


    /**
     * 获取recycleView的Id
     * @return recycleView的Id
     */
    protected abstract @LayoutRes int getItemLayoutResId();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        B itemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),getItemLayoutResId(),parent,false);
        return new ItemViewHolder(itemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(mItemList != null) {
            B itemBinding = DataBindingUtil.findBinding(holder.itemView);
            if(itemBinding != null) {
                onBindItem(itemBinding, mItemList.get(position));
                itemBinding.executePendingBindings();
            }
        }
    }

    /**
     * 绑定item
     * @param itemBinding item的viewBinding
     * @param m item的info
     */
    protected abstract void onBindItem(B itemBinding, M m);

    @Override
    public int getItemCount() {
        return mItemList == null ? 0:mItemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
         ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
