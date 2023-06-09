package com.example.cotam.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class UserData(
    val userId: String? = null,
    val username: String? = null,
    val image: String? = null,
    var token: String? = null,

    val sendMsgTo: List<String> = emptyList(),
    val gotMsgFrom: List<String> = emptyList(),

    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    fun toMap() = mapOf(
        "username" to username,
        "image" to image,
        "sendMsgTo" to sendMsgTo,
        "gotMsgFrom" to gotMsgFrom,
        "token" to token
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(image)
        parcel.writeStringList(sendMsgTo)
        parcel.writeStringList(gotMsgFrom)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }

}
