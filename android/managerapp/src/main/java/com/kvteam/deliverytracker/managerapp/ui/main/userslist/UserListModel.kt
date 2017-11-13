package com.kvteam.deliverytracker.managerapp.ui.main.userslist

import android.os.Parcel
import android.os.Parcelable
import com.kvteam.deliverytracker.core.models.UserModel

data class UserListModel(
    var isSelected: Boolean,
    var isInEditMode: Boolean,
    val userModel: UserModel
) : Parcelable {
    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<UserListModel> {
            override fun createFromParcel(source: Parcel) = UserListModel(source)
            override fun newArray(size: Int): Array<out UserListModel?> = arrayOfNulls(size)
        }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readValue(null) as Boolean,
            parcelIn.readValue(null) as Boolean,
            parcelIn.readParcelable(UserModel::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(isSelected)
        dest.writeValue(isInEditMode)
        dest.writeParcelable(userModel, flags)
    }

    override fun describeContents() = 0
}