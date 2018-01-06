package com.kvteam.deliverytracker.core.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class TaskModel(
        var id: UUID? = null,
        var number: String? = null,
        var details: String? = null,
        var shippingDesc: String? = null,
        var address: String? = null,
        var state: String? = null,
        @Embedded(prefix = "instance_") var instance: InstanceModel? = null,
        @Embedded(prefix = "author_") var author: UserModel? = null,
        @Embedded(prefix = "performer_") var performer: UserModel? = null,
        @Embedded var taskDateTimeRange: DateTimeRangeModel? = null,
        var creationDate: Date? = null,
        var setPerformerDate: Date? = null,
        var inWorkDate: Date? = null,
        var completionDate: Date? = null) : Parcelable {


    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<TaskModel> {
            override fun createFromParcel(source: Parcel) = TaskModel(source)
            override fun newArray(size: Int): Array<out TaskModel?> = arrayOfNulls(size)
        }
    }

    @Ignore
    constructor(parcelIn: Parcel) : this(
            UUID.fromString(parcelIn.readString()),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readParcelable(InstanceModel::class.java.classLoader),
            parcelIn.readParcelable(UserModel::class.java.classLoader),
            parcelIn.readParcelable(UserModel::class.java.classLoader),
            parcelIn.readParcelable(DateTimeRangeModel::class.java.classLoader),
            parcelIn.readSerializable() as Date,
            parcelIn.readSerializable() as Date,
            parcelIn.readSerializable() as Date,
            parcelIn.readSerializable() as Date
            )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id.toString())
        dest.writeString(number)
        dest.writeString(details)
        dest.writeString(shippingDesc)
        dest.writeString(address)
        dest.writeString(state)
        dest.writeParcelable(instance, flags)
        dest.writeParcelable(author, flags)
        dest.writeParcelable(performer, flags)
        dest.writeParcelable(taskDateTimeRange, flags)
        dest.writeSerializable(creationDate)
        dest.writeSerializable(setPerformerDate)
        dest.writeSerializable(inWorkDate)
        dest.writeSerializable(completionDate)
    }

    override fun describeContents() = 0
}