package me.rorschach.schoolcontacts.home.college;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.contacts.ContactsActivity;
import me.rorschach.schoolcontacts.util.HanziToPinyin;

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
        mColleges.clear();
        mColleges.addAll(colleges);
        mCollegeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public static class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeHolder>
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
        public CollegeAdapter.CollegeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view = inflater.inflate(R.layout.item_college, parent, false);
            return new CollegeHolder(view);
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
        public void onBindViewHolder(CollegeAdapter.CollegeHolder holder, int position) {

            String college = colleges.get(position);
            holder.mTvCollegeItem.setText(college);

            if (getItemViewType(position) == ITEM) {
                holder.mTvCollegeHead.setVisibility(View.GONE);

            } else if (getItemViewType(position) == HEAD) {

                String pinyin = HanziToPinyin
                        .getPinYin(college.charAt(0) + "")
                        .charAt(0) + "";

                holder.mTvCollegeHead.setText(pinyin);
            }
        }

        @Override
        public int getItemCount() {
            return colleges.size();
        }

        @NonNull
        @Override
        public String getSectionName(int position) {

            String source = colleges.get(position).charAt(0) + "";
            String result = HanziToPinyin.getPinYin(source);
            return result.charAt(0) + "";
        }

        class CollegeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_college_head)
            TextView mTvCollegeHead;
            @Bind(R.id.tv_college_item)
            TextView mTvCollegeItem;

            public CollegeHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ContactsActivity.class);
                int position = getAdapterPosition();
                intent.putExtra("COLLEGE", colleges.get(position));
                mActivity.startActivity(intent);
            }
        }

    }

}
