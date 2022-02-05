package com.noqapp.android.client.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import com.noqapp.android.client.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageUtils {

    public static Drawable getThumbPlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.thumb_placeholder);
    }

    public static Drawable getBannerPlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.header_placeholder);
    }

    public static Drawable getThumbErrorPlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.thumb_error);
    }

    public static Drawable getBannerErrorPlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.header_error);
    }

    public static Drawable getProfilePlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.profile_theme);
    }

    public static Drawable getProfileErrorPlaceholder(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.profile_theme);
    }

    public static int getBannerPlaceholder() {
        return R.drawable.header_placeholder;
    }

    public static int getThumbPlaceholder() {
        return R.drawable.thumb_placeholder;
    }

    public static int getProfilePlaceholder() {
        return R.drawable.profile_theme;
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = context.getContentResolver();
                    Uri picCollection = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    ContentValues picDetail = new ContentValues();
                    picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
                    picDetail.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    picDetail.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + UUID.randomUUID().toString());
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 1);
                    Uri finaluri = resolver.insert(picCollection, picDetail);
                    picDetail.clear();
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(picCollection, picDetail, null, null);
                    return finaluri;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

            } else {
                return null;
            }
        }
    }


    public static Bitmap loadFromUri(Uri photoUri, Context context) {
        Bitmap image=null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                // on newer versions of Android, use the new decodeBitmap method
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }


}
