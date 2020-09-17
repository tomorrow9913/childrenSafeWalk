package kr.co.woobi.tomorrow99.safewalk.tool.util


/**
 * Created by SungBin on 2020-09-17.
 */

object ColorUtil {

    val randomColor: Int get() = (Math.random() * 16777215).toInt() or (0xFF shl 24)

}