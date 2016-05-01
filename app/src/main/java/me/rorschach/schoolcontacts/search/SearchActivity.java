package me.rorschach.schoolcontacts.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.util.TextUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class SearchActivity extends AppCompatActivity implements SearchContract.View {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_search)
    RecyclerView mRvSearch;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.tv_empty)
    TextView mTvEmpty;

    @BindString(R.string.search_hint)
    String searchHint;
    @BindString(R.string.title_activity_search)
    String title;

    private SearchView mSearchView;

    private SearchContract.Presenter mPresenter;

    private boolean isActive;

    private List<Contact> mResult;
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();

        isActive = true;

        mPresenter = new SearchPresenter(ContactRepository.getInstance(), this);
    }

    private void initView() {

        mToolbar.inflateMenu(R.menu.menu_search);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvSearch.setLayoutManager(layoutManager);
        mRvSearch.setHasFixedSize(true);

        mResult = new ArrayList<>();
        WeakReference<Activity> reference = new WeakReference<Activity>(this);
        mSearchAdapter = new SearchAdapter(reference.get(), mResult);
        mRvSearch.setAdapter(mSearchAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        keyword = "";
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    private static String keyword = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menuItem.getActionView();

        if (mSearchView != null) {
            mSearchView.setIconifiedByDefault(true);
            mSearchView.setQueryHint(
                    Html.fromHtml("<font color = #F9F9F9>" + searchHint + "</font>"));

            RxSearchView.queryTextChanges(mSearchView)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CharSequence>() {
                        @Override
                        public void onCompleted() {
                            Log.d("TAG", "onCompleted: " + keyword);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", "onError: " + e.getMessage());
                        }

                        @Override
                        public void onNext(CharSequence charSequence) {

                            String cs = charSequence.toString();

                            if (!TextUtils.isEmpty(cs)) {
                                keyword = cs;
                                mPresenter.search(keyword);
                            } else {
                                showNoResult();
                            }
                        }
                    });

//            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                @DebugLog
//                public boolean onQueryTextSubmit(String query) {
//                    if (query.equals(keyword)) {
//                        return false;
//                    }
//                    keyword = query;
//                    mPresenter.search(keyword);
//                    return true;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    if (newText.equals(keyword)) {
//                        return false;
//                    }
//                    keyword = newText;
//                    mPresenter.search(keyword);
//                    return true;
//                }
//            });

            MenuItemCompat.expandActionView(menuItem);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {

                    if ("".equals(keyword)) {
                        onBackPressed();
                    }else {
                        keyword = "";
                    }
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @DebugLog
    @Override
    public void showSearchResult(List<Contact> result) {
        mTvEmpty.setVisibility(View.INVISIBLE);
        mRvSearch.setVisibility(View.VISIBLE);

        mResult.clear();
        mResult.addAll(result);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoResult() {

        mTvEmpty.setVisibility(View.VISIBLE);
        mRvSearch.setVisibility(View.INVISIBLE);

//        mResult.clear();
//        mSearchAdapter.notifyDataSetChanged();
//        Toast.makeText(this, "no result!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    public static class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

        private Activity mActivity;
        private List<Contact> result;

        public SearchAdapter(Activity activity, List<Contact> result) {
            mActivity = activity;
            this.result = result;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.item_result, parent, false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
            Contact contact = result.get(position);

            String result = contact.getName() + " - " + contact.getPhone();

            SpannableStringBuilder textString =
                    TextUtil.highlight(mActivity, result, keyword);

            holder.mTvResult.setText(textString);
        }

        @Override
        public int getItemCount() {
            return result.size();
        }

        static class SearchHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tv_result)
            TextView mTvResult;

            public SearchHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
