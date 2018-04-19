package com.kvteam.deliverytracker.core.dagger.modules

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.common.*
import com.kvteam.deliverytracker.core.dataprovider.*
import com.kvteam.deliverytracker.core.dataprovider.base.DataProvider
import com.kvteam.deliverytracker.core.dataprovider.base.ViewDigestComponent
import com.kvteam.deliverytracker.core.dataprovider.base.ViewDigestContainer
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
    fun configuration(
            gson: IDeliveryTrackerGsonProvider,
            app: T): Configuration {
        return Configuration(gson, app)
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
            configuration: Configuration,
            httpManager: IHttpManager,
            app: T,
            sessionInfo: ISessionInfo,
            storage: IStorage): ISession {
        return Session(
                gson,
                configuration,
                httpManager,
                sessionInfo,
                app.applicationContext,
                storage)
    }

    @Provides
    @Singleton
    fun webservice(
            gson: IDeliveryTrackerGsonProvider,
            conf: Configuration,
            session: ISession,
            httpManager: IHttpManager): IWebservice {
        return Webservice(gson, conf, session, httpManager)
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
            webservice: IWebservice): ITaskWebservice {
        return TaskWebservice(webservice)
    }


    @Provides
    @Singleton
    fun dataProvider(viewDigestComponent: ViewDigestComponent,
                     clientComponent: ClientDataComponent,
                     clientAddressDataComponent: ClientAddressDataComponent,
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
                     taskProductDataComponent: TaskProductDataComponent,
                     clientContainer: ClientDataContainer,
                     clientAddressDataContainer: ClientAddressDataContainer,
                     paymentTypesContainer: PaymentTypeDataContainer,
                     productsContainer: ProductDataContainer,
                     warehousesContainer: WarehouseDataContainer,
                     userContainer: UserDataContainer,
                     invitationContainer: InvitationDataContainer,
                     taskInfoContainer: TaskInfoDataContainer,
                     taskProductDataContainer: TaskProductDataContainer) : DataProvider {
        return DataProvider(
                viewDigestComponent,
                clientComponent,
                clientAddressDataComponent,
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
                taskProductDataComponent,
                clientContainer,
                clientAddressDataContainer,
                paymentTypesContainer,
                productsContainer,
                warehousesContainer,
                userContainer,
                invitationContainer,
                taskInfoContainer,
                taskProductDataContainer)
    }

    @Provides
    @Singleton
    fun viewDigestContainer() : ViewDigestContainer {
        return ViewDigestContainer()
    }

    @Provides
    @Singleton
    fun viewDigestComponent(container: ViewDigestContainer, viewWebservice: IViewWebservice) : ViewDigestComponent {
        return ViewDigestComponent(
                container,
                viewWebservice)
    }

    @Provides
    @Singleton
    fun clientContainer(clientAddressDataContainer: ClientAddressDataContainer) : ClientDataContainer {
        return ClientDataContainer(clientAddressDataContainer)
    }

    @Provides
    @Singleton
    fun clientComponent(referenceWebservice: IReferenceWebservice, clientsContainer: ClientDataContainer, clientAddressDataContainer: ClientAddressDataContainer) :ClientDataComponent {
        return ClientDataComponent(referenceWebservice, clientsContainer, clientAddressDataContainer)
    }

    @Provides
    @Singleton
    fun clientViewComponent(viewWebservice: IViewWebservice, clientsContainer: ClientDataContainer, clientAddressDataContainer: ClientAddressDataContainer) : ClientViewComponent {
        return ClientViewComponent(viewWebservice, clientsContainer, clientAddressDataContainer)
    }

    @Provides
    @Singleton
    fun clientAddressContainer() : ClientAddressDataContainer {
        return ClientAddressDataContainer()
    }

    @Provides
    @Singleton
    fun clientAddressComponent(clientAddressDataContainer: ClientAddressDataContainer) :ClientAddressDataComponent {
        return ClientAddressDataComponent(clientAddressDataContainer)
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
    fun taskInfoContainer() : TaskInfoDataContainer {
        return TaskInfoDataContainer()
    }

    @Provides
    @Singleton
    fun taskInfoComponent(webservice: ITaskWebservice,
                          productsDataContainer: ProductDataContainer,
                          paymentTypeDataContainer: PaymentTypeDataContainer,
                          warehouseDataContainer: WarehouseDataContainer,
                          clientDataContainer: ClientDataContainer,
                          clientAddressDataContainer: ClientAddressDataContainer,
                          userDataContainer: UserDataContainer,
                          taskProductDataContainer: TaskProductDataContainer,
                          container: TaskInfoDataContainer): TaskInfoDataComponent {
        return TaskInfoDataComponent(webservice,
                productsDataContainer,
                paymentTypeDataContainer,
                warehouseDataContainer,
                clientDataContainer,
                clientAddressDataContainer,
                userDataContainer,
                taskProductDataContainer,
                container)
    }

    @Provides
    @Singleton
    fun taskInfoViewComponent(viewWebservice: IViewWebservice, container: TaskInfoDataContainer) : TaskInfoViewComponent {
        return TaskInfoViewComponent(viewWebservice, container)
    }

    @Provides
    @Singleton
    fun taskProductContainer() : TaskProductDataContainer {
        return TaskProductDataContainer()
    }

    @Provides
    @Singleton
    fun taskProductComponent(container: TaskProductDataContainer) :TaskProductDataComponent {
        return TaskProductDataComponent(container)
    }

}