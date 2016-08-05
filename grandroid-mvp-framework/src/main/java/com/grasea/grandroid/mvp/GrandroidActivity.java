package com.grasea.grandroid.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.grasea.grandroid.app.GrandroidApplication;
import com.grasea.grandroid.mvpframework.R;

import de.greenrobot.event.EventBus;

/**
 * Created by USER on 2016/5/7.
 */
public abstract class GrandroidActivity<P extends GrandroidPresenter> extends AppCompatActivity {
    protected GrandroidPresenter presenter;

    private int fragmentContainer = -1;
    protected Boolean mIsCanEixt = true;
    protected Boolean isRegistedListener = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init fragment info.
        isRegistedListener = false;
        FragmentContainer fragmentContainer = this.getClass().getAnnotation(FragmentContainer.class);
        if (fragmentContainer != null) {
            this.fragmentContainer = fragmentContainer.id();
            EventBus.getDefault().register(this);
        }

        Class presenterClass = this.getClass().getAnnotation(UsingPresenter.class).value();
        if (getApplication() instanceof GrandroidApplication) {
            GrandroidApplication app = (GrandroidApplication) getApplication();
            boolean singleton = this.getClass().getAnnotation(UsingPresenter.class).singleton();
            presenter = singleton ? app.getPresenter(presenterClass) : null;
            if (presenter == null) {
                try {
                    presenter = (GrandroidPresenter) presenterClass.newInstance();
                    if (singleton) {
                        app.putPresenter(presenter);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                presenter = (GrandroidPresenter) presenterClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        presenter.setContract(this);
    }


    public P getPresenter() {
        return (P) presenter;
    }

    /**
     * 重新建立一個新的Fragment歷程
     *
     * @param fragmentClass
     * @param tag
     * @param args
     */
    public void turnToFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args) {
        turnToFragment(fragmentClass, tag, args, true);
    }

    /**
     * 重新建立一個新的Fragment歷程
     *
     * @param fragmentClass
     * @param tag
     * @param args
     * @param inHistory
     */
    public void turnToFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args, boolean inHistory) {
        changeFragment(fragmentClass, tag, args, inHistory, true);
    }

    /**
     * 更換當前Fragment並且顯示在最上層，此方法預設為OnResume時不跳過
     *
     * @param fragmentClass 要新增的Fragment
     * @param tag           Fragment Tag
     * @param args          要傳入Fragment的Bundle
     */
    public void changeFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args) {
        changeFragment(fragmentClass, tag, args, true, false);
    }

    public void changeFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args, boolean inHistory) {
        changeFragment(fragmentClass, tag, args, inHistory, false);
    }

    /**
     * 更換當前Fragment並且顯示在最上層
     *
     * @param fragmentClass 要新增的Fragment
     * @param tag           Fragment Tag
     * @param args          要傳入Fragment的Bundle
     * @param inHistory     false: Back回此頁直接跳過
     */
    public void changeFragment(Class<? extends Fragment> fragmentClass, String tag, Bundle args, boolean inHistory, boolean clearTop) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        mIsCanEixt = false;
//        Fragment fragment = fm.findFragmentByTag(tag);
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
            fragment.setArguments(new Bundle());
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (args != null && !args.isEmpty()) {
            fragment.getArguments().putAll(args);
        }
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment instanceof GrandroidFragment) {
            int[] fragmentTransitions = ((GrandroidFragment) fragment).getFragmentTransitions();
            if (fragmentTransitions != null && fragmentTransitions.length == 4) {
                ft.setCustomAnimations(fragmentTransitions[0], fragmentTransitions[2], fragmentTransitions[3], fragmentTransitions[1]);
            }
        } else {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left);
        }
        if (clearTop) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        ft.replace(fragmentContainer, fragment, tag);
        if (inHistory) {
            ft.addToBackStack(tag);
        }
        ft.commit();

    }

    public void onEventMainThread(UISettingEvent event) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentContainer != -1 && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        if (fragmentContainer != -1) {
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }

    public boolean onBackPress() {
        if (fragmentContainer != -1) {
            Log.e("grandroid", "onBackPress fragmentContainer has found.");
            Fragment lastFragment = getLastFragment();
            if (lastFragment != null && lastFragment instanceof GrandroidFragment) {
                Boolean result = ((GrandroidFragment) lastFragment).onBackPress();
                Log.e("grandroid", "onBackPress result:" + result);
                return result;
            }
        }
        Log.e("grandroid", "onBackPress result true");
        return true;
    }

    @Override
    public void onBackPressed() {
        if (onBackPress()) {
            if (fragmentContainer == -1) {
                super.onBackPressed();
                return;
            }
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 1) {
                finish();
                //additional code
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            Log.e("grandroid", "BackPressed returned");
            return;
        }
    }

    /**
     * 取得最上層Fragment
     *
     * @return
     */
    public Fragment getLastFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null) {
                    return fm.getFragments().get(i);
                }
            }
        }
        return null;
    }


    /**
     * 更改UI的Event Class
     */
    public interface UISettingEvent {

    }

}
