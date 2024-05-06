package uz.coder.prdownloadertest

data class DownloadOBJ(
    val downloadId:Int,
    val url:String,
    val progressisIndeterminate:Boolean,
    val btnStartIsEnabled:Boolean,
    val btnStopIsEnabled:Boolean,
    val btnStartText:String,
    val progressHorizontalProgress:Int,
    val downloadingPercentageText:String,
    val downloadFilePath:String
)