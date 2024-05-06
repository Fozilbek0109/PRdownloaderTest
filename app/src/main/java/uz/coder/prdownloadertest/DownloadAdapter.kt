package uz.coder.prdownloadertest

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.downloader.PRDownloader
import com.downloader.Status
import uz.coder.prdownloadertest.databinding.ItemDownloadBinding

class DownloadAdapter(
    val downloadList: List<DownloadOBJ>,
    val context: Context,
    val stop:()->Unit,
    val start:()->Unit
):RecyclerView.Adapter<DownloadAdapter.VH>() {

    inner class VH(val binding: ItemDownloadBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(ItemDownloadBinding.inflate(
        LayoutInflater.from(parent.context),parent,false))

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.btnDownload.setOnClickListener {
            if (holder.binding.urlEtText.text.toString().isNotEmpty()) {
                holder.binding.detailsBox.visibility = View.VISIBLE
            }
        }
        holder.binding.btnStart.setOnClickListener {
            start.invoke()
        }
        holder.binding.btnStop.setOnClickListener {
            stop.invoke()
        }
    }


}