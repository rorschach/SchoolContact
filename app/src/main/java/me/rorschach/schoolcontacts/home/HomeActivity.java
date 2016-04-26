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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.home.college.CollegeFragment;
import me.rorschach.schoolcontacts.home.college.CollegePresenter;
import me.rorschach.schoolcontacts.home.history.HistoryFragment;
import me.rorschach.schoolcontacts.home.history.HistoryPresenter;
import me.rorschach.schoolcontacts.home.star.StarFragment;
import me.rorschach.schoolcontacts.home.star.StarPresenter;

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

    private void initView() {

        mCollegeFragment = CollegeFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();
        mStarFragment = StarFragment.newInstance();

        setSupportActionBar(mToolbar);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mContainer.setAdapter(mPagerAdapter);
        mContainer.setCurrentItem(1);

        mTabs.setupWithViewPager(mContainer);

        HistoryPresenter historyPresenter = new HistoryPresenter(HistoryRepository.getInstance(), mHistoryFragment);
        CollegePresenter collegePresenter = new CollegePresenter(ContactRepository.getInstance(), mCollegeFragment);
        StarPresenter starPresenter = new StarPresenter(ContactRepository.getInstance(), mStarFragment);
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
