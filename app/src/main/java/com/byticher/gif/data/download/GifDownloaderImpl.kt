package com.byticher.gif.data.download

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.byticher.gif.R
import com.byticher.gif.const.ConstValues
import com.byticher.gif.domain.Gif
import kotlinx.coroutines.CancellationException
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class GifDownloaderImpl (
    private val context: Context
): GifDownloader {

    override suspend fun downloadAndShareGif(gif: Gif): Boolean {
        val client = OkHttpClient()
        val request = Request.Builder().url(gif.url).build()
        val fileName = gif.title.filter { it.isLetterOrDigit() } + ".gif"

        val type = Environment.DIRECTORY_PICTURES
        val target = Environment.getExternalStoragePublicDirectory(type)
        val file = File(target, fileName)
        var downloadCompleted = false

        if (!file.exists()) {
            try {
                client.newCall(request).execute().use { response ->
                    response.body?.byteStream()?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                Log.d("GifDownloader", "Successfully downloaded $fileName")
                downloadCompleted = true
            } catch (e: CancellationException) {
                Log.d("GifDownloader", "Download cancelled")
                throw e
            } catch (e: Exception) {
                Log.d("GifDownloader", "Downloading $fileName failed due to ${e.message}")
                return false
            }
        }

        if (downloadCompleted) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf(ConstValues.GIF_TYPE)
            ) { path, uri ->
                Log.d("MediaScanner", "newGif: $path || $uri")
                if (uri != null) {
                    shareGif(uri, gif)
                } else {
                    Log.d("MediaScanner", "MediaScanner returned null Uri")
                }
            }
        }

        return true
    }

    private fun shareGif(uri: Uri, gif: Gif) {
        val shareIntent: Intent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, gif.title)
            putExtra(Intent.EXTRA_TITLE, context.getString(R.string.share_gif))

            putExtra(Intent.EXTRA_STREAM, uri)
            setTypeAndNormalize(ConstValues.GIF_TYPE)

            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)
        context.startActivity(shareIntent)
        Log.d("GifDownloader", "Successfully shared file at ${uri.path}")
    }
}