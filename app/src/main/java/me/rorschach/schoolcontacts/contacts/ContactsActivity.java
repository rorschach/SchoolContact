package me.rorschach.schoolcontacts.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.search.SearchActivity;
import me.rorschach.schoolcontacts.util.AccessStorageApi;
import me.rorschach.schoolcontacts.util.HanziToPinyin;

public class ContactsActivity extends AppCompatActivity implements ContactsContract.View{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_contacts)
    FastScrollRecyclerView mRvContacts;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private boolean isActive;

    private ContactsContract.Presenter mPresenter;
    private ContactsAdapter mAdapter;
    private List<Contact> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacs);
        ButterKnife.bind(this);
        initView();

        isActive = true;

        setPresenter(new ContactsPresenter(ContactRepository.getInstance(), this));

        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String college = intent.getStringExtra("COLLEGE");
        getSupportActionBar().setTitle(college);
        if (mPresenter != null) {
            mPresenter.loadContacts(college);
        }
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvContacts.setLayoutManager(layoutManager);
        mRvContacts.setHasFixedSize(true);

        mContacts = new ArrayList<>();
        WeakReference<Activity> reference = new WeakReference<Activity>(this);
        mAdapter = new ContactsAdapter(reference.get(), mContacts);
        mRvContacts.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(ContactsActivity.this, SearchActivity.class));
                break;
            case R.id.action_update:
                chooseFile();
                break;
            case R.id.action_export:
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseFile() {

        SharedPreferences sp = this.getSharedPreferences("backup", MODE_PRIVATE);
        String path = sp.getString("BACKUP_PATH",
                Environment.getExternalStorageDirectory().getPath() + "/GnnuContact/");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        Uri uri = Uri.parse("/storage/emulated/0/Download/");
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "text/xml");
//        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "请选择备份文件"), 0x01);
    }

    private static final String TAG = "TAG";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x01 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "onActivityResult: " + uri.toString());

            String path = AccessStorageApi.getPath(this, uri);
            Log.d(TAG, "onActivityResult: " + path);

//            XmlPullParser xpp = Xml.newPullParser();
//            xpp.setInput(new FileInputStream(path));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showContacts(List<Contact> contacts) {
        mContacts.clear();
        mContacts.addAll(contacts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoContact() {

    }

    @Override
    public void showAddContact(@NonNull Contact contact) {
        mContacts.add(contact);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeleteContact(@NonNull Contact contact) {
        mContacts.remove(contact);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setPresenter(ContactsContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    public static class ContactsAdapter
            extends RecyclerView.Adapter<ContactsAdapter.ContactsHolder>
            implements FastScrollRecyclerView.SectionedAdapter{

        private Activity mActivity;
        private List<Contact> mContacts;

        public ContactsAdapter(Activity activity, List<Contact> contacts) {
            mActivity = activity;
            mContacts = contacts;
        }

        @Override
        public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View view = inflater.inflate(R.layout.item_contacts, parent, false);
            return new ContactsHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactsHolder holder, int position) {
            Contact contact = mContacts.get(position);
            holder.mTvContacts.setText(contact.getName() + " - " + contact.getPhone());
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            Contact contact = mContacts.get(position);
            String source = contact.getName().charAt(0) + "";
            String result = HanziToPinyin.getPinYin(source);
            return result.charAt(0) + "";
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        class ContactsHolder extends RecyclerView.ViewHolder{

            @Bind(R.id.tv_contacts)
            TextView mTvContacts;

            public ContactsHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
