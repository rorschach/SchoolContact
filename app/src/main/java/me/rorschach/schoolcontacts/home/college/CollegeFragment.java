package me.rorschach.schoolcontacts.home.college;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.HanziToPinyin;
import me.rorschach.schoolcontacts.R;

public class CollegeFragment extends Fragment implements CollegeContract.View {


    @Bind(R.id.rv_college)
    FastScrollRecyclerView mRvCollege;

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

    public static class CollegeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements FastScrollRecyclerView.SectionedAdapter {

        private Activity mActivity;
        private List<String> colleges;

        private static final int ITEM = 0;
        private static final int HEAD = 1;

        public CollegeAdapter(WeakReference<Activity> reference, List<String> colleges) {
            mActivity = reference.get();
            this.colleges = colleges;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view;

            if (viewType == HEAD) {
                view = inflater.inflate(R.layout.item_college_head, parent, false);
                return new HeadHolder(view);
            } else if (viewType == ITEM) {
                view = inflater.inflate(R.layout.item_college, parent, false);
                return new ItemHolder(view);
            }else {
                return null;
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return HEAD;
            } else {

                String current = colleges.get(position).charAt(0) + "";
                char c = HanziToPinyin.getPinYin(current).charAt(0);

                String previous = colleges.get(position - 1).charAt(0) + "";
                char c1 = HanziToPinyin.getPinYin(previous).charAt(0);

                if (c == c1) {
                    return ITEM;
                } else {
                    return HEAD;
                }
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String college = colleges.get(position);

            if (holder instanceof HeadHolder) {
                ((HeadHolder) holder).mTvCollegeItem.setText(college);

                String pinyin = HanziToPinyin
                        .getPinYin(college.charAt(0) + "")
                        .charAt(0) + "";

                ((HeadHolder) holder).mTvCollegeHead.setText(pinyin);

            } else if(holder instanceof ItemHolder){
                ((ItemHolder) holder).mTvCollegeItem.setText(college);
            }

        }

        @Override
        public int getItemCount() {
            return colleges.size();
        }

        @NonNull
        @Override
        public String getSectionName(int position) {

            if (getItemViewType(position) == ITEM) {
                String source = colleges.get(position).charAt(0) + "";
                String result = HanziToPinyin.getPinYin(source);
                return result.charAt(0) + "";
            }else {
                return "";
            }
        }

        class HeadHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tv_college_head)
            TextView mTvCollegeHead;
            @Bind(R.id.tv_college_item)
            TextView mTvCollegeItem;

            public HeadHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_college_item)
            TextView mTvCollegeItem;

            public ItemHolder(View itemView) {
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
