package com.android.jenny.videoobjectdetect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.jenny.videoobjectdetect.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ObjectActivity"
        const val REQUEST_VIDEO_GET: Int = 101
        const val DETECT_RESULT = "N"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var choseButton: Button
    private lateinit var tvPlaceholder: TextView
    private lateinit var resultTextView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        imageView = binding.imageView
        tvPlaceholder = binding.tvPlaceholder
        resultTextView = binding.resultTextView
        choseButton = binding.choseButton
        choseButton.setOnClickListener {
            Log.e(TAG, "choseButton Click")
            cleanUpVariable()
            dispatchGetVideoIntent()
        }
    }

    private fun cleanUpVariable() {
        Log.e(TAG, "cleanUpVariable() ")
        resultTextView.text = ""
    }

    private fun dispatchGetVideoIntent() {
        Log.e(TAG, "dispatchGetVideoIntent()")
        Intent(Intent.ACTION_GET_CONTENT).also { getVideoIntent ->
            getVideoIntent.type = "video/*"
            startActivityForResult(Intent.createChooser(getVideoIntent, "select video"), REQUEST_VIDEO_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult()")
        if (requestCode == REQUEST_VIDEO_GET && resultCode == Activity.RESULT_OK) {
//            data!!.data?.let { getFrameFromVideo(it) }

            var selectedVideoUri = data!!.data
            Log.e(TAG, "selectedVideoUri: $selectedVideoUri")
            // MEDIA GALLERY
            var selectedVideoPath = selectedVideoUri?.let { getPath(it) }
            if (selectedVideoPath != null) {
                Log.e(TAG, "selectedVideoPath:$selectedVideoPath")
            }

        }
    }

    @SuppressLint("Recycle")
    private fun getPath(uri: Uri): String? {
        Log.e(TAG, "getPath()")
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Video.Media.ALBUM)
            cursor = contentResolver.query(uri, projection, null, null, null)!!

            var columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)
            cursor.moveToFirst()
            return columnIndex.let { cursor.getString(it) }
        } finally {
            cursor?.close()
        }
    }

    private fun getFrameFromVideo(path: String?): Bitmap? {
        Log.e(TAG, "getFrameFromVideo()")
        var bitmap: Bitmap? = null
        var retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            val bytes = retriever.embeddedPicture
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFrameFromVideo_try-catch:${e.message}")
        } finally {
            try {
                retriever.release()
            } catch (e2: Exception) {
                Log.e(TAG, "getFrameFromVideo_finally:${e2.message}")
            }
        }
        return bitmap
    }

    fun createAlbumArt(filePath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            val bytes = retriever.embeddedPicture
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        return bitmap
    }




}