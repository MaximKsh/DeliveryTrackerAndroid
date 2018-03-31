package com.kvteam.deliverytracker.core.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.launchUI
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.ui.errorhandling.IErrorHandler
import com.kvteam.deliverytracker.core.ui.toolbar.ToolbarController
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_base_edit_settings.*
import javax.inject.Inject

abstract class BaseEditSettingsFragment : DeliveryTrackerFragment() {
    private data class PasswordChangeContext(
            val oldPassword: String,
            val newPassword: String,
            val needChangePassword: Boolean,
            val correct: Boolean)

    private data class ModifyUserContext(
            val modifiedUser: User?,
            val correct: Boolean)

    private val userKey = "USER"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

    @Inject
    lateinit var eh: IErrorHandler

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
        toolbarController.setToolbarTitle("Profile")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
        val user = if(args?.containsKey(userKey) == true) {
            args.getSerializable(userKey) as User
        } else {
            val user = session.user!!
            val bundle = Bundle()
            bundle.putSerializable(userKey, user)
            arguments = bundle
            user
        }

        initControls(user)
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
        val (modifiedUser, modifiedUserCorrect) =
                prepareModifiedUser()
        val (oldPassword, newPassword, needChangePassword, passwordCorrect) =
                preparePasswordChange()

        if(modifiedUser == null
                && modifiedUserCorrect
                && !needChangePassword
                && passwordCorrect) {
            afterSuccessfulEdit()
            return
        }

        if(!modifiedUserCorrect || !passwordCorrect) {
            return
        }


        if(modifiedUser != null) {
            val editResult = session.editUserInfoAsync(modifiedUser)
            if(eh.handle(editResult)) {
                return
            }
        }

        if(needChangePassword) {
            val changePasswordResult = session.changePasswordAsync(
                    CodePassword(password = oldPassword),
                    CodePassword(password = newPassword))
            if(eh.handle(changePasswordResult)) {
                return
            }
        }

        afterSuccessfulEdit()
    }

    private fun initControls(user: User) {
        tvHeader.text = "${Role.getCaption(user.role, lm)} (${user.code})"
        etNameField.setText(user.name)
        etSurnameField.setText(user.surname)
        etPatronymicField.setText(user.patronymic)
        etPhoneNumberField.setText(user.phoneNumber)

        val surname = user.surname
        val name = user.name
        val materialAvatarDefault = TextDrawable.builder()
                .buildRound((name?.get(0)?.toString() ?: EMPTY_STRING) + (surname?.get(0)?.toString() ?: EMPTY_STRING), Color.LTGRAY)
        ivUserAvatar.setImageDrawable(materialAvatarDefault)
    }

    private fun prepareModifiedUser() : ModifyUserContext {
        val args = arguments ?: return ModifyUserContext(null, true)
        val modifiedUser = User()
        val originalUser = args.getSerializable(userKey) as User

        modifiedUser.id = originalUser.id
        modifiedUser.instanceId = originalUser.instanceId

        var cnt = 0
        cnt += appendIfChanged(etNameField, originalUser.name, { modifiedUser.name = it })
        cnt += appendIfChanged(etSurnameField, originalUser.surname, { modifiedUser.surname = it })
        cnt += appendIfChanged(etPatronymicField, originalUser.patronymic, { modifiedUser.patronymic = it })
        cnt += appendIfChanged(etPhoneNumberField, originalUser.phoneNumber, { modifiedUser.phoneNumber = it })

        return if(cnt != 0) ModifyUserContext(modifiedUser, true)
            else ModifyUserContext(null, true)
    }

    private fun preparePasswordChange() : PasswordChangeContext {
        if(etPasswordField.text.isBlank()) {
            return PasswordChangeContext(
                    EMPTY_STRING, EMPTY_STRING, false, true)
        }
        if(etPasswordField.text.toString() != etConfirmPasswordField.text.toString()) {
            showToast("Password and confirm password is differ.")
            return PasswordChangeContext(
                    EMPTY_STRING, EMPTY_STRING, true, false)
        }
        if(etOldPasswordField.text.isBlank()) {
            showToast("Old password is empty.")
            return PasswordChangeContext(
                    EMPTY_STRING, EMPTY_STRING, true, false)
        }

        return PasswordChangeContext(
                etOldPasswordField.text.toString(),
                etPasswordField.text.toString(),
                true,
                true)
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

    private fun showToast(text: String) {
        Toast
                .makeText(activity, text, Toast.LENGTH_LONG)
                .show()
    }

}