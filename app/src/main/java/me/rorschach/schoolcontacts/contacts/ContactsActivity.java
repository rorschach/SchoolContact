package me.rorschach.schoolcontacts.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.detail.DetailActivity;
import me.rorschach.schoolcontacts.search.SearchActivity;
import me.rorschach.schoolcontacts.util.HanziToPinyin;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static me.rorschach.schoolcontacts.util.IOUtil.COLLEGE;
import static me.rorschach.schoolcontacts.util.IOUtil.export2VcfFile;
import static me.rorschach.schoolcontacts.util.IOUtil.export2XmlFile;

public class ContactsActivity extends AppCompatActivity implements ContactsContract.View {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_contacts)
    FastScrollRecyclerView mRvContacts;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private static final String TAG = "ContactsActivity";

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
        mToolbar.inflateMenu(R.menu.menu_contacts);
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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactsActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        handleIntent();
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
        inflater.inflate(R.menu.menu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_list_xml:
                exportCurrentCollege2Xml();
                break;

            case R.id.action_share_list_vcf:
                exportCurrentCollege2Vcf();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportCurrentCollege2Xml() {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        try {
                            export2XmlFile(ContactsActivity.this, mContacts, COLLEGE);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                        subscriber.onNext(mContacts);
                        subscriber.onCompleted();
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(ContactsActivity.this, "exportCurrentCollege2Xml start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(ContactsActivity.this, "exportCurrentCollege2Xml success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(ContactsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        Log.d("TAG", "size : " + contacts.size());
                    }
                });
    }

    private void exportCurrentCollege2Vcf() {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        try {
                            export2VcfFile(ContactsActivity.this, mContacts, COLLEGE);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                        subscriber.onNext(mContacts);
                        subscriber.onCompleted();
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(ContactsActivity.this, "exportCurrentCollege2Vcf start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(ContactsActivity.this, "exportCurrentCollege2Vcf success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(ContactsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        Log.d("TAG", "size : " + contacts.size());
                    }
                });
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
            implements FastScrollRecyclerView.SectionedAdapter {

        private static final int ITEM = 0;
        private static final int HEAD = 1;

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
            String name = contact.getName();

            holder.mTvContacts.setText(name);

            if (getItemViewType(position) == ITEM) {
                holder.mTvContactsHead.setVisibility(View.GONE);

            } else if (getItemViewType(position) == HEAD) {
                String pinyin = HanziToPinyin
                        .getPinYin(name.charAt(0) + "")
                        .charAt(0) + "";

                holder.mTvContactsHead.setText(pinyin);
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return HEAD;
            } else {

                String current = mContacts.get(position).getName().charAt(0) + "";
                char c = HanziToPinyin.getPinYin(current).charAt(0);

                String previous = mContacts.get(position - 1).getName().charAt(0) + "";
                char c1 = HanziToPinyin.getPinYin(previous).charAt(0);

                if (c == c1) {
                    return ITEM;
                } else {
                    return HEAD;
                }
            }
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

        class ContactsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.tv_contacts_head)
            TextView mTvContactsHead;
            @Bind(R.id.tv_contacts)
            TextView mTvContacts;

            public ContactsHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                int position = getAdapterPosition();
                Contact contact = mContacts.get(position);
                intent.putExtra("CONTACT", contact);
                mActivity.startActivity(intent);
            }
        }
    }
}
