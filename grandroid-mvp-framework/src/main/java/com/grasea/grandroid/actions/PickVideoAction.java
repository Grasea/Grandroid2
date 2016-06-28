/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * 使用此Action來獲得影片
 * @author Alan
 */
public abstract class PickVideoAction extends PendingAction {

    /**
     *
     * @param context
     */
    public PickVideoAction(Context context) {
        super(context, 65013);
    }

    public abstract void onVideoSelected(File f);

    @Override
    public Intent getActionIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/mp4"); // ?mp4 3gp android? 
        return intent;
    }

    @Override
    public boolean callback(boolean result, Intent data) {
        if (result) {
            Uri selectedImage = data.getData();
            File f = getFileFromUri(context, selectedImage, true);
////                        imageStream = context.getContentResolver().openInputStream(selectedImage);
////                        photo = BitmapFactory.decodeStream(imageStream);
//            //photo = ImageUtil.sampling(context, selectedImage);
//            Log.d("grandroid", "android.os.Build.BRAND=" + android.os.Build.BRAND + ", android.os.Build.DEVICE=" + android.os.Build.DEVICE);
//
//            Cursor cursor = context.getContentResolver().query(selectedImage,
//                    new String[]{MediaStore.Video.Media.DATA},
//                    null, null, null);
//            String filepath = null;
//            try {
//                if (cursor.moveToFirst()) {
//                    filepath = cursor.getString(0);
//
//                } else {
//                }
//            } finally {
//                cursor.close();
//            }
            onVideoSelected(f);
        }
        return true;
    }

    @SuppressLint("NewApi")
    public static File getFileFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
        if (uri == null) {
            return null;
        }
        // 判斷是否為Android 4.4之後的版本
        final boolean after44 = Build.VERSION.SDK_INT >= 19;
        if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            final String authority = uri.getAuthority();
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents".equals(authority)) {
                // 外部儲存空間
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                if ("primary".equals(type)) {
                    String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
                    return createFileObjFromPath(path, mustCanRead);
                }
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // 下載目錄
                final String docId = DocumentsContract.getDocumentId(uri);
                final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                String path = queryAbsolutePath(context, downloadUri);
                return createFileObjFromPath(path, mustCanRead);
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // 圖片、影音檔案
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                Uri mediaUri = null;
                if ("image".equals(type)) {
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    return null;
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
                String path = queryAbsolutePath(context, mediaUri);
                return createFileObjFromPath(path, mustCanRead);
            }
        } else {
            // 如果是一般的URI
            final String scheme = uri.getScheme();
            String path = null;
            if ("content".equals(scheme)) {
                // 內容URI
                path = queryAbsolutePath(context, uri);
            } else if ("file".equals(scheme)) {
                // 檔案URI
                path = uri.getPath();
            }
            return createFileObjFromPath(path, mustCanRead);
        }
        return null;
    }

    public static File createFileObjFromPath(final String path, final boolean mustCanRead) {
        if (path != null) {
            try {
                File file = new File(path);
                if (mustCanRead) {
                    file.setReadable(true);
                    if (!file.canRead()) {
                        return null;
                    }
                }
                return file.getAbsoluteFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String queryAbsolutePath(final Context context, final Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
