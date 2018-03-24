package com.example.androidservice.ui

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by weechan on 18-3-24.
 */


/**
 * Created by steve on 17-11-27.
 */
data class FileInfo(val name: String,
                    @SerializedName("path")val location: String,
                    @SerializedName("size")val initSize: Long = 0,
                    @Transient var id: Long = 0,
                    @Transient var icon: Drawable? = null) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeLong(initSize)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileInfo> {
        override fun createFromParcel(parcel: Parcel): FileInfo {
            return FileInfo(parcel)
        }

        override fun newArray(size: Int): Array<FileInfo?> {
            return arrayOfNulls(size)
        }
    }

}