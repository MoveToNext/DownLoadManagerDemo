# DownLoadManagerDemo
```
downLoadManagerPro = MyDownLoadManagerPro(this, Uri.parse(s))
        downLoadManagerPro.setDestinationDirAndFileName("test", "down.apk")
                .setNotificationTitleAndDesc("测试apk","下载中。。。")
                .setProgressListener({ process: Float -> Log.d("process", process.toString())})
                .start()
                
```

Android下载管理DownloadManager介绍和使用封装 http://blog.csdn.net/yeziliuyang/article/details/78675344
