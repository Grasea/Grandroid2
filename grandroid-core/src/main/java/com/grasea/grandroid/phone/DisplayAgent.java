/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.phone;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

/**
 *
 * @author Rovers
 */
public class DisplayAgent {

    /**
     *
     */
    protected int width;
    /**
     *
     */
    protected int height;
    /**
     *
     */
    protected DisplayMetrics metrics;
    /**
     *
     */
    protected boolean statusBar;
    /**
     *
     */
    protected boolean titleBar;
    /**
     *
     */
    protected boolean compatibleMode;
    /**
     *
     */
    protected int softSpace;
    /**
     *
     */
    protected int rigidSpace;
    /**
     *
     */
    protected int excluseSpace;
    /**
     *
     */
    protected int cumulatePadding;
    /**
     *
     */
    protected int orientation;
    /**
     *
     */
    protected int density;

    /**
     * default setting: statusBar=true, titleBar=false, compatibleMode=true;
     *
     * @param activity
     */
    public DisplayAgent(Activity activity) {
        this(activity, true, false, true);
    }

    /**
     *
     * @param frame
     * @param statusBar
     * @param titleBar
     * @param compatibleMode
     */
    public DisplayAgent(Activity frame, boolean statusBar, boolean titleBar, boolean compatibleMode) {
        this.statusBar = statusBar;
        this.titleBar = titleBar;
        this.compatibleMode = compatibleMode;
        metrics = new DisplayMetrics();

        Display display = frame.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        orientation = Configuration.ORIENTATION_UNDEFINED;
        if (display.getWidth() == display.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (display.getWidth() < display.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
    }

    /**
     *
     * @return
     */
    public DisplayMetrics getDisplayMetrics() {
        return metrics;
    }

    /**
     *
     * @return
     */
    public int getScreenOrientation() {
        return orientation;
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     *
     * @return
     */
    public int getRealHeight() {
        return Math.round(metrics.heightPixels * metrics.density);
    }

    /**
     *
     * @return
     */
    public int getRealWidth() {
        return Math.round(metrics.widthPixels * metrics.density);
    }

    /**
     *
     * @return @throws Exception
     */
    public int getDensityDPI() throws Exception {
        return metrics.densityDpi;
    }

    /**
     * 取得NavigationBar的高度
     *
     * @param c
     * @return
     */
    public static int getNavigationBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    private static boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     *
     * @param size
     * @return
     */
    public int[] layoutWidth(int[] size) {
        int totalNeed = 0;
        int availWidth = width;
        int takeAllSpace = -1;
        for (int i = 0; i < size.length; i++) {
            if (size[i] > 0) {
                availWidth -= size[i];
            } else if (size[i] < 0) {
                totalNeed += size[i];
            } else {
                takeAllSpace = i;
            }
        }
        int[] result = new int[size.length];

        for (int i = 0; i < size.length; i++) {
            if (size[i] > 0) {
                result[i] = size[i];
            } else if (size[i] < 0) {
                result[i] = takeAllSpace < 0 ? (int) (availWidth * (size[i] / (float) totalNeed)) : -size[i];
            } else {
                result[i] = Math.max(0, availWidth + totalNeed);
            }
        }
        return result;
    }

    /**
     *
     * @return
     */
    public int getExcluseSpace() {
        return excluseSpace;
    }

    /**
     *
     * @param excluseSpace
     */
    public void setExcluseSpace(int excluseSpace) {
        this.excluseSpace = excluseSpace;
    }

    /**
     *
     * @param fullHeight
     * @return
     */
    public int predictAvailableSpaceHeight(int fullHeight) {
        int contentHeight = 0;
        contentHeight = fullHeight;
        if (statusBar) {
            if (compatibleMode) {
                contentHeight -= 25;
            } else {
                if (fullHeight <= 360) {
                    contentHeight -= 19;
                } else if (fullHeight <= 480) {
                    contentHeight -= 25;
                } else {
                    contentHeight -= 38;
                }
            }
        }
        if (titleBar) {
            //未實測
            contentHeight -= 38;
        }
        return contentHeight;
    }

    /**
     *
     * @param paddingWhenY800
     * @return
     */
    public int correctPadding(int paddingWhenY800) {
        return correctPadding(paddingWhenY800, 0);
    }

    /**
     *
     * @param paddingWhenY800
     * @param extraMinusForSmallScreen
     * @return
     */
    public int correctPadding(int paddingWhenY800, int extraMinusForSmallScreen) {
        if (height > 480) {
            extraMinusForSmallScreen = 0;
        }
        if (compatibleMode) {
            return Math.max(Math.round(paddingWhenY800 * ((float) predictAvailableSpaceHeight(height) - excluseSpace) / (predictAvailableSpaceHeight(533) - excluseSpace)) - extraMinusForSmallScreen, 0);
        } else {
            if (height == 800) {
                return paddingWhenY800;
            } else {
                float paddingScale = (480f / width) * ((float) predictAvailableSpaceHeight(height) - excluseSpace) / (predictAvailableSpaceHeight(800) - excluseSpace);
                return Math.max(Math.round(paddingWhenY800 * paddingScale), 0);
            }
        }
    }

    /**
     *
     * @param paddingWhenY800
     * @param deltaRigid
     * @return
     */
    public int fixPadding(int paddingWhenY800, int deltaRigid) {
        return correctPadding(paddingWhenY800, deltaRigid - correctPadding(deltaRigid));
    }
}
