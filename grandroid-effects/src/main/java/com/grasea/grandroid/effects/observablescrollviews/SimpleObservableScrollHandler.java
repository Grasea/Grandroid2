package com.grasea.grandroid.effects.observablescrollviews;
/*
 * Copyright (C) 2016 Alan Ding
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Alan Ding on 2016/5/31.
 */
public class SimpleObservableScrollHandler implements ObservableScrollHandler, ObservableScrollViewCallbacks {
    private Handler mHandler;
    private int scrollViewFullHeight, scrollViewCanScrollHeight, scrollViewHeight, mSlop;
    private boolean mScrolled;
    private boolean adjustEnable = true;
    private ScrollState mLastScrollState;

    private TouchInterceptionFrameLayout touchInterceptionFrameLayout;
    private Scrollable scrollView;
    private View[] shouldStayViews;
    private View[] hasClickEventViews;

    private TouchInterceptionFrameLayout.TouchInterceptionListener interceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {

        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (hasClickEventViews != null) {
                for (View v : hasClickEventViews) {
                    Rect rect = new Rect();
                    v.getHitRect(rect);
                    if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                        return false;
                    }
                }
            }

            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = scrollView;
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any
            // longer.
            int overlayHeight = scrollViewCanScrollHeight;
            int translationY = (int) ViewHelper.getTranslationY(touchInterceptionFrameLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;

            if (scrollingUp) {
                if (translationY == -overlayHeight && scrollView.getCurrentScrollY() - diffY > 0) {
                    return false;
                } else if (translationY == -overlayHeight && scrollView.getCurrentScrollY() - diffY < 0) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.UP;
                    return true;
                } else if (translationY < 0) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.UP;
                    return true;
                }
            } else if (scrollingDown) {
                if (-overlayHeight < translationY) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.DOWN;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        public void onDownMotionEvent(MotionEvent ev) {
        }

        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(touchInterceptionFrameLayout) + diffY, -scrollViewCanScrollHeight, 0);

            ViewHelper.setTranslationY(touchInterceptionFrameLayout, translationY);
            if (shouldStayViews != null && shouldStayViews.length > 0) {
                for (View v : shouldStayViews) {
                    ViewHelper.setTranslationY(v, -translationY);
                }
            }
            requestRootLayout(translationY);
            onViewResize(translationY);
        }

        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
            adjustScrollableView(mLastScrollState);
        }
    };

    public void onViewResize(float translationY) {

    }

    /**
     * @param shouldStayViews              不隨layout滑動的Views
     * @param scrollView                   可滑動的ScrollView
     * @param touchInterceptionFrameLayout
     */
    public SimpleObservableScrollHandler(View[] shouldStayViews, View[] hasClickEventViews, Scrollable scrollView, TouchInterceptionFrameLayout touchInterceptionFrameLayout) {
        ViewConfiguration vc = ViewConfiguration.get(touchInterceptionFrameLayout.getContext());
        mHandler = new Handler();
        mSlop = vc.getScaledTouchSlop();
        this.shouldStayViews = shouldStayViews;
        this.hasClickEventViews = hasClickEventViews;
        this.scrollView = scrollView;
        this.touchInterceptionFrameLayout = touchInterceptionFrameLayout;
        touchInterceptionFrameLayout.setScrollInterceptionListener(interceptionListener);
        scrollView.setScrollViewCallbacks(this);
        scrollView.setTouchInterceptionViewGroup(touchInterceptionFrameLayout);
        getHeights();
    }

    public SimpleObservableScrollHandler(Scrollable scrollView, TouchInterceptionFrameLayout touchInterceptionFrameLayout) {
        this(null, null, scrollView, touchInterceptionFrameLayout);
    }

    private void getHeights() {
        ScrollUtils.addOnGlobalLayoutListener(touchInterceptionFrameLayout, new Runnable() {
            @Override
            public void run() {
                scrollViewFullHeight = touchInterceptionFrameLayout.getHeight();
            }
        });
        if (scrollView instanceof View) {
            ScrollUtils.addOnGlobalLayoutListener(((View) scrollView), new Runnable() {
                @Override
                public void run() {
                    scrollViewHeight = ((View) scrollView).getHeight();

                }
            });
        }
        if (shouldStayViews != null && shouldStayViews.length > 0 && scrollViewCanScrollHeight == 0) {
            ScrollUtils.addOnGlobalLayoutListener(shouldStayViews[0], new Runnable() {
                @Override
                public void run() {
                    scrollViewCanScrollHeight = shouldStayViews[0].getHeight();

                }
            });
        }
    }

    @Override
    public void setTouchInterceptionFrameLayout(TouchInterceptionFrameLayout interceptionFrameLayout) {
        this.touchInterceptionFrameLayout = interceptionFrameLayout;
    }

    @Override
    public TouchInterceptionFrameLayout getTouchInterceptionFrameLayout() {
        return touchInterceptionFrameLayout;
    }

    @Override
    public void setScrollView(ObservableScrollView scrollableView) {
        this.scrollView = scrollableView;
    }

    @Override
    public Scrollable getScrollView() {
        return scrollView;
    }

    @Override
    public void setCanScrollValue(int recyclerCanScrollHeight) {
        this.scrollViewCanScrollHeight = recyclerCanScrollHeight;
    }

    @Override
    public int getCanScrollValue() {
        return scrollViewCanScrollHeight;
    }

    @Override
    public void scrollTo(float position) {
        animateScrollView(position);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (!mScrolled) {
            // This event can be used only when TouchInterceptionFrameLayout
            // doesn't handle the consecutive events.
//            gvPhoto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            gvPhoto.requestLayout();
            adjustScrollableView(scrollState);
        }
    }

    public void setAdjustEnable(boolean adjustEnable) {
        this.adjustEnable = adjustEnable;
    }

    private boolean scrollViewOnDefault() {
        return ViewHelper.getTranslationY(touchInterceptionFrameLayout) == 0;
    }

    private boolean scrollViewOnFull() {
        return ViewHelper.getTranslationY(touchInterceptionFrameLayout) == -scrollViewCanScrollHeight;
    }

    private void scrollToDefault() {
        if (!scrollViewOnFull() && !scrollViewOnDefault()) {
            animateScrollView(0);
        }
    }

    private void scrollToFull() {
        animateScrollView(-scrollViewCanScrollHeight);
    }

    private void requestRootLayout(float translationY) {
        if (touchInterceptionFrameLayout.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) touchInterceptionFrameLayout.getLayoutParams();
            lp.height = (int) (-translationY + scrollViewFullHeight);
            touchInterceptionFrameLayout.setLayoutParams(lp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    touchInterceptionFrameLayout.requestLayout();
                }
            });

        } else if (touchInterceptionFrameLayout.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) touchInterceptionFrameLayout.getLayoutParams();
            lp.height = (int) (-translationY + scrollViewFullHeight);
            touchInterceptionFrameLayout.setLayoutParams(lp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    touchInterceptionFrameLayout.requestLayout();
                }
            });
        } else if (touchInterceptionFrameLayout.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) touchInterceptionFrameLayout.getLayoutParams();
            lp.height = (int) (-translationY + scrollViewFullHeight);
            touchInterceptionFrameLayout.setLayoutParams(lp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    touchInterceptionFrameLayout.requestLayout();
                }
            });
        } else {
            ViewGroup.LayoutParams lp = touchInterceptionFrameLayout.getLayoutParams();
            lp.height = (int) (-translationY + scrollViewFullHeight);
            touchInterceptionFrameLayout.setLayoutParams(lp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    touchInterceptionFrameLayout.requestLayout();
                }
            });
        }
    }

    private void animateScrollView(final float toY) {
        float layoutTranslationY = ViewHelper.getTranslationY(touchInterceptionFrameLayout);
        if (layoutTranslationY != toY) {
            ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(touchInterceptionFrameLayout), toY).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float translationY = (Float) animation.getAnimatedValue();
                    ViewHelper.setTranslationY(touchInterceptionFrameLayout, translationY);
                    if (shouldStayViews != null && shouldStayViews.length > 0) {
                        for (View v : shouldStayViews) {
                            ViewHelper.setTranslationY(v, -translationY);
                        }
                    }
                    requestRootLayout(translationY);
                    onViewResize(translationY);
                }
            });
//            animator.addListener(new Animator.AnimatorListener() {
//
//                public void onAnimationStart(Animator anmtr) {
//                    if (toY != 0) {
//                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) touchInterceptionFrameLayout.getLayoutParams();
//                        lp.height = (int) (-toY + scrollViewFullHeight);
//                        touchInterceptionFrameLayout.requestLayout();
//                    }
//                }
//
//                public void onAnimationEnd(Animator anmtr) {
//                }
//
//                public void onAnimationCancel(Animator anmtr) {
//                }
//
//                public void onAnimationRepeat(Animator anmtr) {
//                }
//            });
            animator.start();
        }
    }

    private void adjustScrollableView(ScrollState scrollState) {
        if (!adjustEnable) {
            return;
        }
        final Scrollable scrollable = scrollView;
        if (scrollable == null) {
            return;
        }
        int scrollY = scrollable.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            //此處為往上滑到一半的動作
            scrollToFull();
        } else if (scrollState == ScrollState.UP) {
            if (scrollViewCanScrollHeight <= scrollY) {
                scrollToFull();
            } else {
                scrollToDefault();
            }
            //hideAdFrame();
        } else if (!scrollViewOnDefault() && !scrollViewOnFull()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()

            scrollToDefault();
        }
        //Config.loge("LogoBarIs Hidden:" + adFrameIsHidden());
    }

    public int getScrollViewHeight() {
        return scrollViewHeight;
    }

    public int getScrollViewCanScrollHeight() {
        return scrollViewCanScrollHeight;
    }

    public int getScrollViewFullHeight() {
        return scrollViewFullHeight;
    }
}
