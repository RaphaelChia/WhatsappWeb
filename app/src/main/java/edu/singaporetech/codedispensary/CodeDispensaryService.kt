package edu.singaporetech.codedispensary

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import edu.singaporetech.codedispensary.utils.Companion.charPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import edu.singaporetech.codedispensary.NotificationServices as ns

/**
 * Code dispensary that doesn't need UI to run.
 * Main roles:
 * - generate 8-char codes at regular intervals
 * - provide functions for "others" to get their desired outputs
 */
class CodeDispensaryService : Service() , CoroutineScope by MainScope(){
    private val binder = LocalBinder()
    private var currentString :String = ""
    lateinit var notiMgr : NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notiMgr = ns.getNotiMgr(this)
        launch {
            while(true){
                dispenseCode()
            }
        }
        return START_STICKY
    }


    /**
     * This function will have a delay of 1687ms (declared in utils.kt)
     * and will generate the random string using generateRand()
     * The notification will be fired after the generateRand() function had run.
     * @see generateRand()
     */
    suspend fun dispenseCode() {
        delay(utils.GENERATOR_DELAY)
        this.currentString = generateRand()
        val (noti,id) = ns.buildNotification(
            this,
            CodeDispensaryView::class.java,
            utils.NOTIFICATION_CHANNEL_ID,
            utils.NOTIFICATION_TITLE,
            "Current Code: ${this.currentString}"
        )
        notiMgr.notify(utils.NOTIFICATION_ID_CODE,noti)
    }

    /**
     * This is a getter to get the currently generated Number
     * @return String of 8 char code
     * Currently used by CodeDispensaryView
     * @See CodeDispensaryView
     */
    fun getCurString():String{
        return this.currentString
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        /**
         * Trade secret method, DO NOT EDIT
         * - we will be very upset if you meddled with our hard work below
         * - note that it is not hard for our tests to determine if you edited this function
         *
         * @param code nuff said
         * @return String to be output in the view
         */
        fun highlySecretiveYetEffectiveMsgGenerator(code: String): String {
            val msgs = listOf(
                "Code for life.",
                "Do good, write code.",
                "Talk is cheap, show your code.",
                "Take a break, have a code",
                "May the code be with you"
            )

            val digits = IntArray(code.length)
            for (i in code.indices) {
                digits[i] = Character.getNumericValue(code[i])
            }

            var seed = 8F
            for (i in 0 until 8888888) {
                seed += (digits[Random.nextInt(code.length)] *
                        digits[Random.nextInt(code.length)]).toFloat()

                for (j in digits.size-1 downTo 0) {
                    if (digits[j] > 0) seed /= digits[j]
                }
            }
            seed *= 888888888F // whut...

            return "\"${msgs[Random(seed.toInt()).nextInt(msgs.size)]}\"\n$code"
        }
    }

    inner class LocalBinder(): Binder(){
        fun getService(): CodeDispensaryService {
            return this@CodeDispensaryService

        }
    }

    /**
     * This function generates a 8 char long code. The code generated is chosen from a
     * charPool at utils.kt.
     * @return String of randomly generated ID
     * @see utils
     */
    fun generateRand():String{
        val currId = (1..8)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        return currId
    }


}
