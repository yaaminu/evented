package com.evented.utils;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

;


/**
 * @author Null-Pointer on 6/16/2015.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class FileUtils {
    public static final String TAG = FileUtils.class.getSimpleName();

    public static String resolveContentUriToFilePath(String uri) {
        return resolveContentUriToFilePath(uri, false);
    }

    public static String resolveContentUriToFilePath(String uri, boolean loadInfNotFoundLocally) {
        if ((TextUtils.isEmpty(uri))) {
            return null;
        }
        return resolveContentUriToFilePath(Uri.parse(uri), loadInfNotFoundLocally);
    }

    public static String resolveContentUriToFilePath(Uri uri) {
        return resolveContentUriToFilePath(uri, false);
    }

    public static String resolveContentUriToFilePath(Uri uri, boolean loadInfNotFoundLocally) {
        return getPathInternal(Config.getApplicationContext(), uri, loadInfNotFoundLocally);
    }

    public static String getMimeType(String path) {
        String extension = getExtension(path);
        if ("".equals(extension)) return "application/octet-stream";
        //re use extension.
        extension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (GenericUtils.isEmpty(extension)) {
            extension = "application/octet-stream";
        }
        PLog.i(TAG, "mime type of " + path + " is: " + extension);
        return extension;
    }

    public static String getExtension(String path) {
        return getExtension(path, null);
    }

    public static String getExtension(String path, String fallback) {
        String extension = FilenameUtils.getExtension(path);
        if (extension == null || extension.length() < 1) return fallback;
        return extension;
    }

    public static String sha1(String source) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        return sha1(source.getBytes());
    }

    public static String md5(String input) {
        return digest(input.getBytes(), "md5");
    }

    public static String sha1(byte[] source) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        String algorithm = "sha1";
        return digest(source, algorithm);
    }

    private static String digest(byte[] source, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            //re-use param source
            source = digest.digest(source);
            String hashString = hex(source);
            PLog.d(TAG, "sha1: " + hashString);
            return hashString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static String hex(byte[] source) {
        StringBuilder hashString = new StringBuilder(source.length * 2);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < source.length; i++) {
            hashString.append(Integer.toString((source[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hashString.toString();
    }

    private static void checkIfCancelled() throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            throw new IOException("cancelled");
        }
    }

    public static String hashFile(File source) throws IOException {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("sha1");
            digest.reset();
            byte[] hash;// digest.digest(org.apache.commons.io.FileUtils.readFileToByteArray(source));
            final byte[] buffer = new byte[4096];
            final BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(source));
            int read;
            try {
                while ((read = bIn.read(buffer, 0, 4096)) != -1) {
                    checkIfCancelled();
                    digest.update(buffer, 0, read);
                }
            } finally {
                close(bIn);
            }
            checkIfCancelled();
            hash = digest.digest();
            StringBuilder builder = new StringBuilder(hash.length * 2);
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < hash.length; i++) {
                builder.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            PLog.d(TAG, "sha1: " + builder);
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * the code below was  shamelessly adapted from paulBurke: https://github.com/ipaulPro/aFileChooser
     * licensed under the apache licence
     */

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     *                author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPathInternal(final Context context, final Uri uri, boolean loadIfNotFoundLocally) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null, loadIfNotFoundLocally);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs, loadIfNotFoundLocally);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null, loadIfNotFoundLocally);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs, boolean loadIfNotFoundLocally) {

        if (loadIfNotFoundLocally) {
            ThreadUtils.ensureNotMain();
        }
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column,
        };

        try {
            final ContentResolver contentResolver = context.getContentResolver();
            cursor = contentResolver.query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    String path = cursor.getString(column_index);

                    if (loadIfNotFoundLocally && path == null) {
                        throw new IllegalArgumentException("path == null");//just to send us to the catch clause
                    }
                    return path;
                } catch (IllegalArgumentException noSuchColumn) {
                    int columnIndex = cursor.getColumnIndex("mime_type");
                    if (columnIndex == -1) {
                        return null;
                    }
                    String mimeType = cursor.getString(columnIndex);
                    if (TextUtils.isEmpty(mimeType)) {
                        return null;
                    }
                    String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    if (TextUtils.isEmpty(ext)) {
                        return null;
                    }
                    byte[] bytes = new byte[15];
                    new SecureRandom().nextBytes(bytes);
                    String fileName = Base64.encodeToString(bytes, Base64.URL_SAFE) + "." + ext;
                    File dir
                            = Config.getAppBinFilesBaseDir();
                    File file = new File(dir, fileName);
                    try {
                        //noinspection ConstantConditions
                        org.apache.commons.io.FileUtils.copyInputStreamToFile(
                                contentResolver.openInputStream(uri), file);
                        return file.getAbsolutePath();
                    } catch (IOException e) {
                        PLog.d(TAG, "error opening stream, reason: " + e.getMessage());
                        return null;
                    }
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    public static boolean open(Context context, String filePath) {
        GenericUtils.ensureNotNull(context, (Object) filePath);

        Uri uri = filePath.startsWith("http") ? Uri.parse(filePath) : Uri.fromFile(new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getMimeType(filePath));
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
