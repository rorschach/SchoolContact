package me.rorschach.schoolcontacts.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.History;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.bt_call)
    ImageView mBtCall;
    @Bind(R.id.tv_detail_phone)
    TextView mTvDetailPhone;
    @Bind(R.id.bt_sms)
    ImageView mBtSms;
    @Bind(R.id.rv_record_detail)
    RecyclerView mRvRecordDetail;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    private boolean isActive;

    private DetailContract.Presenter mPresenter;
    private List<History> mHistories;

    private RecordAdapter mRecordAdapter;

    private static Contact mContact;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initView();

        isActive = true;

        setPresenter(new DetailPresenter(ContactRepository.getInstance(), HistoryRepository.getInstance(), this));

        handleIntent();
    }

    private void initView() {
        mToolbar.inflateMenu(R.menu.menu_detail);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvRecordDetail.setLayoutManager(layoutManager);
        mRvRecordDetail.setHasFixedSize(true);

        mHistories = new ArrayList<>();
        WeakReference<Activity> reference = new WeakReference<Activity>(this);
        mRecordAdapter = new RecordAdapter(reference.get(), mHistories);
        mRvRecordDetail.setAdapter(mRecordAdapter);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mContact = (Contact) intent.getSerializableExtra("CONTACT");

        mActionBar.setTitle(mContact.getName());
        mTvDetailPhone.setText(mContact.getPhone());

        if (mPresenter != null) {
            mPresenter.loadRecord(mContact);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActive = true;
        if (mPresenter != null) {
            mPresenter.start();
        }
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
    protected void onRestart() {
        super.onRestart();

        if (mPresenter != null) {
            mPresenter.addRecord(mContact);
        }
    }

    @BindDrawable(R.drawable.ic_star_white_24dp)
    Drawable star;

    @BindDrawable(R.drawable.ic_unstar_white_24dp)
    Drawable unStar;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.action_star);
        menuItem.setIcon(mContact.isStared() ? star : unStar);

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.homeAsUp:
                onBackPressed();
                break;

            case R.id.action_star:

                boolean stared = mContact.isStared();
                mContact.setStared(!stared);

                if (mPresenter != null) {
                    mPresenter.update(mContact);
                }

                break;
            case R.id.action_edit:

                break;
            case R.id.action_delete:

                break;
            case R.id.action_share:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bt_call, R.id.tv_detail_phone})
    public void call() {
        if (mPresenter != null) {
            mPresenter.call(mContact.getPhone());
        }
    }

    @OnClick(R.id.bt_sms)
    public void sms() {
        if (mPresenter != null) {
            mPresenter.sendSms(mContact.getPhone());
        }
    }

    @Override
    public void showDetail(@NonNull Contact contact) {

    }

    @Override
    public Context getContext() {
        WeakReference<Activity> reference = new WeakReference<Activity>(this);
        return reference.get();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onCall() {

    }

    @Override
    public void onSms() {

    }

    @Override
    public void onUpdate() {
        invalidateOptionsMenu();
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void showRecord(List<History> histories) {
        mHistories.clear();
        mHistories.addAll(histories);
        mRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddRecord(History history) {
        mHistories.add(history);
        mRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoRecord() {
        mHistories.clear();
        mRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    public static class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {

        private Activity mActivity;
        private List<History> mHistories;

        public RecordAdapter(Activity activity, List<History> histories) {
            mActivity = activity;
            mHistories = histories;
        }

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.item_contacts, parent, false);
            return new RecordHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordHolder holder, int position) {
            History history = mHistories.get(position);
            String time = history.getBeginTime().toString("yyyy-MM-dd HH:mm:ss EE", Locale.CHINESE);
            holder.mTvContacts.setText(time);
        }

        @Override
        public int getItemCount() {
            return mHistories.size();
        }

        class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_contacts)
            TextView mTvContacts;

            public RecordHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + mContact.getPhone());
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                try {
                    mActivity.startActivityForResult(intent, 0x00);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
