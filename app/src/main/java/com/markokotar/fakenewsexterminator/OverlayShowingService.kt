package com.markokotar.fakenewsexterminator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


class OverlayShowingService : Service(), OnTouchListener, View.OnClickListener {
    private var topLeftView: View? = null
    private var overlayedButton: ImageView? = null
    private var offsetX = 0f
    private var offsetY = 0f
    private var originalXPos = 0
    private var originalYPos = 0
    private var moving = false
    private var wm: WindowManager? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        overlayedButton = ImageView(this)
        // overlayedButton?.text = "Overlay button"
        overlayedButton?.setImageDrawable(resources.getDrawable(R.drawable.fake))
        //overlayedButton?.setOnTouchListener(this)
        //overlayedButton?.alpha = 0.0f
        overlayedButton?.setBackgroundColor(resources.getColor(android.R.color.transparent))
        var layoutFlag : Int;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //overlayedButton?.setOnClickListener(this)
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            layoutFlag,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL
        params.x = 0
        params.y = 0
        wm!!.addView(overlayedButton, params)
//        topLeftView = View(this)
//        val topLeftParams = LayoutParams(
//            LayoutParams.WRAP_CONTENT,
//            LayoutParams.WRAP_CONTENT,
//            layoutFlag,
//            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL,
//            PixelFormat.TRANSLUCENT
//        )
//        topLeftParams.gravity = Gravity.LEFT or Gravity.TOP
//        topLeftParams.x = 0
//        topLeftParams.y = 0
//        topLeftParams.width = 0
//        topLeftParams.height = 0
//        wm!!.addView(topLeftView, topLeftParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayedButton != null) {
            wm!!.removeView(overlayedButton)
   //         wm!!.removeView(topLeftView)
            overlayedButton = null
            topLeftView = null
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {

//        if (event.action == MotionEvent.ACTION_DOWN) {
//            val x = event.rawX
//            val y = event.rawY
//            moving = false
//            val location = IntArray(2)
//            overlayedButton?.getLocationOnScreen(location)
//            originalXPos = location[0]
//            originalYPos = location[1]
//            offsetX = originalXPos - x
//            offsetY = originalYPos - y
//        } else if (event.action == MotionEvent.ACTION_MOVE) {
//            val topLeftLocationOnScreen = IntArray(2)
//            topLeftView?.getLocationOnScreen(topLeftLocationOnScreen)
//            println("topLeftY=" + topLeftLocationOnScreen[1])
//            println("originalY=$originalYPos")
//            val x = event.rawX
//            val y = event.rawY
//            val params: LayoutParams = overlayedButton?.layoutParams as LayoutParams
//            val newX = (offsetX + x).toInt()
//            val newY = (offsetY + y).toInt()
//            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
//                return false
//            }
//            params.x = newX - topLeftLocationOnScreen[0]
//            params.y = newY - topLeftLocationOnScreen[1]
//            wm!!.updateViewLayout(overlayedButton, params)
//            moving = true
//        } else if (event.action == MotionEvent.ACTION_UP) {
//            if (moving) {
//                return true
//            }
//        }
        return false
    }

    override fun onClick(v: View?) {
        Toast.makeText(this, "Overlay button click event", Toast.LENGTH_SHORT).show()
    }
}