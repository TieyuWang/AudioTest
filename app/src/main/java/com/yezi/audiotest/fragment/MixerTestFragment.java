package com.yezi.audiotest.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.yezi.audiotest.R;

import com.yezi.audiotest.adpter.BaseRecycleViewAdapter;
import com.yezi.audiotest.adpter.RecyclerViewNoBugLinearLayoutManager;
import com.yezi.audiotest.bean.LocalPlayerInfo;
import com.yezi.audiotest.bean.PlayerControl;
import com.yezi.audiotest.databinding.CardPlayerItemBinding;
import com.yezi.audiotest.databinding.FragmentMixerTestBinding;
import com.yezi.audiotest.viewmodel.MixerTestViewModel;
import com.yezi.player.factory.IPlayerController;


import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * @author : yezi
 * @date : 2020/3/27 17:36
 * desc   :
 * version: 1.0
 */
public class MixerTestFragment extends Fragment {
    private final String TAG = "MixerTestFragment";

    private FragmentMixerTestBinding mMixerTestBinding;
    private MixerTestViewModel mMixerTestViewModel;
    private RecyclerView mRecyclerView;

    private MutableLiveData<AudioAttributes> mAddPlayerCommand;
    private MutableLiveData<PlayerControl> mPlayerControl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mMixerTestViewModel = new ViewModelProvider(this,androidViewModelFactory).get(MixerTestViewModel.class);
        Log.d(TAG, "onCreate: "+mMixerTestViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mixer_test,container,false);
        mMixerTestBinding = FragmentMixerTestBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddPlayerCommand = mMixerTestViewModel.getCommandLiveData();
        mPlayerControl = mMixerTestViewModel.getControlLiveData();

        mMixerTestBinding.setQuickOnClickListener(mQuickOnClickListener);
        mRecyclerView = mMixerTestBinding.fmtRecyclerviewPlayers;

        mRecyclerView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getContext(),VERTICAL,false));
        //设置空白分割线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_blank_divider, null));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        final PlayersAdapter playersAdapter = new PlayersAdapter(getContext());
        mRecyclerView.setAdapter(playersAdapter);

        mMixerTestViewModel.getPlayersLiveData().observe(getViewLifecycleOwner(), new Observer<List<LocalPlayerInfo>>() {
            @Override
            public void onChanged(List<LocalPlayerInfo> playerInfo) {
                int size = playerInfo==null ? 0: playerInfo.size();
                Log.d(TAG, "playerList live data update: "+playerInfo+" size = "+size);
                playersAdapter.updateList(playerInfo);
                //立即更新binding的视图，默认在下一动画帧更新
                mMixerTestBinding.executePendingBindings();
            }
        });

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }

    View.OnClickListener mQuickOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
            switch (view.getId()){
                case R.id.quick_add_music:
                    attributesBuilder.setUsage(AudioAttributes.USAGE_MEDIA);
                    attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                    break;
                case R.id.quick_add_notification:
                    attributesBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
                    attributesBuilder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
                    break;
                case R.id.quick_add_tts:
                    //attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                    attributesBuilder.setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY);

                    break;
                case R.id.quick_add_vr:
                    attributesBuilder.setUsage(AudioAttributes.USAGE_ASSISTANT);
                    attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                    break;
                case R.id.quick_add_nav:
                    attributesBuilder.setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE);
                    attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                    break;
                default:
            }
            AudioAttributes audioAttributes = attributesBuilder.build();
            Log.d(TAG, "quick add player: "+audioAttributes);
            mAddPlayerCommand.setValue(attributesBuilder.build());
        }
    };

    class PlayersAdapter extends BaseRecycleViewAdapter<LocalPlayerInfo,CardPlayerItemBinding> {
        private final String TAG = "MixerTestFragment.PlayersAdapter";

        PlayersAdapter(Context context) {
            super(context);
            /*
             * 解决闪烁问题 需要去除super.getItemId(position) 目前没去除 没有出现错乱
             * Adapter中的getItemId要重写成如下，如果仍用super.getItemId(position)，数据刷新会出错。
             *
             * @Override
             * public long getItemId(int position) {
             *    return position;
             * }
             *
             * 4-6 出现错乱
             */
         //   setHasStableIds(true);
        }

/*        *//**
         * 出现错乱
         * return position; =>> mItemList.get(position).hashCode();
         * @param  //position
         * @return
         *//*
        @Override
        public long getItemId(int position) {
            return mItemList.get(position).hashCode();
        }*/

        @Override
        protected boolean areContentsTheSame(LocalPlayerInfo oldItem, LocalPlayerInfo newItem) {
            return oldItem.getPid()==newItem.getPid() && oldItem.getMediaSessionId() == newItem.getMediaSessionId();
        }

        @Override
        protected boolean areItemsTheSame(LocalPlayerInfo oldItem, LocalPlayerInfo newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        protected int getItemLayoutResId() {
            return R.layout.card_player_item;
        }

        @Override
        protected void onBindItem(CardPlayerItemBinding itemBinding, final LocalPlayerInfo playerInfo) {
            Log.d(TAG, "onBindItem: "+playerInfo);
            itemBinding.setLocalPlayerInfo(playerInfo);
            itemBinding.playerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayerControl playerControl = new PlayerControl(playerInfo);
                    mPlayerControl.setValue(playerControl);
                }
            });
            itemBinding.playerClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayerControl playerControl = new PlayerControl(playerInfo);
                    playerControl.cmd = IPlayerController.PLAYER_RELEASE;
                    mPlayerControl.setValue(playerControl);
                }
            });
        }

    }

}
