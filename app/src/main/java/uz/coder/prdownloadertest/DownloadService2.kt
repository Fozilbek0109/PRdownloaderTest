package uz.coder.prdownloadertest


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
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

class DownloadService2 : Service() {
    private var downloadID = 0
    private var path = ""
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        creatNotificationChanel()
        startForeground(1, createNotification().build())
        PRDownloader.initialize(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("sevice in work")
        if (intent != null && intent.getBooleanExtra("stop", false)) {
            PRDownloader.cancel(downloadID)
            stopSelf()
            log("servise s")
            return START_STICKY
        } else {
            scope.launch {
                val url = intent?.getStringExtra(URL_PATH) ?: ""
                downloadFile(url)
            }
            return START_STICKY
        }
    }

    override fun stopService(name: Intent?): Boolean {
        log("Service stop")
        return super.stopService(name)
    }

    private fun downloadFile(url: String) {
        if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
            PRDownloader.resume(downloadID)
        }
        val filNotif =URLUtil.guessFileName(url, null, null)
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
                stopSelf()
            }
            .setOnProgressListener { progress -> // getting the progress of download
                log("progress-total ${progress.totalBytes}")
                val progressPer = progress.currentBytes * 100 / progress.totalBytes
                log("progress-current ${progress.currentBytes}")
                notification.setProgress(100, progressPer.toInt(), false)
                notification.setContentText("$filNotif ${progressPer.toInt()}%")
                notificationManager.notify(1, notification.build())
                val broadC = Intent(BroadCast.ACTION_SET_ON_PROGRESS_LISTENER).apply {
                    putExtra(PAGE_TXT_CURRENT_BYTE, progress.currentBytes)
                    putExtra(PAGE_TXT_TOTAL_BYTE, progress.totalBytes)
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
                    stopSelf()
                }

                override fun onError(error: Error) {
                    log("erorr: connection: ${error.isConnectionError} service-${error.isServerError}")
                    downloadID = 0
                    val broadC = Intent(BroadCast.ACTION_ON_EROR)
                    sendBroadcast(broadC)
                    stopSelf()
                }
            })
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("id",downloadID)
        val activity = PendingIntent.getActivity(this, 30, intent, PendingIntent.FLAG_IMMUTABLE)
        activity.send()

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

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun log(s: String) {
        Log.d(TAG, "MyService: $s ")
    }

    private lateinit var notificationManager: NotificationManager
    private fun creatNotificationChanel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private lateinit var notification: NotificationCompat.Builder

    private fun createNotification(): NotificationCompat.Builder {
        notification = NotificationCompat.Builder(this, CHANEL_ID)
            .setOngoing(true)
            .setContentTitle("Download")
            .setSmallIcon(R.drawable.ic_launcher_background)

        return notification
    }


    companion object {
        private const val CHANEL_ID = "chanel_id"
        private const val CHANEL_NAME = "chanel_name"

        const val PAGE_TXT_CURRENT_BYTE = "page_txt_current"
        const val PAGE_TXT_TOTAL_BYTE = "page_txt_total"
        const val PAGE_PROGRESS = "page_progress"
        const val URL_PATH = "url_path"
        private const val TAG = "DownloadService"

        fun newIntent(context: Context, url: String): Intent {
            return Intent(context, DownloadService2::class.java).apply {
                putExtra(URL_PATH, url)
            }
        }
    }
}