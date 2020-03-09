package edu.singaporetech.codedispensary

import android.app.NotificationManager
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.coroutines.cancel
import java.util.concurrent.TimeUnit
import edu.singaporetech.codedispensary.NotificationServices as ns

class CodeDispensaryView : AppCompatActivity() {
    private lateinit var notiMgr : NotificationManager
    private lateinit var mService : CodeDispensaryService
    private var mBound = false
    val connection = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CodeDispensaryService.LocalBinder
            mService = binder.getService()
            mBound = true

        }

    }

    override fun onStart(){
        super.onStart()
        Log.d("MainActivity","On Start activity")
        Intent(this, CodeDispensaryService::class.java).also{
                intent -> bindService(intent,connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop(){
        super.onStop()
        Log.d("MainActivity","On Stop activity")

        unbindService(connection)
        mBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notiMgr = ns.getNotiMgr(this) //Getting the notification manager
        ns.createNotificationChannel(notiMgr,
            utils.NOTIFICATION_CHANNEL_ID,
            utils.NOTIFICATION_CHANNEL_NAME,4) //Creating the notification channel

        // - Initializing XML elements
        val btnShutdown = findViewById<Button>(R.id.shutdownBtn)
        val btnPrint = findViewById<Button>(R.id.printBtn)
        val textView = findViewById<TextView>(R.id.textViewPrint)

        // - Starting the code dispensary service
        startService(Intent(this,CodeDispensaryService::class.java))

        // - Work manager
        val workMgr = WorkManager.getInstance(application)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // - Temperature worker
        val tempWorker = PeriodicWorkRequestBuilder<TempWorker>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workMgr.enqueue(tempWorker)

        // - Listeners
        btnPrint.setOnClickListener {
            // - One time work request for running the highlySecretive method
            val myWorker = OneTimeWorkRequestBuilder<MyWorker>()
                .setConstraints(constraints)
                .setInputData(createInputData(mService.getCurString()))
                .build()
            workMgr.enqueue(myWorker)
            workMgr.getWorkInfoByIdLiveData(myWorker.id).observe(this, Observer { wi->
                if(wi!=null && wi.state.isFinished){
                    Log.d("main","work finished.")
                    textView.text = "${ wi.outputData.getString(utils.INPUTDATA_GENERATEDCODE)}"
                }
            })
        }

        btnShutdown.setOnClickListener{
            mService.cancel()
            unbindService(connection)
            mBound=false
            System.exit(0)
        }

    }

    /**
     * This function is to build a data to be passed into the worker
     * @param str String should be the 8 char generated code
     * @return Data object that is built. Inside contains a string
     */
    fun createInputData(str:String): Data {
        return Data.Builder()
            .putString(utils.INPUTDATA_GENERATEDCODE, str)
            .build()
    }


}
