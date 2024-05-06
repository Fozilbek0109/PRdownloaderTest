package uz.coder.prdownloadertest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: ""
        when (action) {
            ACTION_SET_ON_START_OR_RESUME_LISTENER -> {}
            ACTION_SET_ON_PAUSE_LISTENER -> {}
            ACTION_SET_ON_CANCLE_LISTENER -> {}
            ACTION_SET_ON_PROGRESS_LISTENER -> {}
            ACTION_START -> {}
            ACTION_ON_EROR -> {}
        }
    }

    companion object {
        const val ACTION_SET_ON_START_OR_RESUME_LISTENER = "setOnStartOrResumeListener"
        const val ACTION_SET_ON_PAUSE_LISTENER = "setOnPauseListener"
        const val ACTION_SET_ON_CANCLE_LISTENER = "setOnCancelListener"
        const val ACTION_SET_ON_PROGRESS_LISTENER = "setOnProgressListener"
        const val ACTION_START = "action_progress_is_indeterminate"
        const val ACTION_ON_EROR = "action_btn_start_text"
    }
}