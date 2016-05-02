package me.rorschach.schoolcontacts.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import org.xmlpull.v1.XmlPullParser;

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
import me.rorschach.schoolcontacts.util.IOUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

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

        HistoryPresenter historyPresenter = new HistoryPresenter(HistoryRepository.getInstance(), mHistoryFragment);
        collegePresenter = new CollegePresenter(ContactRepository.getInstance(), mCollegeFragment);
        StarPresenter starPresenter = new StarPresenter(ContactRepository.getInstance(), mStarFragment);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
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

    private void test() {
//        importFromFile(null);

//        startActivity(new Intent(HomeActivity.this, SearchActivity.class));

//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_SENDTO);
//        Uri uri = Uri.parse("smsto:"+"18370998101;18370995001");
//        intent.setData(uri);
//        intent.putExtra("sms_body", "test");
//        startActivity(intent);
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

    private void importFromFile(String path) {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {
                        XmlPullParser xpp = getResources().getXml(R.xml.gnnu);
                        ContactRepository repository = ContactRepository.getInstance();

                        List<Contact> contacts = IOUtil.parseXml(xpp);
                        repository.saveAll(contacts);

                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())

                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(HomeActivity.this, "start...", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(HomeActivity.this, "success!", Toast.LENGTH_SHORT).show();
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
