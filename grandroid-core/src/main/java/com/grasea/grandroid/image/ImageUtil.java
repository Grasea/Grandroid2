/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author Rovers
 */
public class ImageUtil {

    /**
     *
     */
    public static int MAX_LENGTH = 1024;

    public static int getResourceByName(Context context, String drawableName) {
        String uri = "drawable/" + drawableName;
        int res = context.getResources().getIdentifier(uri, null, context.getPackageName());
        return res;
    }

    /**
     *
     * @param context
     * @param drawableName
     * @return
     */
    public static Drawable getDrawableByName(Context context, String drawableName) {
        int imageResource = getResourceByName(context, drawableName);
        if (imageResource != 0) {
            Drawable image = context.getResources().getDrawable(imageResource);
            return image;
        } else {
            return null;
        }
    }

    /**
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static Bitmap loadBitmap(String uri) throws Exception {
        if (uri.startsWith("http")) {
            return loadBitmap(new URL(uri));
        } else {
            return loadBitmap(new File(uri));
        }
    }

    /**
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static Bitmap loadBitmap(URL url) throws Exception {
        return loadBitmap(url, false);
    }


    public static void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[1024];
            int count = 0;
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
                count += n;
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
    }


    /**
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static Bitmap loadBitmap(URL url, boolean sampling) throws Exception {
        if (sampling) {
            return sampling(url);
        } else {
            Bitmap bitmap = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(20000);
                conn.setInstanceFollowRedirects(true);
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.setRequestProperty("Accept", "*/*");
                conn.connect();
                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);
                conn.disconnect();
                //bitmap = BitmapFactory.decodeStream(instream);
            } catch (Exception e) {
                Log.e("grandroid", "fail to load image: " + url.toString(), e);
                throw e;
            }
            return bitmap;
        }
    }

    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap loadBitmap(File file) throws FileNotFoundException {
        if (file.exists()) {
            return sampling(file);
            //return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            Log.e("grandroid", "can't load " + file.getAbsolutePath());
            throw new FileNotFoundException();
        }
    }

    public static Bitmap sampling(URL url) throws Exception {
        Bitmap bitmap = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, o);
            //is.close();
            //Find the correct scale value. It should be the power of 2.

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp <= MAX_LENGTH && height_tmp <= MAX_LENGTH) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            try {
                return BitmapFactory.decodeStream(is, null, o2);
            } catch (Exception ex) {
                return loadBitmap(url, false);
            } finally {
                is.close();
                conn.disconnect();
            }
            //bitmap = BitmapFactory.decodeStream(is);

            //bitmap = BitmapFactory.decodeStream(instream);
        } catch (Exception e) {
            Log.e("grandroid", null, e);
            throw e;
        }
    }

    /**
     *
     * @param context
     * @param uri
     * @return
     * @throws Exception
     */
    public static Bitmap sampling(Context context, Uri uri) throws Exception {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, o);
        imageStream.close();
        //Find the correct scale value. It should be the power of 2.

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= MAX_LENGTH && height_tmp <= MAX_LENGTH) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        //decode with inSampleSize
        imageStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            return BitmapFactory.decodeStream(imageStream, null, o2);
        } finally {
            imageStream.close();
        }
    }

    /**
     *
     * @param f
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap sampling(File f) throws FileNotFoundException {
        //InputStream instream
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

        //Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= MAX_LENGTH && height_tmp <= MAX_LENGTH) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    }


    /**
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path) {
        long cnt = System.currentTimeMillis();
        return saveBitmap(bitmap, path, cnt + ".jpg", true, 100);
    }

    /**
     *
     * @param bitmap
     * @param path
     * @param fileNamePrefix
     * @param fileNameSuffix
     * @param saveAsJPEG
     * @param quality
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path, String fileNamePrefix, String fileNameSuffix, boolean saveAsJPEG, int quality) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                /*
                 * 資料夾不在就先建立
                 */
                File f = new File(Environment.getExternalStorageDirectory(), path);
                if (!f.exists()) {
                    f.mkdir();
                }
                NumberFormat nf = new DecimalFormat("0000");
                for (int fileIndex = 1; fileIndex <= 9999; fileIndex++) {
                    String fileName = fileNamePrefix + nf.format(fileIndex) + fileNameSuffix;
                    File n = new File(f, fileName);
                    if (!n.exists()) {
                        return saveBitmap(bitmap, path, fileName, saveAsJPEG, quality);
                    }
                }
            } catch (Exception e) {
                Log.e("grandroid", null, e);
            }
        }
        return null;
    }

    /**
     *
     * @param bitmap
     * @param path
     * @param fileName
     * @param saveAsJPEG
     * @param quality
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path, String fileName, boolean saveAsJPEG, int quality) {
        /*
         * 儲存檔案
         */
        if (bitmap != null) {
            /*
             * 檢視SDCard是否存在
             */
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                /*
                 * SD卡不存在，顯示Toast訊息
                 */
            } else {
                try {
                    /*
                     * 資料夾不在就先建立
                     */
                    File f = new File(
                            Environment.getExternalStorageDirectory(), path);

                    if (!f.exists()) {
                        f.mkdir();
                    }

                    /*
                     * 儲存相片檔
                     */
                    File n = new File(f, fileName);
                    FileOutputStream bos
                            = new FileOutputStream(n.getAbsolutePath());
                    /*
                     * 檔案轉換
                     */
                    if (saveAsJPEG) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    } else {
                        bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
                    }
                    /*
                     * 呼叫flush()方法，更新BufferStream
                     */
                    bos.flush();
                    /*
                     * 結束OutputStream
                     */
                    bos.close();
                    return n.getAbsolutePath();
                } catch (Exception e) {
                    Log.e("grandroid", null, e);
                }
            }
        }
        return null;
    }

    public static String saveBitmap(Bitmap bitmap, File file, boolean saveAsJPEG, int quality) throws Exception {
        /*
         * 儲存檔案
         */
        if (bitmap != null) {
            /*
             * 檢視SDCard是否存在
             */
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                /*
                 * SD卡不存在，顯示Toast訊息
                 */
            } else {
                FileOutputStream bos
                        = new FileOutputStream(file.getAbsolutePath());
                /*
                 * 檔案轉換
                 */
                if (saveAsJPEG) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
                }
                /*
                 * 呼叫flush()方法，更新BufferStream
                 */
                bos.flush();
                /*
                 * 結束OutputStream
                 */
                bos.close();
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     *
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, float scale) {
        return resizeBitmap(bitmap, scale, false);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float scale, boolean recycleOldBitmap) {
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scale, scale);
        if (recycleOldBitmap) {
            int w = (int) (scale * bitmap.getWidth());
            int h = (int) (scale * bitmap.getHeight());
            Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(bitmap, matrix, new Paint());
            return newBitmap;
        } else {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return newBitmap;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        return rotateBitmap(bitmap, angle, false);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle, boolean recycleOldBitmap) {
        Matrix mtx = new Matrix();
        mtx.postRotate(angle);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, false);
        } finally {
            if (recycleOldBitmap) {
                bitmap.recycle();
            }
        }
    }

    public static Bitmap resizeBitmapMaxWidth(Bitmap bitmap, int width, boolean recycleOldBitmap) {
        if (bitmap.getWidth() > width) {
            return resizeBitmap(bitmap, width / (float) bitmap.getWidth(), recycleOldBitmap);
        } else {
            return bitmap;
        }
    }

    public static Bitmap resizeBitmapFitWidth(Bitmap bitmap, int width, boolean recycleOldBitmap) {
        return resizeBitmap(bitmap, width / (float) bitmap.getWidth(), recycleOldBitmap);
    }

    /**
     *
     * @param bitmap
     * @param width
     * @return
     */
    public static Bitmap resizeBitmapMaxWidth(Bitmap bitmap, int width) {
        if (bitmap.getWidth() > width) {
            return resizeBitmap(bitmap, width / (float) bitmap.getWidth());
        } else {
            return bitmap;
        }
    }

    public static Bitmap resizeBitmapMaxHeight(Bitmap bitmap, int height, boolean recycleOldBitmap) {
        if (bitmap.getHeight() > height) {
            return resizeBitmap(bitmap, height / (float) bitmap.getHeight(), recycleOldBitmap);
        } else {
            return bitmap;
        }
    }

    public static Bitmap resizeBitmapFitHeight(Bitmap bitmap, int height, boolean recycleOldBitmap) {
        return resizeBitmap(bitmap, height / (float) bitmap.getHeight(), recycleOldBitmap);
    }

    /**
     *
     * @param bitmap
     * @param height
     * @return
     */
    public static Bitmap resizeBitmapMaxHeight(Bitmap bitmap, int height) {
        if (bitmap.getHeight() > height) {
            return resizeBitmap(bitmap, height / (float) bitmap.getHeight());
        } else {
            return bitmap;
        }
    }

    /**
     *
     * @param bmp1
     * @param bmp2
     * @param left
     * @param top
     * @return
     */
    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2, float left, float top) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, left, top, null);
        return bmOverlay;
    }

    public static Bitmap flip(Bitmap bmp, boolean xAxis, boolean yAxis, boolean recycleOldBitmap) {
        Bitmap bmpFliped = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(bmpFliped);
        Matrix m = new Matrix();
        m.setScale(xAxis ? -1 : 1, yAxis ? -1 : 1);
        m.postTranslate(xAxis ? bmp.getWidth() : 0, yAxis ? bmp.getHeight() : 0);
        canvas.drawBitmap(bmp, m, null);
        if (recycleOldBitmap) {
            bmp.recycle();
        }
        return bmpFliped;
    }

    /**
     *
     * @param bmp
     * @return
     */
    public static Bitmap cut(Bitmap bmp) {
        return cut(bmp, false);
    }

    public static Bitmap cut(Bitmap bmp, boolean recycleOldBitmap) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w > h) {
            return cut(bmp, (w - h) / 2f, 0, recycleOldBitmap);
        } else if (h > w) {
            return cut(bmp, 0, (h - w) / 2f, recycleOldBitmap);
        } else {
            return bmp;
        }
    }

    /**
     *
     * @param bmp
     * @param cutMarginX
     * @param cutMarginY
     * @return
     */
    public static Bitmap cut(Bitmap bmp, float cutMarginX, float cutMarginY) {
        return cut(bmp, cutMarginX, cutMarginY, false);
    }

    public static Bitmap cut(Bitmap bmp, float cutMarginX, float cutMarginY, boolean recycleOldBitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth() - Math.round(2 * cutMarginX), bmp.getHeight() - Math.round(2 * cutMarginY), bmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp, -cutMarginX, -cutMarginY, null);
        if (recycleOldBitmap) {
            bmp.recycle();
        }
        Log.d("grandroid", "after cut, bitmap is recycled? " + bmOverlay.isRecycled());
        return bmOverlay;
    }

    /**
     *
     * @param bmp
     * @param cutLeft
     * @param cutTop
     * @param cutRight
     * @param cutBottom
     * @return
     */
    public static Bitmap cut(Bitmap bmp, int cutLeft, int cutTop, int cutRight, int cutBottom) {
        return cut(bmp, cutLeft, cutTop, cutRight, cutBottom, false);
    }

    public static Bitmap cut(Bitmap bmp, int cutLeft, int cutTop, int cutRight, int cutBottom, boolean recycleOldBitmap) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth() - (cutLeft + cutRight), bmp.getHeight() - (cutTop + cutBottom), bmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp, -cutLeft, -cutTop, null);
        bmp.recycle();
        return bmOverlay;
    }

    /**
     *
     * @param bitmap
     * @param edge
     * @return
     */
    public static Bitmap square(Bitmap bitmap, float edge) {
        return square(bitmap, edge, false);
    }

    public static Bitmap square(Bitmap bitmap, float edge, boolean recycleOldBitmap) {
        Matrix matrix = new Matrix();
        // resize the Bitmap
        try {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                matrix.postScale(edge / bitmap.getHeight(), edge / bitmap.getHeight());
                return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
            } else {
                matrix.postScale(edge / bitmap.getWidth(), edge / bitmap.getWidth());
                return Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
            }
        } finally {
            if (recycleOldBitmap) {
                bitmap.recycle();
            }
        }
    }

    public static Bitmap round(Bitmap bitmap) {
        return round(bitmap, 12);
    }

    public static Bitmap round(Bitmap bitmap, int roundPx) {
        return round(bitmap, bitmap.getWidth(), bitmap.getHeight(), roundPx);
    }

    public static Bitmap round(Bitmap bitmap, int edge, int roundPx) {
        return round(bitmap, edge, edge, roundPx);
    }

    public static Bitmap round(Bitmap bitmap, int width, int height, int roundPx) {
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        if (width != bitmap.getWidth()) {
            int minLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Matrix m = new Matrix();
            if (bitmap.getWidth() > bitmap.getHeight()) {
                m.setTranslate(-(bitmap.getWidth() - minLength) / 2, 0);
            } else {
                m.setTranslate(0, -(bitmap.getHeight() - minLength) / 2);
            }
            m.setScale(width / (float) minLength, width / (float) minLength);
            canvas.drawBitmap(bitmap, m, paint);
        } else {
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        bitmap.recycle();
        return output;
    }

    public static Bitmap circle(Bitmap bitmapimg) {
        int length = Math.min(bitmapimg.getWidth(), bitmapimg.getHeight());
        Bitmap output = Bitmap.createBitmap(length,
                length, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, length,
                length);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(length / 2,
                length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        bitmapimg.recycle();
        return output;
    }

    public static byte[] toBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    public static String toPngBase64String(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
    }

    public static Bitmap toGrayscale(Bitmap bmp) {
        return toGrayscale(bmp, false);
    }

    public static Bitmap toGrayscale(Bitmap bmp, boolean recycleOldBitmap) {
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmp, 0, 0, paint);
        if (recycleOldBitmap) {
            bmp.recycle();
        }
        return bmpGrayscale;
    }
}
