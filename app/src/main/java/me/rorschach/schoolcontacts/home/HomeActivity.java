package me.rorschach.schoolcontacts.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.home.college.CollegeFragment;
import me.rorschach.schoolcontacts.home.college.CollegePresenter;
import me.rorschach.schoolcontacts.home.history.HistoryFragment;
import me.rorschach.schoolcontacts.home.history.HistoryPresenter;
import me.rorschach.schoolcontacts.home.star.StarFragment;
import me.rorschach.schoolcontacts.home.star.StarPresenter;
import me.rorschach.schoolcontacts.search.SearchActivity;
import me.rorschach.schoolcontacts.util.AccessStorageApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static me.rorschach.schoolcontacts.util.IOUtil.*;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.appbar)
    AppBarLayout mAppbar;
    @Bind(R.id.container)
    ViewPager mContainer;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.main_content)
    CoordinatorLayout mMainContent;

    @BindDrawable(R.drawable.ic_college_white_24dp)
    Drawable collegeDrawable;
    @BindDrawable(R.drawable.ic_record_white_24dp)
    Drawable recordDrawable;
    @BindDrawable(R.drawable.ic_star_white_24dp)
    Drawable starDrawable;

    private PagerAdapter mPagerAdapter;

    private static HistoryFragment mHistoryFragment;
    private static CollegeFragment mCollegeFragment;
    private static StarFragment mStarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initView();
    }

    CollegePresenter collegePresenter;
    HistoryPresenter historyPresenter;
    StarPresenter starPresenter;

    private void initView() {

        mToolbar.inflateMenu(R.menu.menu_search);

        mCollegeFragment = CollegeFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();
        mStarFragment = StarFragment.newInstance();

        setSupportActionBar(mToolbar);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mContainer.setAdapter(mPagerAdapter);
        mContainer.setCurrentItem(1);

        mTabs.setupWithViewPager(mContainer);
        mTabs.getTabAt(0).setIcon(recordDrawable);
        mTabs.getTabAt(1).setIcon(collegeDrawable);
        mTabs.getTabAt(2).setIcon(starDrawable);

        historyPresenter = new HistoryPresenter(HistoryRepository.getInstance(), mHistoryFragment);
        collegePresenter = new CollegePresenter(ContactRepository.getInstance(), mCollegeFragment);
        starPresenter = new StarPresenter(ContactRepository.getInstance(), mStarFragment);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                break;
            case R.id.action_import_vcf:
//                chooseFile();
                break;
            case R.id.action_update_all:
                chooseFile(UPDATE_ALL);
                break;
            case R.id.action_export_vcf:
                exportAll2VcfFile();
                break;
            case R.id.action_export_xml:
                exportAll2XmlFile();
                break;
            case R.id.action_clear:
                historyPresenter.clearAllHistory();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static final int IMPORT_VCF = 0x00;
    public static final int UPDATE_ALL = 0x01;

    private void chooseFile(int flag) {

//        SharedPreferences sp = this.getSharedPreferences("backup", MODE_PRIVATE);
//        String path = sp.getString("BACKUP_PATH",
//                Environment.getExternalStorageDirectory().getPath() + "/GnnuContact/");

        String path = null;
        try {
            path = getUpdateXmlFilePath(HomeActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "text/xml");
        startActivityForResult(Intent.createChooser(intent, "请选择备份文件"), flag);
    }

    private static final String TAG = "TAG";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_ALL && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "onActivityResult: " + uri.toString());

            String path = AccessStorageApi.getPath(this, uri);
            Log.d(TAG, "onActivityResult: " + path);

            updateAllFromXmlFile(path);
        } else if (requestCode == IMPORT_VCF && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "onActivityResult: " + uri.toString());

            String path = AccessStorageApi.getPath(this, uri);
            Log.d(TAG, "onActivityResult: " + path);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAllFromXmlFile(final String path) {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        ContactRepository contactRepository = ContactRepository.getInstance();
                        HistoryRepository historyRepository = HistoryRepository.getInstance();

                        contactRepository.deleteAll();
                        historyRepository.deleteAll();

                        List<Contact> contacts = null;

                        try {
                            contacts = importFromXmlBackup(path);
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }

                        ContactRepository.getInstance().saveAll(contacts);

                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())

                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(HomeActivity.this, "updateAllFromXmlFile start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(HomeActivity.this, "updateAllFromXmlFile success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        Log.d("TAG", "size : " + contacts.size());

                        collegePresenter.loadColleges();
                        starPresenter.loadStars();
                        historyPresenter.loadHistories();
                    }
                });
    }

    private void exportAll2XmlFile() {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        List<Contact> contacts = ContactRepository.getInstance().queryList(Contact.class);

                        try {
                            export2XmlFile(HomeActivity.this, contacts, ALL);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(HomeActivity.this, "exportAll2XmlFile start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(HomeActivity.this, "exportAll2XmlFile success!", Toast.LENGTH_SHORT).show();
                        chooseFile(0x03);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        Log.d("TAG", "size : " + contacts.size());

                    }
                });
    }

    private void exportAll2VcfFile() {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        List<Contact> contacts = ContactRepository.getInstance().queryList(Contact.class);

                        try {
                            export2VcfFile(HomeActivity.this, contacts, ALL);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(HomeActivity.this, "exportAll2VcfFile start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(HomeActivity.this, "exportAll2VcfFile success!", Toast.LENGTH_SHORT).show();
                        chooseFile(0x03);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        Log.d("TAG", "size : " + contacts.size());
                    }
                });
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mHistoryFragment;

                case 1:
                    return mCollegeFragment;

                case 2:
                    return mStarFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }


//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "记录";
//                case 1:
//                    return "学院";
//                case 2:
//                    return "收藏";
//                default:
//                    return null;
//            }
//        }
    }
}
