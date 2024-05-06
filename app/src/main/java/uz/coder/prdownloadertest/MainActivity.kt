package uz.coder.prdownloadertest

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.downloader.PRDownloader
import com.downloader.Status
import uz.coder.prdownloadertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getBroadCastMy()
        val intExtra = intent.getIntExtra("id", 0)
        binding.btnDownload.setOnClickListener {
            if (binding.urlEtText.text.toString().isNotEmpty()) {
                binding.detailsBox.visibility = View.VISIBLE
            }
        }
        binding.btnStart.setOnClickListener {
            if (Status.RUNNING == PRDownloader.getStatus(intExtra)) {
                PRDownloader.pause(intExtra)
                return@setOnClickListener
            }
            binding.btnStart.isEnabled = false
            if (Status.PAUSED == PRDownloader.getStatus(intExtra)) {
                PRDownloader.resume(intExtra)
                return@setOnClickListener
            }
            val url = binding.urlEtText.text.toString().trim { it <= ' ' }
            ContextCompat.startForegroundService(this, DownloadService2.newIntent(this, url))
        }

        binding.btnStop.setOnClickListener {
            val apply = Intent(this, DownloadService2::class.java).apply {
                putExtra("stop", true)
            }
            val service =
                PendingIntent.getService(this, 50, apply, PendingIntent.FLAG_IMMUTABLE)
            service.send()
        }


    }

    fun getBroadCastMy() {
        val brodcastMy = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action ?: ""
                when (action) {
                    BroadCast.ACTION_SET_ON_START_OR_RESUME_LISTENER -> {
                        binding.progressHorizontal.isIndeterminate = false
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "Pause"
                        binding.btnStop.isEnabled = true
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading started",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    BroadCast.ACTION_SET_ON_PAUSE_LISTENER -> {
                        binding.btnStart.text = "Resume"
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading Paused",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    BroadCast.ACTION_SET_ON_CANCLE_LISTENER -> {
                        binding.btnStart.text = "Start"
                        binding.btnStop.isEnabled = false
                        binding.progressHorizontal.progress = 0
                        binding.downloadingPercentage.text = ""
                        binding.progressHorizontal.isIndeterminate = false
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading Cancelled",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }

                    BroadCast.ACTION_SET_ON_PROGRESS_LISTENER -> {
                        val extra = intent?.getIntExtra(DownloadService.PAGE_PROGRESS, 0) ?: 0
                        val prs_txt_total =
                            intent?.getLongExtra(DownloadService.PAGE_TXT_TOTAL_BYTE, 0) ?: 0
                        val prs_txt_current =
                            intent?.getLongExtra(DownloadService.PAGE_TXT_CURRENT_BYTE, 0) ?: 0
                        binding.progressHorizontal.setProgress(extra, true)
                        binding.downloadingPercentage.text =
                            Utils.getProgressDisplayLine(prs_txt_current, prs_txt_total)
                        binding.progressHorizontal.isIndeterminate = false
                    }

                    BroadCast.ACTION_START -> {
                        val s = intent?.getStringExtra(DownloadService.URL_PATH) ?: ""
                        binding.btnStart.isEnabled = false
                        binding.btnStop.isEnabled = false
                        binding.btnStart.text = "Completed"
                        binding.txtUrl.text = "File stored at : $s"
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading Completed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    BroadCast.ACTION_ON_EROR -> {
                        binding.btnStart.text = "Start"
                        binding.downloadingPercentage.text = "0"
                        binding.progressHorizontal.progress = 0
                        binding.btnStart.isEnabled = true
                        binding.btnStop.isEnabled = false
                        binding.progressHorizontal.isIndeterminate = false
                        Toast.makeText(this@MainActivity, "Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }
        val intentFilter = IntentFilter().apply {
            addAction(BroadCast.ACTION_START)
            addAction(BroadCast.ACTION_ON_EROR)
            addAction(BroadCast.ACTION_SET_ON_PROGRESS_LISTENER)
            addAction(BroadCast.ACTION_SET_ON_CANCLE_LISTENER)
            addAction(BroadCast.ACTION_SET_ON_PAUSE_LISTENER)
            addAction(BroadCast.ACTION_SET_ON_START_OR_RESUME_LISTENER)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(brodcastMy, intentFilter, RECEIVER_EXPORTED)
            } else {
                registerReceiver(brodcastMy, intentFilter, RECEIVER_EXPORTED)
            }
        } else {
            registerReceiver(brodcastMy, intentFilter)
        }
    }
}

//
//class MainActivity : AppCompatActivity() {
//    private val binding by lazy {
//        ActivityMainBinding.inflate(layoutInflater)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        getBroadCastMy()
//        val intExtra = intent.getIntExtra("id", 0)
//
//
//        binding.btnStop.setOnClickListener {
//            val apply = Intent(this, DownloadService2::class.java).apply {
//                putExtra("stop", true)
//            }
//            val service =
//                PendingIntent.getService(this, 50, apply, PendingIntent.FLAG_IMMUTABLE)
//            service.send()
//        }
//        binding.btnStart.setOnClickListener {
//
//            if (Status.RUNNING == PRDownloader.getStatus(downloadList[position])) {
//                PRDownloader.pause(downloadList[position])
//                return@setOnClickListener
//            }
//            holder.binding.btnStart.isEnabled = false
//            if (Status.PAUSED == PRDownloader.getStatus(downloadList[position])) {
//                PRDownloader.resume(downloadList[position])
//                return@setOnClickListener
//            }
//            val url = holder.binding.urlEtText.text.toString().trim { it <= ' ' }
//            ContextCompat.startForegroundService(context, DownloadService2.newIntent(context, url))
//        }
//
//    }
//
//    fun getBroadCastMy() {
//        val brodcastMy = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val action = intent?.action ?: ""
//                when (action) {
//                    BroadCast.ACTION_SET_ON_START_OR_RESUME_LISTENER -> {
//                        binding.progressHorizontal.isIndeterminate = false
//                        binding.btnStart.isEnabled = true
//                        binding.btnStart.text = "Pause"
//                        binding.btnStop.isEnabled = true
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Downloading started",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//
//                    BroadCast.ACTION_SET_ON_PAUSE_LISTENER -> {
//                        binding.btnStart.text = "Resume"
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Downloading Paused",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//
//                    BroadCast.ACTION_SET_ON_CANCLE_LISTENER -> {
//                        binding.btnStart.text = "Start"
//                        binding.btnStop.isEnabled = false
//                        binding.progressHorizontal.setProgress(0,false)
//                        binding.downloadingPercentage.text = ""
//                        binding.progressHorizontal.isIndeterminate = false
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Downloading Cancelled",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//
//                    }
//
//                    BroadCast.ACTION_SET_ON_PROGRESS_LISTENER -> {
//                        val extra = intent?.getIntExtra(DownloadService.PAGE_PROGRESS, 0) ?: 0
//                        val prs_txt_total =
//                            intent?.getLongExtra(DownloadService.PAGE_TXT_TOTAL_BYTE, 0) ?: 0
//                        val prs_txt_current =
//                            intent?.getLongExtra(DownloadService.PAGE_TXT_CURRENT_BYTE, 0) ?: 0
//                        binding.progressHorizontal.setProgress(extra, true)
//                        binding.downloadingPercentage.text =
//                            Utils.getProgressDisplayLine(prs_txt_current, prs_txt_total)
//                        binding.progressHorizontal.isIndeterminate = false
//                    }
//
//                    BroadCast.ACTION_START -> {
//                        val s = intent?.getStringExtra(DownloadService.URL_PATH) ?: ""
//                        binding.btnStart.isEnabled = false
//                        binding.btnStop.isEnabled = false
//                        binding.btnStart.text = "Completed"
//                        binding.txtUrl.text = "File stored at : $s"
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Downloading Completed",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    BroadCast.ACTION_ON_EROR -> {
//                        binding.btnStart.text = "Start"
//                        binding.downloadingPercentage.text = "0"
//                        binding.progressHorizontal.progress = 0
//                        binding.btnStart.isEnabled = true
//                        binding.btnStop.isEnabled = false
//                        binding.progressHorizontal.isIndeterminate = false
//                        Toast.makeText(this@MainActivity, "Error Occurred", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//
//        }
//        val intentFilter = IntentFilter().apply {
//            addAction(BroadCast.ACTION_START)
//            addAction(BroadCast.ACTION_ON_EROR)
//            addAction(BroadCast.ACTION_SET_ON_PROGRESS_LISTENER)
//            addAction(BroadCast.ACTION_SET_ON_CANCLE_LISTENER)
//            addAction(BroadCast.ACTION_SET_ON_PAUSE_LISTENER)
//            addAction(BroadCast.ACTION_SET_ON_START_OR_RESUME_LISTENER)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                registerReceiver(brodcastMy, intentFilter, RECEIVER_EXPORTED)
//            } else {
//                registerReceiver(brodcastMy, intentFilter, RECEIVER_EXPORTED)
//            }
//        } else {
//            registerReceiver(brodcastMy, intentFilter)
//        }
//    }
//}
//
