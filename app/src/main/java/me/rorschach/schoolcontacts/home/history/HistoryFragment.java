package me.rorschach.schoolcontacts.home.history;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.local.History;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    @Bind(R.id.rv_history)
    RecyclerView mRvHistory;

    private List<History> mHistories;
    private HistoryAdapter mHistoryAdapter;

    private HistoryContract.Presenter mPresenter;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvHistory.setLayoutManager(layoutManager);
        mRvHistory.setHasFixedSize(true);

        mHistories = new ArrayList<>();
        WeakReference<Activity> reference = new WeakReference<Activity>(getActivity());
        mHistoryAdapter = new HistoryAdapter(reference.get(), mHistories);
        mRvHistory.setAdapter(mHistoryAdapter);
    }

    private static History mHistory;

    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null) {

            if (mHistory != null) {
                DateTime endTime = new DateTime();
                mHistory.setEndTime(endTime);
                mPresenter.addToHistory(mHistory);

                mHistory = null;
            }

            mPresenter.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void setPresenter(HistoryContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @DebugLog
    @Override
    public void showHistories(List<History> histories) {
        mHistories.clear();
        mHistories.addAll(histories);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @DebugLog
    @Override
    public void showNoHistory() {
        mHistories.clear();
        mHistoryAdapter.notifyDataSetChanged();
    }

    @DebugLog
    @Override
    public void showAddHistory(History history) {
        mHistories.add(history);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeleteHistory(History history) {
        mHistories.remove(history);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    public static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {


        private Activity mActivity;
        private List<History> mHistories;

        public HistoryAdapter(Activity activity, List<History> histories) {
            mActivity = activity;
            mHistories = histories;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.item_history, parent, false);
            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            History history = mHistories.get(position);

            DateTime start = history.getBeginTime();
            DateTime end = history.getEndTime();
            int minDiff = Minutes.minutesBetween(start, end).getMinutes();
            int secDiff = Seconds.secondsBetween(start, end).getSeconds();

            String diff;

            if (minDiff != 0) {
                diff = minDiff + "分" + secDiff + "秒";
            } else {
                diff = secDiff + "秒";
            }

            String name = history.getName() + "  -  " + history.getPhone();
            holder.mTvHistoryName.setText(name);

            String time = history.getBeginTime().toString("yyyy-MM-dd HH:mm:ss EE", Locale.CHINESE);
            holder.mTvHistoryTime.setText(time + "  -  " + diff);
        }

        @Override
        public int getItemCount() {
            return mHistories.size();
        }

        class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_history_name)
            TextView mTvHistoryName;
            @Bind(R.id.tv_history_time)
            TextView mTvHistoryTime;

            public HistoryHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                History history = mHistories.get(position);

                Uri uri = Uri.parse("tel:" + history.getPhone());
                Intent intent = new Intent(Intent.ACTION_CALL, uri);

                mHistory = new History();
                mHistory.setName(history.getName());
                mHistory.setPhone(history.getPhone());
                DateTime startTime = new DateTime();
                mHistory.setBeginTime(startTime);

                try {
                    mActivity.startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
