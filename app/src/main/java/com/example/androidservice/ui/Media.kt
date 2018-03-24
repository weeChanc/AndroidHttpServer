package com.example.androidservice.ui

import android.content.ContentResolver
import android.provider.MediaStore
import android.util.Log
import java.io.File

/**
 * Created by weechan on 18-3-24.
 */

/**
 * Created by steve on 17-11-22.
 */

class Media {

    companion object {


        private val mContentResolver: ContentResolver = App.ctx.contentResolver

        var musics : List<FileInfo>? = null
        var docs : List<FileInfo>? = null
        var videos : List<FileInfo>? = null
        var pictures : List<FileInfo>? = null


        @Synchronized
        fun getMusic() : List<FileInfo> {
            Log.e("Media","get music")

            if(musics!=null) return musics!!

            val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE)
            val cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, MediaStore.Audio.Media.DATE_MODIFIED + " desc")


            val list = mutableListOf<FileInfo>()

            cursor.moveToFirst()
            do
            {
                var initSize = (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))).toLong()
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                if (initSize == 0L) initSize = File(location).length()
                val info = FileInfo(name, location, initSize)
                list.add(info)
                Log.e("Media","load")

            } while (cursor.moveToNext())

            cursor.close()

            Log.e("Media","finish")

            this.musics = list
            return list

        }

        @Synchronized
        fun getDocument()  : List<FileInfo> {

            if(docs!=null) return docs!!

            val list = mutableListOf<FileInfo>()
            val projection = arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.TITLE)

            val selection = (MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                    + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")

            val selectionArgs = arrayOf("application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel")

            val cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc")



                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val location = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        val initSize = java.lang.Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))
                        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))
                        val info = FileInfo(name, location, initSize)
                        list.add(info)
                    } while (cursor.moveToNext())

                    cursor.close()

                }

            this.docs = list
            return list
        }

        @Synchronized
        fun getVideo() : List<FileInfo> {

            if(videos!=null) return videos!!


            val list = mutableListOf<FileInfo>()

            val cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE,
                            MediaStore.Video.VideoColumns.SIZE, MediaStore.Video.VideoColumns._ID),
                    null, null, MediaStore.Video.VideoColumns.DATE_MODIFIED + "  desc")



                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val location = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                        val id = java.lang.Long.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)))

                        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE))
                        val initSize = java.lang.Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)))
                        val info = FileInfo(name, location, initSize, id)
                        list.add(info)
                    } while (cursor.moveToNext())
                }
                cursor?.close()

                this.videos = list
                return list
            }

        @Synchronized
        fun getPhotos() : List<FileInfo> {

            if(pictures != null) return pictures!!

            val list = mutableListOf<FileInfo>()
            val addPath = HashSet<String>()

            val cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, MediaStore.Images.ImageColumns.DATA + "  desc")



                if (cursor != null && cursor.moveToFirst()) {
                    do {

                        val location = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                        val parentPath = File(location).parent
                        addPath.add(parentPath)
                    } while (cursor.moveToNext())
                }
                cursor.close()

                addPath.forEach {

                    val name = it.substring(it.lastIndexOf('/') + 1, it.length)
                    val location = it
                    val folderInfo = FileInfo(name, location)
                    list.add(folderInfo)
                }
            this.pictures = list
            return list

        }



    }


}


