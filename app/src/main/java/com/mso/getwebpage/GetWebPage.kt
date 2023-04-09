package com.mso.getwebpage

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

// used for interacting with user interface
// used for passing data
// used for connectivity
class GetWebPage : Activity() {
    lateinit var h: Handler
    private var tView: TextView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val eText = findViewById<View>(R.id.address) as EditText
        tView = findViewById<View>(R.id.pagetext) as TextView
        h = Handler()

        /*{
            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                switch (msg.what) {
                    case 0:
                        tView.append((String) msg.obj);
                        break;
                }
                super.handleMessage(msg);
            }
        };*/

        if (!ckeckPermissions()) requestPermissions()
        val button = findViewById<View>(R.id.ButtonGo) as Button
        button.setOnClickListener {
            try {
                tView!!.text = ""
                val tr: Thread = object : Thread() {
                    override fun run() {
                        try {
                            // Perform action on click
                            val url = URL(eText.text.toString())
                            val conn = url.openConnection()
                            conn.connect()
                            // Get the response
                            val rd =
                                BufferedReader(InputStreamReader(conn.getInputStream()))
                            var line: String?
                            while (rd.readLine().also { line = it } != null) {
                                h.post(updateUIThread(line))
                            }
                        } catch (e: MalformedURLException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        } catch (e: IOException) {
                            Log.i(
                                "GetWebPage",
                                "downloading text: IOException"
                            ) // Log and trace
                            e.printStackTrace()
                        }
                    }
                }
                tr.start()
            } catch (e: Exception) {
                Log.i("GetWebPage", "Exception")
            }
        }
    }

    internal inner class updateUIThread(private val msg: String?) : Runnable {
        override fun run() {
            tView!!.append(msg)
        }
    }

    private fun ckeckPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.INTERNET
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this@GetWebPage,
            arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET),
            0
        )
    }
}
