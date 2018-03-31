package com.kvteam.deliverytracker.core.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.EditText
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.models.User

@SuppressLint("RestrictedApi")
fun removeShiftMode(view: BottomNavigationView): Boolean {
    val menuView = view.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            item.setShiftingMode(false)
            item.setChecked(item.itemData.isChecked)
        }
    } catch (e: NoSuchFieldException) {
        Log.e("ERROR NO SUCH FIELD", "Unable to getAsync shift mode field")
        return false
    } catch (e: IllegalAccessException) {
        Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode")
        return false
    }
    return true
}

fun EditText.setNullableText(str: String?) {
    if (str != null) {
        this.setText(str)
    } else {
        this.text.clear()
    }
}

fun materialDefaultAvatar(user: User?) : TextDrawable{
    val text = StringBuilder(4)
    if(user?.name?.isNotBlank() == true) {
        text.append(user.name!![0].toString())
    }
    if(user?.surname?.isNotBlank() == true) {
        text.append(user.surname!![0].toString())
    }

    val materialAvatarDefault = TextDrawable.builder()
            .buildRound(text.toString(), Color.LTGRAY)

    return materialAvatarDefault
}