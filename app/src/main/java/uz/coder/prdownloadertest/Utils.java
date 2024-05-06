package uz.coder.prdownloadertest;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Locale;

public final class Utils {

    private Utils() {

    }

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

//    private val binding by lazy {
//        ActivityMainBinding.inflate(layoutInflater)
//    }
//
//    private
//    var downloadID = 0
//    private var path = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//
//        // Initializing PRDownloader library
//        PRDownloader.initialize(this)
//
//
//        //storing the path of the file
//        path = Utils.getRootDirPath(this)
//
//        // handling onclick event on button
//        binding.btnDownload.setOnClickListener(View.OnClickListener { // getting the text from edittext
//            // and storing it to url variable
//            val url = binding.urlEtText.text.toString().trim { it <= ' ' }
//            // setting the visibility of linear layout to visible
//            binding.detailsBox.visibility = View.VISIBLE
//            // calling method downloadFile passing url as parameter
//            downloadFile(url)
//        })
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun downloadFile(url: String) {
//        binding.btnStart.setOnClickListener(View.OnClickListener {
//            if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
//                PRDownloader.pause(downloadID)
//                return@OnClickListener
//            }
//            binding.btnStart.isEnabled = false
//            if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
//                PRDownloader.resume(downloadID)
//                return@OnClickListener
//            }
//            val fileName = URLUtil.guessFileName(url, null, null)
//            binding.fileName.text = "Downloading $fileName"
//            downloadID = PRDownloader.download(url, path, fileName)
//                .build()
//                .setOnStartOrResumeListener {
//                    binding.progressHorizontal.isIndeterminate = false
//                    binding.btnStart.isEnabled = true
//                    binding.btnStart.text = "Pause"
//                    binding.btnStop.isEnabled = true
//                    Toast.makeText(this@MainActivity, "Downloading started", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                .setOnPauseListener {
//                    binding.btnStart.text = "Resume"
//                    Toast.makeText(this@MainActivity, "Downloading Paused", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                .setOnCancelListener {
//                    downloadID = 0
//                    binding.btnStart.text = "Start"
//                    binding.btnStop.isEnabled = false
//                    binding.progressHorizontal.progress = 0
//                    binding.downloadingPercentage.text = ""
//                    binding.progressHorizontal.isIndeterminate = false
//                    Toast.makeText(this@MainActivity, "Downloading Cancelled", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                .setOnProgressListener { progress ->
//                    val progressPer = progress.currentBytes * 100 / progress.totalBytes
//                    binding.progressHorizontal.progress = progressPer.toInt()
//                    binding.downloadingPercentage.text = Utils.getProgressDisplayLine(
//                        progress.currentBytes,
//                        progress.totalBytes
//                    )
//                    binding.progressHorizontal.isIndeterminate = false
//                }
//                .start(object : OnDownloadListener {
//                    override fun onDownloadComplete() {
//                        binding.btnStart.isEnabled = false
//                        binding.btnStop.setEnabled(false)
//                        binding.btnStart.text = "Completed"
//                        binding.txtUrl.text = "File stored at : $path"
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Downloading Completed",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    override fun onError(error: Error) {
//                        binding.btnStart.text = "Start"
//                        binding.downloadingPercentage.text = "0"
//                        // resetting the progressbar
//                        binding.progressHorizontal.progress = 0
//                        // resetting the downloadID
//                        downloadID = 0
//                        // enabling the start button
//                        binding.btnStart.isEnabled = true
//                        // disabling the cancel button
//                        binding.btnStop.isEnabled = false
//                        binding.progressHorizontal.isIndeterminate = false
//                        Toast.makeText(this@MainActivity, "Error Occurred", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                })
//
//            // handling click event on cancel button
//            binding.btnStop.setOnClickListener {
//                binding.btnStart.text = "Start"
//                // cancels the download
//                PRDownloader.cancel(downloadID)
//            }
//        })
//    }
}
