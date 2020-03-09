package edu.singaporetech.codedispensary

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * This class is a worker to show a notification
 * to remind temperature taking
 */
class TempWorker(val c: Context, param: WorkerParameters): Worker(c,param){
    override fun doWork(): Result {
        val notiMgr = NotificationServices.getNotiMgr(c)
        val (noti,id) = NotificationServices.buildNotification(
            c,
            CodeDispensaryView::class.java,
            utils.NOTIFICATION_CHANNEL_ID,
            utils.NOTIFICATION_TITLE,
            "Take your own temperature!"
        )
        notiMgr.notify(utils.NOTIFICATION_ID_TEMP,noti)
        return Result.success()
    }

}