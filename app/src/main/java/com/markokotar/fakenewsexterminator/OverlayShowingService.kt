package com.markokotar.fakenewsexterminator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.markokotar.fakenewsexterminator.json.HoaxResult
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.reflect.typeOf


class OverlayShowingService : Service(), OnTouchListener, View.OnClickListener {
    private var topLeftView: TextView? = null
    private var overlayedButton: ImageView? = null
    private var offsetX = 0f
    private var offsetY = 0f
    private var originalXPos = 0
    private var originalYPos = 0
    private var moving = false
    private var wm: WindowManager? = null
    private val timer = Timer()
    private var error : String = ""
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val handler = Handler(Looper.getMainLooper())

            val client = OkHttpClient()
            var url = intent?.data as Uri;
            var parts = url.toString().split(Regex("\\W"))
            parts = parts.sortedWith(compareBy { it.length }).reversed().take(5).filter { it.length > 3 }.map{ "$it~" }

            var query = parts.joinToString(
                separator = "%20AND%20",
                prefix = "",
                postfix = "",
                limit = 5,
                truncated = ""
            )
//            handler.post {
//                showFake()
//                topLeftView!!.text = query;
//            }
            val request: Request = Request.Builder()
                .url("https://api-hoaxy.p.rapidapi.com/articles?sort_by=relevant&use_lucene_syntax=true&query=$query")
                .get()
                .addHeader("x-rapidapi-host", "api-hoaxy.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "94e23c8b64msha1c13f625da13e6p17ccbfjsn5fc8a04cd572")
                .build()
            client.newCall(request).enqueue(object : Callback {



                override fun onFailure(call: Call, e: IOException) {
                    handler.post {
                        showFake()
                        topLeftView!!.text = "Error $e"
                    }
                }

                override fun onResponse(call: Call, response: Response) {

                    if(response.code() == 200) {
                        val mapper = jacksonObjectMapper()
                        var articles = mapper.readValue<HoaxResult>(response?.body()?.string()!!)
                        if (articles.num_of_entries != null && articles.num_of_entries!! > 0) {
                            handler.post {
                                showFake()
                            }
                        } else
                        {
                            handler.post {
                                stopSelf()
                            }
                        }
                    }
                    else
                    {
                        handler.post {
                            showFake()
                            topLeftView!!.text = "Error " +response?.body()?.toString()
                        }
                    }
                }
            })
        }
        catch (e : Exception){
            error =
                e.toString() +

                        if(e.message == null) {"None"} else {e.message!!}


            ;
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()


    }

    private fun showFake() {
        val numberOfSeconds = 10
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.SECOND, numberOfSeconds)
        timer.schedule(object : TimerTask() {
            override fun run() {
                mainTask()
            }
        }, calendar.time);

        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        overlayedButton = ImageView(this)
        // overlayedButton?.text = "Overlay button"
        overlayedButton?.setImageDrawable(resources.getDrawable(R.drawable.fake))
        //overlayedButton?.setOnTouchListener(this)
        //overlayedButton?.alpha = 0.0f
        overlayedButton?.setBackgroundColor(resources.getColor(android.R.color.transparent))
        var layoutFlag: Int;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = LayoutParams.TYPE_PHONE;
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
        topLeftView = TextView(this)
        topLeftView!!.text = error;
        topLeftView!!.setBackgroundColor(resources.getColor(android.R.color.white))
        topLeftView!!.setTextColor(resources.getColor(android.R.color.black))

        val topLeftParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            layoutFlag,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        topLeftParams.gravity = Gravity.LEFT or Gravity.TOP

        //        topLeftParams.x = 0
        //
        //        topLeftParams.y = 0
        //        topLeftParams.width = 0
        //        topLeftParams.height = 0
        wm!!.addView(topLeftView, topLeftParams)
    }

    private fun mainTask(){

        stopSelf();
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayedButton != null) {
            wm!!.removeView(overlayedButton)
            wm!!.removeView(topLeftView)
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