package com.kvteam.deliverytracker.core.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.setPhoneNumber
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_edit_settings.*
import kotlinx.android.synthetic.main.fragment_base_settings.*
import javax.inject.Inject

abstract class BaseEditSettingsFragment : DeliveryTrackerFragment() {
    private val scrollPosKey = "scrollPosKey"


    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    lateinit var validation: AwesomeValidation

    protected abstract fun afterSuccessfulEdit()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_edit_settings, container, false)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbarController.setToolbarTitle(lm.getString(R.string.Core_EditProfile))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val position = savedInstanceState?.getInt(scrollPosKey)
        if (position != null) {
            svMainScrollView.scrollY = position
        }

        val user = session.user!!
        initControls(user)

        validation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        validation.addValidation(
                etSurname, RegexTemplate.NOT_EMPTY, getString(R.string.Core_SurnameValidationError))
        validation.addValidation(
                etName, RegexTemplate.NOT_EMPTY, getString(R.string.Core_NameValidationError))
        validation.addValidation(
                etPhoneNumber,
                {
                    it.length == 16
                },
                getString(R.string.Core_PhoneValidationError))
        validation.setContext(this.dtActivity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(scrollPosKey, svMainScrollView.scrollY)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> editSettings()
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private suspend fun editSettings() {
        if(!validation.validate()) {
            return
        }

        val modifiedUser = User()
        val originalUser = session.user!!

        modifiedUser.id = originalUser.id
        modifiedUser.instanceId = originalUser.instanceId

        var cnt = 0
        cnt += appendIfChanged(etSurname, originalUser.name, { modifiedUser.name = it })
        cnt += appendIfChanged(etName, originalUser.surname, { modifiedUser.surname = it })
        cnt += appendIfChanged(etPatronymic, originalUser.patronymic, { modifiedUser.patronymic = it })
        cnt += appendIfChanged(etPhoneNumber, originalUser.phoneNumber, { modifiedUser.phoneNumber = it })

        if(cnt != 0){
            val editResult = session.editUserInfoAsync(modifiedUser)
            if(eh.handle(editResult)) {
                return
            }
        }

        afterSuccessfulEdit()
    }

    private fun initControls(user: User) {
        etSurname.setText(user.surname)
        etName.setText(user.name)
        etPatronymic.setText(user.patronymic)
        etPhoneNumber.setPhoneNumber(user.phoneNumber)
    }

    private fun appendIfChanged(et: EditText, originalText: String?, appendFunc: (String) -> Unit) : Int {
        val text = et.text.toString()
        if(originalText == null) {
            if(text.isNotBlank()) {
                appendFunc(text)
                return 1
            }
        }
        else if(text != originalText) {
            appendFunc(text)
            return 1
        }
        return 0
    }
}