package me.rorschach.schoolcontacts.search;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.local.Contact;

public class SearchActivity extends AppCompatActivity implements SearchContract.View{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_search)
    RecyclerView mRvSearch;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private SearchContract.Presenter mPresenter;

    private boolean isActive;

    private List<Contact> mResult;
    private SearchAdapter  mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        mToolbar.inflateMenu(R.menu.menu_search);
        mToolbar.setPopupTheme(R.style.AppTheme_ToolBar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvSearch.setLayoutManager(layoutManager);
        mRvSearch.setHasFixedSize(true);

        mResult = new ArrayList<>();
        mSearchAdapter = new SearchAdapter(mResult);
        mRvSearch.setAdapter(mSearchAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        if (searchView != null) {
            searchView.setIconifiedByDefault(true);
            searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "查询" + "</font>"));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                @DebugLog
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });

            MenuItemCompat.expandActionView(menuItem);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    onBackPressed();
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showSearchResult(List<Contact> result) {
        mResult.clear();
        mResult.addAll(result);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoResult() {

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

    private static class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

        private List<Contact> result;

        public SearchAdapter(List<Contact> result) {
            this.result = result;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return result.size();
        }

        static class SearchHolder extends RecyclerView.ViewHolder{
            public SearchHolder(View itemView) {
                super(itemView);
            }
        }

    }


}
