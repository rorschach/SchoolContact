package me.rorschach.schoolcontacts.home.star;


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
import me.rorschach.schoolcontacts.data.local.Contact;

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
        mStarAdapter = new StarAdapter(mStared);
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

    }

    @Override
    public void showNoStar() {

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

    private static class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarHolder> {

        private List<Contact> mStared;

        public StarAdapter(List<Contact> stared) {
            mStared = stared;
        }

        @Override
        public StarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(StarHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mStared.size();
        }

        static class StarHolder extends RecyclerView.ViewHolder {
            public StarHolder(View itemView) {
                super(itemView);
            }
        }

    }

}
