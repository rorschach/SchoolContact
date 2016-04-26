package me.rorschach.schoolcontacts.home.college;


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

public class CollegeFragment extends Fragment implements CollegeContract.View{


    @Bind(R.id.rv_college)
    RecyclerView mRvCollege;

    private CollegeContract.Presenter mPresenter;

    private List<String> mColleges;

    private CollegeAdapter mCollegeAdapter;

    public static CollegeFragment newInstance() {
        CollegeFragment fragment = new CollegeFragment();
        return fragment;
    }

    public CollegeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_college, container, false);
        ButterKnife.bind(this, view);

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvCollege.setLayoutManager(layoutManager);
        mRvCollege.setHasFixedSize(true);

        mColleges = new ArrayList<>();
        mCollegeAdapter = new CollegeAdapter(mColleges);// TODO: 16-4-25
        mRvCollege.setAdapter(mCollegeAdapter);
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
    public void setPresenter(CollegeContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @DebugLog
    @Override
    public void showColleges(List<String> colleges) {
        this.mColleges.clear();
        this.mColleges.addAll(colleges);
        mCollegeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeHolder> {

        private List<String> colleges;

        public CollegeAdapter(List<String> colleges) {
            this.colleges = colleges;
        }

        @Override
        public CollegeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(CollegeHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return colleges.size();
        }

        class CollegeHolder extends RecyclerView.ViewHolder {
            public CollegeHolder(View itemView) {
                super(itemView);
            }
        }

    }

}
