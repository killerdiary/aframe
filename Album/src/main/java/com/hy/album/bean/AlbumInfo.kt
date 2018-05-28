package com.hy.album.bean

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.hy.frame.util.HyUtil

/**
 * AlbumInfo
 * @author HeYan
 * @time 2017/10/16 15:42
 */
class AlbumInfo : Parcelable {
    var id: Int = 0
    var name: String? = null
    var thumb: String? = null
    var size: Long = 0
    var time: Long = 0
    var isSelected: Boolean = false
    var flag: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString()
        thumb = parcel.readString()
        size = parcel.readLong()
        time = parcel.readLong()
        isSelected = parcel.readByte() != 0.toByte()
        flag = parcel.readInt()
    }

    constructor()

    constructor(name: String?, thumb: String?) {
        this.name = name
        this.thumb = thumb
    }

    constructor(id: Int, name: String?, thumb: String?, size: Long, time: Long) {
        this.id = id
        this.name = name
        this.thumb = thumb
        this.size = size
        this.time = time
    }

    fun getDate(): String {
        val cur = System.currentTimeMillis()
        return when {
            TextUtils.equals(HyUtil.getDateTime(cur, "yyyy-MM-dd"), HyUtil.getDateTime(time, "yyyy-MM-dd")) -> HyUtil.getDateTime(time, "HH:mm")
            else -> HyUtil.getDateTime(time, "MM-dd")
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(thumb)
        parcel.writeLong(size)
        parcel.writeLong(time)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(flag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumInfo> {
        override fun createFromParcel(parcel: Parcel): AlbumInfo {
            return AlbumInfo(parcel)
        }

        override fun newArray(size: Int): Array<AlbumInfo?> {
            return arrayOfNulls(size)
        }
    }
}