package me.rorschach.schoolcontacts.home.college;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;

public class CollegeFragment extends Fragment implements CollegeContract.View {


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
        WeakReference<Activity> reference = new WeakReference<Activity>(getActivity());
        mCollegeAdapter = new CollegeAdapter(reference, mColleges);// TODO: 16-4-25
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

    public static class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeHolder> {

        private Activity mActivity;
        private List<String> colleges;

        public CollegeAdapter(WeakReference<Activity> reference, List<String> colleges) {
            mActivity = reference.get();
            this.colleges = colleges;
        }

        @Override
        public CollegeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.item_college, parent, false);
            return new CollegeHolder(view);
        }

        @Override
        public void onBindViewHolder(CollegeHolder holder, int position) {
            String college = colleges.get(position);
            holder.mTvCollege.setText(college);
        }

        @Override
        public int getItemCount() {
            return colleges.size();
        }

        class CollegeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @Bind(R.id.tv_college)
            TextView mTvCollege;

            public CollegeHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "position:" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
