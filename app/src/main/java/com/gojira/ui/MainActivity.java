package com.gojira.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gojira.R;
import com.gojira.app.GojiraApp;
import com.orhanobut.hawk.Hawk;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.pager)
    ViewPager mPager;

    @InjectView(R.id.tabs)
    TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GojiraApp.get(this).getGraph().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Setup action bar
        setSupportActionBar(mToolbar);

        // Setup tabs
        MainTabsPagerAdapter adapter = new MainTabsPagerAdapter(this);
        mPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            onLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onLogout() {
        // Clear secure information
        Hawk.clear();

        // Go to login
        startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    static class MainTabsPagerAdapter extends FragmentPagerAdapter {

        private static final int POSITION_PROJECTS = 0;
        private static final int POSITION_DASHBOARDS = 1;
        private static final int POSITION_COUNT = 2;

        private Context context;

        public MainTabsPagerAdapter(Activity activity) {
            super(activity.getFragmentManager());
            this.context = activity;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POSITION_PROJECTS:
                    return new ProjectListFragment();
                case POSITION_DASHBOARDS:
                    return new DashboardListFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POSITION_PROJECTS:
                    return context.getString(R.string.title_tab_projects);
                case POSITION_DASHBOARDS:
                    return context.getString(R.string.title_tab_dashboards);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return POSITION_COUNT;
        }
    }

}
