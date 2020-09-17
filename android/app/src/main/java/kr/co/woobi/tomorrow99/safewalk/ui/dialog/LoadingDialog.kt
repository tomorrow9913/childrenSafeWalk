package kr.co.woobi.tomorrow99.safewalk.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import kr.co.woobi.tomorrow99.safewalk.R


/**
 * Created by SungBin on 2020-09-16.
 */

class LoadingDialog(private val activity: Activity) {

    companion object {
        private lateinit var loadingDialog: LoadingDialog

        fun instance(activity: Activity): LoadingDialog {
            if (!::loadingDialog.isInitialized) {
                loadingDialog = LoadingDialog(activity)
            }
            return loadingDialog
        }
    }

    private lateinit var alert: AlertDialog

    @SuppressLint("InflateParams")
    fun show() {
        val layout = LayoutInflater.from(activity).inflate(R.layout.layout_loading_dialog, null)
        val dialog = AlertDialog.Builder(activity)
        dialog.setView(layout)
        alert = dialog.create()
        alert.run {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setWindowAnimations(R.style.DialogAnimation)
            show()
        }
    }

    fun close() {
        alert.cancel()
    }

}