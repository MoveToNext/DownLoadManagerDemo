package com.example.down.download;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * <pre>
 * PackageName:  com.dai159.loan.download
 * Description:
 * Created by :  Liu
 * date:         2017/11/16 下午2:42
 * </pre>
 */
public class DownloadChangeObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    private Handler handler;
    public DownloadChangeObserver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        updateView();
    }

    public void updateView() {
        handler.sendMessage(handler.obtainMessage(0));
    }

//    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
//    Runnable command = new Runnable() {
//
//        @Override
//        public void run() {
//            updateView();
//        }
//    };
//    scheduledExecutorService.scheduleAtFixedRate(command, 0, 3, TimeUnit.SECONDS);

}
