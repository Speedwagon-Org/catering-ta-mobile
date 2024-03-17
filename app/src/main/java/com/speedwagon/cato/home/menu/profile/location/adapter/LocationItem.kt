package com.speedwagon.cato.home.menu.profile.location.adapter
import android.os.Parcel
import android.os.Parcelable

data class LocationItem(
    var id: String,
    var label: String,
    var description: String,
    var lat: Double,
    var long: Double,
    var isDefault: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(label)
        parcel.writeString(description)
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
        parcel.writeByte(if (isDefault) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationItem> {
        override fun createFromParcel(parcel: Parcel): LocationItem {
            return LocationItem(parcel)
        }

        override fun newArray(size: Int): Array<LocationItem?> {
            return arrayOfNulls(size)
        }
    }
}

