package com.grasea.grandroid.demo.fragment.tab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.grasea.grandroid.demo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentWithTabActivity extends AppCompatActivity {

    @BindView(R.id.pager_tabs)
    TabLayout pagerTabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_with_tab);
        ButterKnife.bind(this);

    }
}
