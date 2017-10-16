package com.kvteam.deliverytracker.managerapp.dagger

import android.app.Activity
import android.content.Context
import dagger.Binds
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.managerapp.FirstFragment
import com.kvteam.deliverytracker.managerapp.LoginActivity
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import dagger.Component
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import dagger.BindsInstance




/**
 * Created by maxim on 15.10.17.
 */

interface ISimpleType


class SimpleType: ISimpleType {
    var context: Context
    var someNumber = Random().nextInt()

    constructor(ctx: Context) {
        this.context = ctx
    }
}
