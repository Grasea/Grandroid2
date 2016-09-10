package com.grasea.grandroid.demo.fragment.normal;

import android.os.Bundle;

import com.grasea.grandroid.demo.R;
import com.grasea.grandroid.mvp.FragmentContainer;
import com.grasea.grandroid.mvp.GrandroidActivity;
import com.grasea.grandroid.mvp.UsingPresenter;

@UsingPresenter(DemoPresenter.class)
@FragmentContainer(id = R.id.fragment_container)
public class DemoFragmentActivity extends GrandroidActivity<DemoPresenter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_fragment);
        //default
        changeFragment(FragmentDemo.class, "FragmentDemo", new Bundle());
        //exclude history(forget current face)
//        changeFragment(FragmentDemo.class, "FragmentDemo", new Bundle(), false);
        //clearTop
//        changeFragmentClearTop(FragmentDemo.class, "FragmentDemo", new Bundle());
    }

    @Override
    public void onEventMainThread(UISettingEvent event) {
        //When Current Fragment OnResume.
        if (event instanceof DemoEvent) {
            DemoEvent event1 = (DemoEvent) event;
            String title = event1.title;
        }
    }
}
