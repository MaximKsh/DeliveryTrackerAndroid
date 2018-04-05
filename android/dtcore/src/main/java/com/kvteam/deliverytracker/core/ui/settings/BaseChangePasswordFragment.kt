package com.kvteam.deliverytracker.core.ui.settings

import android.os.Bundle
import android.view.*
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_change_password.*
import kotlinx.android.synthetic.main.fragment_base_settings.*
import javax.inject.Inject

abstract class BaseChangePasswordFragment  : DeliveryTrackerFragment() {
    private val scrollPosKey = "scrollPosition"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

    lateinit var awesomeValidation: AwesomeValidation

    protected abstract fun onSuccessChangePassword()


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_change_password, container, false)
    }

    override fun configureToolbar(toolbar: ToolbarController) {
        super.configureToolbar(toolbar)
        toolbarController.setToolbarTitle(lm.getString(R.string.Core_ChangePasswordTooltipHeader))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val position = savedInstanceState?.getInt(scrollPosKey)
        if (position != null) {
            svMainScrollView.scrollY = position
        }

        awesomeValidation = AwesomeValidation(ValidationStyle.UNDERLABEL)
        awesomeValidation.addValidation(
                etOldPassword, RegexTemplate.NOT_EMPTY, getString(R.string.Core_EnterOldPasswordValidationError))
        awesomeValidation.addValidation(
                etNewPassword,
                { it?.length ?: 0 > 3 },
                getString(R.string.Core_EnterNewPasswordValidationError)
        )
        awesomeValidation.addValidation(
                etConfirmPassword, etNewPassword,  getString(R.string.Core_EnterOldPasswordValidationError))
        awesomeValidation.setContext(this.dtActivity)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(scrollPosKey, svMainScrollView.scrollY)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = launchUI ({
        when (item.itemId) {
            R.id.action_done -> changePassword()
        }
    }, {
        super.onOptionsItemSelected(item)
    })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun changePassword() = launchUI {
        if(awesomeValidation.validate()) {
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val changePasswordResult = session.changePasswordAsync(
                    CodePassword(password = oldPassword),
                    CodePassword(password = newPassword))
            if(eh.handle(changePasswordResult)) {
                return@launchUI
            }

            onSuccessChangePassword()
        }
    }

}