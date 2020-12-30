package com.linruan.carconnection

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_camera

    override fun initView() {
        if (Build.VERSION.SDK_INT >= 19) {
            val decorView: View = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            val decorView: View = window.decorView
            val option: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.setSystemUiVisibility(option)
        }
        cameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE)
        cameraView.setErrorLisenter(object : ErrorListener {
            override fun AudioPermissionError() {
                logE("")
            }

            override fun onError() {
                logE("摄像头开启失败")
            }

        })
        cameraView.setJCameraLisenter(object : JCameraListener {
            override fun recordSuccess(url: String?, firstFrame: Bitmap?) {
                logI("拍照url")
            }

            override fun captureSuccess(bitmap: Bitmap?) {
                logI("拍照url")
            }
        })
        cameraView.apply {
            setLeftClickListener { finish() }
            setRightClickListener{finish()}
        }

    }

    override fun onResume() {
        super.onResume()
        cameraView.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraView.onPause()
    }
}