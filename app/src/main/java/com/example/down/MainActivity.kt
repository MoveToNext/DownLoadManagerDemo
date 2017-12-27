package com.example.down

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.down.download.MyDownLoadManagerPro
import com.example.down.permission.PermissionCallback
import com.example.down.permission.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    lateinit var downLoadManagerPro :MyDownLoadManagerPro
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener { openDownLoad("http://down.159cai.com/androidapp/baidu/159cai.apk") }

        PermissionManager.askForPermission(this,permissions, object : PermissionCallback{
            override fun permissionRefused() {
                Toast.makeText(this@MainActivity,"请赋予文件权限",Toast.LENGTH_SHORT).show()
            }

            override fun permissionGranted() {
            }

        })
    }

    private fun openDownLoad(s: String) {
        downLoadManagerPro = MyDownLoadManagerPro(this, Uri.parse(s))
        downLoadManagerPro.setDestinationDirAndFileName("test", "down.apk")
                .setNotificationTitleAndDesc("测试apk","下载中。。。")
                .setProgressListener({ process: Float -> Log.d("process", process.toString())})
                .start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        downLoadManagerPro.cancel()
    }
}
