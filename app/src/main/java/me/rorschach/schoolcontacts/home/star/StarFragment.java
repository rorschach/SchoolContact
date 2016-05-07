package me.rorschach.schoolcontacts.home.star;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.detail.DetailActivity;

public class StarFragment extends Fragment implements StarContract.View {

    @Bind(R.id.rv_star)
    RecyclerView mRvStar;

    private List<Contact> mStared;
    private StarAdapter mStarAdapter;

    private StarContract.Presenter mPresenter;

    public static StarFragment newInstance() {
        StarFragment fragment = new StarFragment();
        return fragment;
    }

    public StarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, container, false);
        ButterKnife.bind(this, view);

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvStar.setLayoutManager(layoutManager);
        mRvStar.setHasFixedSize(true);

        mStared = new ArrayList<>();
        WeakReference<Activity> reference = new WeakReference<Activity>(getActivity());
        mStarAdapter = new StarAdapter(reference.get(), mStared);
        mRvStar.setAdapter(mStarAdapter);
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
    public void setPresenter(StarContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @DebugLog
    @Override
    public void showStars(List<Contact> contacts) {
        mStared.clear();
        mStared.addAll(contacts);
        mStarAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoStar() {
        mStared.clear();
        mStarAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddStar() {

    }

    @Override
    public void showDeleteStar() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public static class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarHolder> {


        private Activity mActivity;
        private List<Contact> mStared;

        public StarAdapter(Activity activity, List<Contact> stared) {
            mActivity = activity;
            mStared = stared;
        }

        @Override
        public StarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.item_star, parent, false);
            return new StarHolder(view);
        }

        @Override
        public void onBindViewHolder(StarHolder holder, int position) {
            Contact contact = mStared.get(position);
            holder.mTvStarName.setText(contact.getName());
//            holder.mTvStarCollege.setText(contact.getCollege());
        }

        @Override
        public int getItemCount() {
            return mStared.size();
        }

        class StarHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_star_name)
            TextView mTvStarName;
//            @Bind(R.id.tv_star_college)
//            TextView mTvStarCollege;

            public StarHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                int position = getAdapterPosition();
                Contact contact = mStared.get(position);
                intent.putExtra("CONTACT", contact);
                mActivity.startActivity(intent);
            }
        }

    }

}
