package com.example.cotam.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class MessageData(
    val messageId: String? = null,
    var message: String? = null,
    var messageIsEmoted: String? = null,
    var replyMessage: String? = null,
    var replyImage: String? = null,
    var replyVideo: String? = null,
    var imageUrl: String? = null,
    var videoUrl: String? = null,

    val senderId: String? = null,
    val senderImage: String? = null,
    val senderUsername: String? = null,

    val getterId: String? = null,
    val getterImage: String? = null,
    val getterUsername: String? = null,

    val time: Timestamp? = Timestamp.now(),

    val visibility: MutableList<String> = mutableListOf()

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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        TODO("visibility")
    ) {
    }

    fun toMap() = mapOf(
        "visibility" to visibility
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeString(message)
        parcel.writeString(messageIsEmoted)
        parcel.writeString(replyMessage)
        parcel.writeString(replyImage)
        parcel.writeString(replyVideo)
        parcel.writeString(imageUrl)
        parcel.writeString(videoUrl)
        parcel.writeString(senderId)
        parcel.writeString(senderImage)
        parcel.writeString(senderUsername)
        parcel.writeString(getterId)
        parcel.writeString(getterImage)
        parcel.writeString(getterUsername)
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
