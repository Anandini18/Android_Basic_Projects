package com.nandini.android.fragmentstab

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

public class ImageGallery {
    public fun listOfImages(context: Context) : ArrayList<String> {
        var imageList: ArrayList<String> = ArrayList()
        var projection = arrayOf(MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        var orderBy:String=MediaStore.Video.Media.DATE_TAKEN

            val imagecursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, orderBy+"DESC"
            )
            for (i in 0 until imagecursor!!.count) {
                imagecursor.moveToPosition(i)
                val dataColumnIndex =
                    imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)
                imageList.add(imagecursor.getString(dataColumnIndex))
            }
            return imageList
        }
    }
