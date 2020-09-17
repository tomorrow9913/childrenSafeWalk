package kr.co.woobi.tomorrow99.safewalk.tool.util

import android.content.Context
import com.sungbin.sungbintool.util.Logger
import kr.co.woobi.tomorrow99.safewalk.view.activity.ExceptionActivity
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

object ExceptionUtil {

    fun except(exception: Exception, context: Context) {
        Logger.e(exception)
        val message = exception.localizedMessage
        val line = exception.stackTrace[0].lineNumber
        val content = "$message #$line"
        context.startActivity(context.intentFor<ExceptionActivity>("message" to content).newTask())
    }

}