package com.example.down.download;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * <pre>
 * PackageName:  com.example.down.download
 * Description:
 * Created by :  Liu
 * date:         2017/12/27
 * </pre>
 */
public class MyDownLoadManagerPro {

    private Uri uri;
    private Context mContext;
    private DownloadManager downloadManager;
    private DownLoadReceiver downLoadReceiver;
    private DownloadChangeObserver downloadChangeObserver;
    private long downloadId;
    private final DownloadManager.Request request;
    private OnDownLoadListener mListener;

    public MyDownLoadManagerPro(Context context, Uri uri) {
        this.uri = uri;
        mContext = context;
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setMimeType("com.loan.download.file");
        request.allowScanningByMediaScanner();

    }

    @NonNull
    public MyDownLoadManagerPro setDestinationDirAndFileName(@NonNull String dir, @NonNull String fileName) {
        FileUtils.createOrExistsDir(Environment.getExternalStoragePublicDirectory(dir));
        request.setDestinationInExternalPublicDir(dir, fileName);
        return this;
    }

    public MyDownLoadManagerPro setProgressListener(OnDownLoadListener listener){
        mListener = listener;
        downloadChangeObserver = new DownloadChangeObserver(mHandler);
        mContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"),
                true, downloadChangeObserver);

        return this;
    }

    public MyDownLoadManagerPro setNotificationTitleAndDesc(String title, String desc) {
        request.setTitle(title);
        request.setDescription(desc);
        return this;
    }

    public void start() {
        downloadId = downloadManager.enqueue(request);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downLoadReceiver = new DownLoadReceiver();
        mContext.registerReceiver(downLoadReceiver, intentFilter);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int[] bytesAndStatus = getBytesAndStatus(downloadId);
            if (mListener != null) {

                float process = ((float) bytesAndStatus[0]) / (float)bytesAndStatus[1];
                mListener.onProcess(process);
            }
        }
    };

    public void cancel() {
        if (downLoadReceiver != null) {
            mContext.unregisterReceiver(downLoadReceiver);
        }

        if (downloadChangeObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(downloadChangeObserver);
        }
    }


    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[] {-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    public class DownLoadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // to do here
            Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(completeDownloadId);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 授予目录临时共享权限
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            installIntent.setDataAndType(uriForDownloadedFile, "application/vnd.android.package-archive");
            mContext.startActivity(installIntent);
        }
    }
}
