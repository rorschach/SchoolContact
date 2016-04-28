package me.rorschach.schoolcontacts.home;

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
import android.view.View;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.joda.time.DateTime;
import org.xmlpull.v1.XmlPullParser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.ContactType;
import me.rorschach.schoolcontacts.data.local.History;
import me.rorschach.schoolcontacts.home.college.CollegeFragment;
import me.rorschach.schoolcontacts.home.college.CollegePresenter;
import me.rorschach.schoolcontacts.home.history.HistoryFragment;
import me.rorschach.schoolcontacts.home.history.HistoryPresenter;
import me.rorschach.schoolcontacts.home.star.StarFragment;
import me.rorschach.schoolcontacts.home.star.StarPresenter;
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

    private void test() {
//        startActivity(new Intent(HomeActivity.this, SearchActivity.class));

//        testContact();

//        testHistory();
    }

    private void testHistory() {

        Observable
                .create(new Observable.OnSubscribe<List<History>>() {
                    @Override
                    public void call(Subscriber<? super List<History>> subscriber) {

                        DateTime dateTime = new DateTime();

                        History history = new History();
                        history.setContactsId(1);
                        history.setContactType(ContactType.PHONE);
                        history.setBeginTime(dateTime);
                        history.setEndTime(dateTime);
                        history.save();

                        List<History> histories =  SQLite.select().from(History.class).queryList();

                        subscriber.onNext(histories);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<History>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<History> histories) {
                        Log.d("TAG", histories.toString());
                    }
                });
    }

    private void testContact() {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {
                        XmlPullParser xpp = getResources().getXml(R.xml.gnnu);
                        ContactRepository repository = ContactRepository.getInstance();

                        List<Contact> contacts = IOUtil.parseXml(xpp);
                        repository.saveAll(contacts);

//                        List<Contact> contacts = repository.searchByKey("黄小平");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        return super.onCreateOptionsMenu(menu);
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

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "记录";
                case 1:
                    return "学院";
                case 2:
                    return "收藏";
                default:
                    return null;
            }
        }
    }
}
