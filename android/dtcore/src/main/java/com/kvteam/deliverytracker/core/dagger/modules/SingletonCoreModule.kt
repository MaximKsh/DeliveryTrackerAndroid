package com.kvteam.deliverytracker.core.dagger.modules

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.dataprovider.*
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.session.ISessionInfo
import com.kvteam.deliverytracker.core.session.Session
import com.kvteam.deliverytracker.core.storage.IStorage
import com.kvteam.deliverytracker.core.storage.Storage
import com.kvteam.deliverytracker.core.webservice.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class SingletonCoreModule<in T : DeliveryTrackerApplication> {

    @Provides
    @Singleton
    fun gsonProvider(): IDeliveryTrackerGsonProvider {
        return DeliveryTrackerGsonProvider()
    }

    @Provides
    @Singleton
    fun httpManager(): IHttpManager {
        return HttpManager()
    }

    @Provides
    @Singleton
    fun session(
            gson: IDeliveryTrackerGsonProvider,
            httpManager: IHttpManager,
            app: T,
            sessionInfo: ISessionInfo): ISession {
        return Session(
                gson,
                httpManager,
                sessionInfo,
                app.applicationContext)
    }

    @Provides
    @Singleton
    fun webservice(
            gson: IDeliveryTrackerGsonProvider,
            app: T,
            session: ISession,
            httpManager: IHttpManager): IWebservice {
        return Webservice(gson, app.applicationContext, session, httpManager)
    }

    @Provides
    @Singleton
    fun storage(app: T): IStorage {
        return Storage(app.applicationContext)
    }

    @Provides
    @Singleton
    fun localizationManager(ext: ILocalizationManagerExtension, app: T): ILocalizationManager {
        return LocalizationManager(ext, app)
    }

    @Provides
    @Singleton
    fun instanceWebservice(
            webservice: IWebservice): IInstanceWebservice {
        return InstanceWebservice(webservice)
    }

    @Provides
    @Singleton
    fun invitationWebservice(
            webservice: IWebservice): IInvitationWebservice {
        return InvitationWebservice(webservice)
    }

    @Provides
    @Singleton
    fun userWebservice(
            webservice: IWebservice): IUserWebservice {
        return UserWebservice(webservice)
    }

    @Provides
    @Singleton
    fun referenceWebservice(
            webservice: IWebservice): IReferenceWebservice {
        return ReferenceWebservice(webservice)
    }

    @Provides
    @Singleton
    fun viewWebservice(
            webservice: IWebservice): IViewWebservice {
        return ViewWebservice(webservice)
    }

    @Provides
    @Singleton
    fun taskWebservice(
            webservice: IWebservice,
            session: ISession): ITaskWebservice {
        return TaskWebservice(webservice, session)
    }


    @Provides
    @Singleton
    fun dataProvider(viewDigestComponent: ViewDigestComponent,
                     clientComponent: ClientDataComponent,
                     clientViewComponent: ClientViewComponent,
                     paymentTypeComponent: PaymentTypeDataComponent,
                     paymentTypeViewComponent: PaymentTypeViewComponent,
                     productComponent: ProductDataComponent,
                     productViewComponent: ProductViewComponent,
                     warehouseComponent: WarehouseDataComponent,
                     warehouseViewComponent: WarehouseViewComponent,
                     userComponent: UserDataComponent,
                     userViewComponent: UserViewComponent,
                     invitations: InvitationDataComponent,
                     invitationView: InvitationViewComponent,
                     taskInfos: TaskInfoDataComponent,
                     taskInfoViews: TaskInfoViewComponent,
                     taskStateTransitions: TaskStateTransitionDataComponent,
                     clientContainer: ClientDataContainer,
                     paymentTypesContainer: PaymentTypeDataContainer,
                     productsContainer: ProductDataContainer,
                     warehousesContainer: WarehouseDataContainer,
                     userContainer: UserDataContainer,
                     invitationContainer: InvitationDataContainer,
                     taskInfoContainer: TaskInfoDataContainer,
                     taskStateContainer: TaskStateTransitionDataContainer) : DataProvider {
        return DataProvider(
                viewDigestComponent,
                clientComponent,
                clientViewComponent,
                paymentTypeComponent,
                paymentTypeViewComponent,
                productComponent,
                productViewComponent,
                warehouseComponent,
                warehouseViewComponent,
                userComponent,
                userViewComponent,
                invitations,
                invitationView,
                taskInfos,
                taskInfoViews,
                taskStateTransitions,
                clientContainer,
                paymentTypesContainer,
                productsContainer,
                warehousesContainer,
                userContainer,
                invitationContainer,
                taskInfoContainer,
                taskStateContainer)
    }

    @Provides
    @Singleton
    fun viewDigestContainer() : ViewDigestContainer {
        return ViewDigestContainer()
    }

    @Provides
    @Singleton
    fun viewDigestComponent(container: ViewDigestContainer, viewWebservice: IViewWebservice) : ViewDigestComponent {
        return ViewDigestComponent(container, viewWebservice)
    }

    @Provides
    @Singleton
    fun clientContainer() : ClientDataContainer {
        return ClientDataContainer()
    }

    @Provides
    @Singleton
    fun clientComponent(referenceWebservice: IReferenceWebservice, clientsContainer: ClientDataContainer) :ClientDataComponent {
        return ClientDataComponent(referenceWebservice, clientsContainer)
    }

    @Provides
    @Singleton
    fun clientViewComponent(viewWebservice: IViewWebservice, clientsContainer: ClientDataContainer) : ClientViewComponent {
        return ClientViewComponent(viewWebservice, clientsContainer)
    }

    @Provides
    @Singleton
    fun warehouseContainer() : WarehouseDataContainer {
        return WarehouseDataContainer()
    }

    @Provides
    @Singleton
    fun warehouseComponent(referenceWebservice: IReferenceWebservice, clientsContainer: WarehouseDataContainer): WarehouseDataComponent {
        return WarehouseDataComponent(referenceWebservice, clientsContainer)
    }

    @Provides
    @Singleton
    fun warehouseViewComponent(viewWebservice: IViewWebservice, container: WarehouseDataContainer) : WarehouseViewComponent {
        return WarehouseViewComponent(viewWebservice, container)
    }

    @Provides
    @Singleton
    fun productContainer() : ProductDataContainer {
        return ProductDataContainer()
    }

    @Provides
    @Singleton
    fun productComponent(referenceWebservice: IReferenceWebservice, container: ProductDataContainer): ProductDataComponent {
        return ProductDataComponent(referenceWebservice, container)
    }

    @Provides
    @Singleton
    fun productViewComponent(viewWebservice: IViewWebservice, container: ProductDataContainer) : ProductViewComponent {
        return ProductViewComponent(viewWebservice, container)
    }

    @Provides
    @Singleton
    fun paymentTypeContainer() : PaymentTypeDataContainer {
        return PaymentTypeDataContainer()
    }

    @Provides
    @Singleton
    fun paymentTypeComponent(referenceWebservice: IReferenceWebservice, container: PaymentTypeDataContainer): PaymentTypeDataComponent {
        return PaymentTypeDataComponent(referenceWebservice, container)
    }

    @Provides
    @Singleton
    fun paymentTypeViewComponent(viewWebservice: IViewWebservice, clientsContainer: PaymentTypeDataContainer) : PaymentTypeViewComponent {
        return PaymentTypeViewComponent(viewWebservice, clientsContainer)
    }

    @Provides
    @Singleton
    fun userContainer() : UserDataContainer {
        return UserDataContainer()
    }

    @Provides
    @Singleton
    fun userComponent(userWebservice: IUserWebservice, container: UserDataContainer): UserDataComponent {
        return UserDataComponent(userWebservice, container)
    }

    @Provides
    @Singleton
    fun userViewComponent(viewWebservice: IViewWebservice, container: UserDataContainer) : UserViewComponent {
        return UserViewComponent(viewWebservice, container)
    }

    @Provides
    @Singleton
    fun invitationContainer() : InvitationDataContainer {
        return InvitationDataContainer()
    }

    @Provides
    @Singleton
    fun invitationComponent(invitationWebservice: IInvitationWebservice, container: InvitationDataContainer): InvitationDataComponent {
        return InvitationDataComponent(invitationWebservice, container)
    }

    @Provides
    @Singleton
    fun invitationViewComponent(viewWebservice: IViewWebservice, container: InvitationDataContainer) : InvitationViewComponent {
        return InvitationViewComponent(viewWebservice, container)
    }

    @Provides
    @Singleton
    fun taskStateTransitionContainer() : TaskStateTransitionDataContainer {
        return TaskStateTransitionDataContainer()
    }

    @Provides
    @Singleton
    fun taskStateTransitionComponent(container: TaskStateTransitionDataContainer): TaskStateTransitionDataComponent {
        return TaskStateTransitionDataComponent(container)
    }

    @Provides
    @Singleton
    fun taskInfoContainer() : TaskInfoDataContainer {
        return TaskInfoDataContainer()
    }

    @Provides
    @Singleton
    fun taskInfoComponent(webservice: ITaskWebservice,
                          taskStateTransitionDataContainer: TaskStateTransitionDataContainer,
                          productsDataContainer: ProductDataContainer,
                          paymentTypeDataContainer: PaymentTypeDataContainer,
                          warehouseDataContainer: WarehouseDataContainer,
                          clientDataContainer: ClientDataContainer,
                          userDataContainer: UserDataContainer,
                          container: TaskInfoDataContainer): TaskInfoDataComponent {
        return TaskInfoDataComponent(webservice,
                taskStateTransitionDataContainer,
                productsDataContainer,
                paymentTypeDataContainer,
                warehouseDataContainer,
                clientDataContainer,
                userDataContainer,
                container)
    }

    @Provides
    @Singleton
    fun taskInfoViewComponent(viewWebservice: IViewWebservice, container: TaskInfoDataContainer) : TaskInfoViewComponent {
        return TaskInfoViewComponent(viewWebservice, container)
    }

}