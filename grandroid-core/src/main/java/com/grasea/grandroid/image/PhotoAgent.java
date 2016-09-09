/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Rovers
 */
public class PhotoAgent {

    private Bitmap bmp;
    private File path;
    private ExifInterface exif;

    /**
     *
     * @param bmp
     */
    public PhotoAgent(Bitmap bmp) {
        this.bmp = bmp;
    }

    public PhotoAgent(Bitmap bmp, File path) {
        this.bmp = bmp;
        this.path = path;
    }

    /**
     *
     * @param bmp
     * @param exif
     */
    public PhotoAgent(Bitmap bmp, ExifInterface exif) {
        this.bmp = bmp;
        this.exif = exif;
    }

    public ExifInterface getExif() {
        return exif;
    }

    public void setExif(ExifInterface exif) {
        this.exif = exif;
    }

    /**
     *
     * @param width
     * @param height
     * @return
     */
    public PhotoAgent fixSize(int width, int height) {
        if (width / (float) height > bmp.getWidth() / (float) bmp.getHeight()) {
            //較bmp寬，裁上下
            fixWidth(width);
            Log.d("grandroid", "after fixWidth, bitmap is recycled? " + bmp.isRecycled());
            return trimHeight(height);
        } else {
            //較bmp瘦，裁左右
            fixHeight(height);
            Log.d("grandroid", "after fixWidth, bitmap is recycled? " + bmp.isRecycled());
            return trimWidth(width);
        }
    }

    /**
     *
     * @param width
     * @return
     */
    public PhotoAgent fixWidth(int width) {
        bmp = ImageUtil.resizeBitmap(bmp, width / (float) bmp.getWidth(), true);
        return this;
    }

    /**
     *
     * @param height
     * @return
     */
    public PhotoAgent fixHeight(int height) {
        bmp = ImageUtil.resizeBitmap(bmp, height / (float) bmp.getHeight(), true);
        return this;
    }

    /**
     *
     * @param width
     * @return
     */
    public PhotoAgent trimWidth(int width) {
        if (bmp.getWidth() > width) {
            bmp = ImageUtil.cut(bmp, (bmp.getWidth() - width) / 2f, 0, true);
        }
        return this;
    }

    /**
     *
     * @param height
     * @return
     */
    public PhotoAgent trimHeight(int height) {
        if (bmp.getHeight() > height) {
            bmp = ImageUtil.cut(bmp, 0, (bmp.getHeight() - height) / 2f, true);
        }
        return this;
    }

    /**
     *
     * @param cutLeft
     * @param cutTop
     * @param cutRight
     * @param cutBottom
     * @return
     */
    public PhotoAgent trimEdge(int cutLeft, int cutTop, int cutRight, int cutBottom) {
        bmp = ImageUtil.cut(bmp, cutLeft, cutTop, cutRight, cutBottom);
        return this;
    }

    public PhotoAgent rotate(int angle) {
        bmp = ImageUtil.rotateBitmap(bmp, angle);
        return this;
    }

    public PhotoAgent cutByHWRatio(float ratio) {
        float currRatio = bmp.getHeight() / (float) bmp.getWidth();
        if (currRatio > ratio) {
            int newH = Math.round(bmp.getWidth() * ratio);
            return trimHeight(newH);
        } else if (currRatio < ratio) {
            int newW = Math.round(bmp.getHeight() / ratio);
            return trimWidth(newW);
        }
        return this;
    }

    /**
     *
     * @return
     */
    public Bitmap getBitmap() {
        return bmp;
    }

    public void setBitmap(Bitmap bmp) {
        this.bmp = bmp;
    }

    /**
     *
     * @param size
     * @return
     */
    public PhotoAgent square(float size) {
        if (bmp.getWidth() > bmp.getHeight()) {
            bmp = ImageUtil.cut(ImageUtil.resizeBitmap(bmp, size / bmp.getHeight(), true));
        } else {
            bmp = ImageUtil.cut(ImageUtil.resizeBitmap(bmp, size / bmp.getWidth(), true));
        }
        return this;
    }

    public PhotoAgent round(int corner) {
        bmp = ImageUtil.round(bmp, bmp.getWidth(), bmp.getHeight(), corner);
        return this;
    }

    public PhotoAgent round(int size, int corner) {
        bmp = ImageUtil.round(bmp, size, corner);
        return this;
    }

    public PhotoAgent removeColor(int color) {
        if (!bmp.isMutable()) {
            Bitmap bmp2 = bmp.copy(Bitmap.Config.ARGB_8888, true);
            bmp.recycle();
            bmp = bmp2;
        }
        int[] allpixels = new int[bmp.getHeight() * bmp.getWidth()];

        bmp.getPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {

            if (allpixels[i] == color) {
                allpixels[i] = Color.TRANSPARENT;
            }
        }
        bmp.setPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return this;
    }

    public PhotoAgent removeColor(int red, int green, int blue) {
        if (!bmp.isMutable()) {
            Bitmap bmp2 = bmp.copy(Bitmap.Config.ARGB_8888, true);
            bmp.recycle();
            bmp = bmp2;
        }
        int[] allpixels = new int[bmp.getHeight() * bmp.getWidth()];

        bmp.getPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {
            if (Color.red(allpixels[i]) > red && Color.green(allpixels[i]) > green && Color.blue(allpixels[i]) > blue) {
                allpixels[i] = Color.TRANSPARENT;
            }
        }
        bmp.setPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return this;
    }

    public void setStoredFile(File f) {
        this.path = f;
    }

    /**
     *
     * @return
     */
    public File getStoredFile() {
        return path;
    }

    /**
     *
     * @return
     */
    public PhotoAgent sdcard() {
        path = Environment.getExternalStorageDirectory();
        return this;
    }

    /**
     *
     * @param dir
     * @return
     */
    public PhotoAgent dir(String dir) {
        path = new File(path, dir);
        if (!path.exists()) {
            path.mkdir();
        }
        return this;
    }

    /**
     *
     * @param fileName
     * @return
     */
    public File file(String fileName) {
        path = new File(path, fileName);
        return path;
    }

    public boolean saveTemp(Context context) {
        return save(new File(context.getExternalCacheDir(), "tmp" + System.currentTimeMillis()), Bitmap.CompressFormat.PNG);
    }

    /**
     *
     * @param file
     * @return
     */
    public boolean save(File file) {
        return save(file, Bitmap.CompressFormat.JPEG);
    }

    /**
     *
     * @param file
     * @param format
     * @return
     */
    public boolean save(File file, Bitmap.CompressFormat format) {
        FileOutputStream bos = null;
        try {
            bos = new FileOutputStream(file.getAbsolutePath());
            /*
             * 檔案轉換
             */
            bmp.compress(format, 100, bos);
            /*
             * 呼叫flush()方法，更新BufferStream
             */
            bos.flush();
            /*
             * 結束OutputStream
             */
            bos.close();
            path = file.getAbsoluteFile();
            return true;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return false;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                Log.e("grandroid", null, ex);
            }
        }
    }
}
