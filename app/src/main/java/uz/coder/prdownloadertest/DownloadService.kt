package uz.coder.prdownloadertest


import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

import android.os.PersistableBundle
import android.util.Log
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DownloadService : JobService() {
    private var downloadID = 0
    private var path = ""
    private val  scope = CoroutineScope(Dispatchers.IO)
    override fun onStartJob(params: JobParameters?): Boolean {
        log("sevice in work")
        scope.launch {
            val url = params?.extras?.getString(URL_PATH)?:""
            downloadFile(url,params)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("Service stop")
       return true
    }

    override fun onCreate() {
        super.onCreate()
        PRDownloader.initialize(this)
    }

    private fun downloadFile(url: String,params: JobParameters?) {
        if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
            PRDownloader.resume(downloadID)
        }
//        URLUtil.guessFileName(url, null, ".mp4")
        val fileName = downloadSaveVideo(url)
        path = Utils.getRootDirPath(this)
        downloadID = PRDownloader.download(url, path, fileName)
            .build()
            .setOnStartOrResumeListener {
                log("OnStartOrResumeListener")
                val broadC = Intent(BroadCast.ACTION_SET_ON_START_OR_RESUME_LISTENER)
                sendBroadcast(broadC)
            }
            .setOnPauseListener { // setting the text of start button to resume
                log("pause down")
                val broadC = Intent(BroadCast.ACTION_SET_ON_PAUSE_LISTENER)
                sendBroadcast(broadC)
            }
            .setOnCancelListener { // resetting the downloadId when
                log("cancle down")
                downloadID = 0
                val broadC = Intent(BroadCast.ACTION_SET_ON_CANCLE_LISTENER)
                sendBroadcast(broadC)
                jobFinished(params,false)
            }
            .setOnProgressListener { progress -> // getting the progress of download
                log("progress-total ${progress.totalBytes}")
               log("progress-current ${progress.currentBytes}")
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
                val broadC = Intent(BroadCast.ACTION_SET_ON_PROGRESS_LISTENER).apply {
                    putExtra(PAGE_TXT_CURRENT_BYTE,progress.currentBytes)
                    putExtra(PAGE_TXT_TOTAL_BYTE,progress.totalBytes)
                    putExtra(PAGE_PROGRESS, progressPer.toInt())
                }
                sendBroadcast(broadC)
            }.start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    log("download complete")
                    val broadC = Intent(BroadCast.ACTION_START).apply {
                        putExtra(URL_PATH, path)
                    }
                    sendBroadcast(broadC)
                    jobFinished(params, false)
                }

                override fun onError(error: Error) {
                    log("erorr: connection: ${error.isConnectionError} service-${error.isServerError}")
                    downloadID = 0
                    val broadC = Intent(BroadCast.ACTION_ON_EROR)
                    sendBroadcast(broadC)
                    jobFinished(params, false)
                }
            })
    }

    private fun downloadSaveVideo(url: String): String {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
        val currentDay = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        val currentHour = SimpleDateFormat("hh", Locale.getDefault()).format(Date())
        val currentMinute = SimpleDateFormat("mm", Locale.getDefault()).format(Date())
        val currentSekunte = SimpleDateFormat("ss", Locale.getDefault()).format(Date())
        return "${currentYear}_${currentMonth}_${currentDay}_${currentHour}_${currentMinute}_${currentSekunte}.mp4"
    }

    override fun onDestroy() {
        log("onDestroy")
        PRDownloader.cancel(downloadID)
        super.onDestroy()
    }
    fun log(s:String){
        Log.d(TAG, "MyService: $s ")
    }
    companion object {
        const val PAGE_TXT_CURRENT_BYTE = "page_txt_current"
        const val PAGE_TXT_TOTAL_BYTE = "page_txt_total"
        const val PAGE_PROGRESS = "page_progress"
        const val URL_PATH = "url_path"
        private const val TAG = "DownloadService"

        fun getBundle(url: String):PersistableBundle{
            return PersistableBundle().apply {
                putString(URL_PATH,url)
            }
        }
    }
}