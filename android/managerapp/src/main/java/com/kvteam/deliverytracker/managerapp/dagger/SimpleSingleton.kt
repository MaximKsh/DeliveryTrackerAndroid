package com.kvteam.deliverytracker.managerapp.dagger

import android.app.Application
import android.content.Context
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.managerapp.LoginActivity
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import dagger.*
import java.util.*
import javax.inject.Singleton

interface ISimpleSingleton
class SimpleSingleton: ISimpleSingleton {
    var someNumber = Random().nextInt()
}

interface ISimpleActivity
class SimpleActivity: ISimpleActivity {
    var someNumber = Random().nextInt()
}


interface IType1
interface IType2

class Type1: IType1 {
    var someNumber = Random().nextInt()
}


class Type2: IType2 {
    var someNumber = Random().nextInt()
    var t1: IType1

    constructor(t1: IType1) {
        this.t1 = t1
    }

}

@Module
internal class SimpleSingletonModule{
    @Provides
    @Singleton
    fun simpleSingleton(context: ManagerApplication): ISimpleSingleton {
        return SimpleSingleton()
    }
}


@Module
internal class SimpleActivityModule1{

    @ActivityScope
    @Provides
    fun simpleActivity(): ISimpleActivity {
        return SimpleActivity()
    }
}

@Module
internal class SimpleTypeModule{
    @Provides
    @FragmentScope
    fun simpleType1(): IType1 {
        return Type1()
    }

    @Provides
    @FragmentScope
    fun simpleType2(type1: IType1): IType2 {
        return Type2(type1)
    }
}
/*
@ActivityScope
@Component(
        dependencies = arrayOf(AppComponent::class),
        modules = arrayOf(SimpleActivityModule1::class))
internal interface SimpleActivityComponent {
    fun simpleSingleton(): ISimpleSingleton

    fun simpleActivity(): ISimpleActivity
}

*/


/*
@Module
internal class SimpleSingletonModule{
    @Provides
    @Singleton
    fun simpleSingleton(context: ManagerApplication): ISimpleSingleton {
        return SimpleSingleton()
    }
}*/