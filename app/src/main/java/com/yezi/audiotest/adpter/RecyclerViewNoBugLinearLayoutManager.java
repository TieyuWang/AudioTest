package com.yezi.audiotest.adpter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author : yezi
 * @date : 2020/4/2 13:54
 * desc   :
 * version: 1.0
 */
public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG = "RecyclerViewNoBugLinearLayoutManager";

    public RecyclerViewNoBugLinearLayoutManager(Context context) {
        super(context);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            //try catch一下
            super.onLayoutChildren( recycler, state );
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "onLayoutChildren: "+e);
            Log.e(TAG, "meet a IOOBE in RecyclerView");
        }

    }

    /**
     * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionItemViewHolder{7e2b30 position=2 id=-1, oldPos=1, pLpos:1 scrap [attachedScrap] tmpDetached not recyclable(1) no parent} androidx.recyclerview.widget.RecyclerView{38b828c VFED..... ......ID 0,19-384,199 #7f080079 app:id/fmt_recyclerview_players}, adapter:com.yezi.audiotest.fragment.MixerTestFragment$PlayersAdapter@8756dd5, layout:androidx.recyclerview.widget.LinearLayoutManager@934d9ea, context:com.yezi.audiotest.MainActivity@b0bc4e4
     *         at androidx.recyclerview.widget.RecyclerView$Recycler.validateViewHolderForOffsetPosition(RecyclerView.java:5974)
     *         at androidx.recyclerview.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:6158)
     *         at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6118)
     *         at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6114)
     *         at androidx.recyclerview.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2303)
     *         at androidx.recyclerview.widget.LinearLayoutManager.layoutChunk(LinearLayoutManager.java:1627)
     *         at androidx.recyclerview.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1587)
     *         at androidx.recyclerview.widget.LinearLayoutManager.onLayoutChildren(LinearLayoutManager.java:665)
     *         at androidx.recyclerview.widget.RecyclerView.dispatchLayoutStep1(RecyclerView.java:4085)
     *         at androidx.recyclerview.widget.RecyclerView.onMeasure(RecyclerView.java:3534)
     *         at android.view.View.measure(View.java:23169)
     *         at android.view.ViewGroup.measureChildWithMargins(ViewGroup.java:6749)
     *         at android.widget.LinearLayout.measureChildBeforeLayout(LinearLayout.java:1535)
     *         at android.widget.LinearLayout.measureVertical(LinearLayout.java:825)
     *         at android.widget.LinearLayout.onMeasure(LinearLayout.java:704)
     *         at android.view.View.measure(View.java:23169)
     *         at android.view.ViewGroup.measureChildWithMargins(ViewGroup.java:6749)
     *         at android.widget.FrameLayout.onMeasure(FrameLayout.java:185)
     *         at androidx.cardview.widget.CardView.onMeasure(CardView.java:260)
     *         at android.view.View.measure(View.java:23169)
     */

}
