package com.example.cotam.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class MessageData(
    val messageId: String? = null,
    var message: String? = null,
    var imageUrl: String? = null,

    val senderUserId: String? = null,
    val senderUsername: String? = null,
    val senderUserImage: String? = null,

    val getterUsername: String? = null,
    val getterUserId: String? = null,
    val getterUserImage: String? = null,
    val getterToken: String? = null,

    val time: Timestamp? = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeString(message)
        parcel.writeString(imageUrl)
        parcel.writeString(senderUserId)
        parcel.writeString(senderUsername)
        parcel.writeString(senderUserImage)
        parcel.writeString(getterUsername)
        parcel.writeString(getterUserId)
        parcel.writeString(getterUserImage)
        parcel.writeString(getterToken)
        parcel.writeParcelable(time, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageData> {
        override fun createFromParcel(parcel: Parcel): MessageData {
            return MessageData(parcel)
        }

        override fun newArray(size: Int): Array<MessageData?> {
            return arrayOfNulls(size)
        }
    }


}
