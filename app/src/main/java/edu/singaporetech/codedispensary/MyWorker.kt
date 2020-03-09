package edu.singaporetech.codedispensary

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(c: Context, param: WorkerParameters): Worker(c,param){
    override fun doWork(): Result {
        val currentString = inputData.getString(utils.INPUTDATA_GENERATEDCODE)
        val returnString  = CodeDispensaryService.highlySecretiveYetEffectiveMsgGenerator(currentString!!)
        return Result.success(createInputData(returnString))
    }

    /**
     * This function creates a data object to be passed back to activity
     * @param str This string should be the result of highlySecretive method
     * @return Data that was built
     */
    fun createInputData(str:String): Data {
        return Data.Builder()
            .putString(utils.INPUTDATA_GENERATEDCODE, str)
            .build()
    }

}