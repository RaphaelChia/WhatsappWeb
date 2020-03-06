package edu.singaporetech.codedispensary

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlin.random.Random

/**
 * Code dispensary that doesn't need UI to run.
 * Main roles:
 * - generate 8-char codes at regular intervals
 * - provide functions for "others" to generate
 */
class CodeDispensaryService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        TODO("what you need")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("binding code lah, what else...")
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
}
