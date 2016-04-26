package me.rorschach.schoolcontacts.home.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
        mHistoryAdapter = new HistoryAdapter(mHistories);
        mRvHistory.setAdapter(mHistoryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null) {
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
        this.mHistories.clear();
        this.mHistories.addAll(histories);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoHistory() {

    }

    @Override
    public void showAddHistory() {

    }

    @Override
    public void showDeleteHistory() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

        private List<History> mHistories;

        public HistoryAdapter(List<History> histories) {
            mHistories = histories;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mHistories.size();
        }

        class HistoryHolder extends RecyclerView.ViewHolder {

            public HistoryHolder(View itemView) {
                super(itemView);
            }
        }

    }


}
