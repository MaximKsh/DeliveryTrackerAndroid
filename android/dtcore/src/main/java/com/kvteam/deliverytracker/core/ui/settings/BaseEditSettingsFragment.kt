package com.kvteam.deliverytracker.core.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.async.invokeAsync
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.common.ILocalizationManager
import com.kvteam.deliverytracker.core.models.CodePassword
import com.kvteam.deliverytracker.core.models.User
import com.kvteam.deliverytracker.core.models.getUser
import com.kvteam.deliverytracker.core.roles.Role
import com.kvteam.deliverytracker.core.session.ISession
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerActivity
import com.kvteam.deliverytracker.core.ui.DeliveryTrackerFragment
import com.kvteam.deliverytracker.core.webservice.NetworkResult
import com.kvteam.deliverytracker.core.webservice.viewmodels.AccountResponse
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

    private data class RequestResult (
            val editResult: NetworkResult<AccountResponse>?,
            val changePasswordResult: NetworkResult<AccountResponse>?)

    private val userKey = "USER"

    @Inject
    lateinit var session: ISession

    @Inject
    lateinit var lm: ILocalizationManager

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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarController.disableDropDown()
        toolbarController.setToolbarTitle("Profile")

        val args = arguments
        val user = if(args == null) {
            val user = session.getUser()
            val bundle = Bundle()
            bundle.putSerializable(userKey, user)
            arguments = bundle
            user
        } else {
            args.getSerializable(userKey) as User
        }

        initControls(user)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_settings -> editSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_base_edit_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun editSettings() {
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

        invokeAsync({
            var editResult: NetworkResult<AccountResponse>? = null
            if(modifiedUser != null) {
                editResult = session.editUserInfo(modifiedUser)
            }
            var changePasswordResult: NetworkResult<AccountResponse>? = null
            if(needChangePassword) {
                changePasswordResult = session.changePassword(
                        CodePassword(password = oldPassword),
                        CodePassword(password = newPassword))
            }
            RequestResult(editResult, changePasswordResult)
        }, {
            val (editResult, changePasswordResult) = it
            if (editResult != null) {
                if(!editResult.success) {
                    showToast("Edit did not successful.")
                    return@invokeAsync
                }
                if(editResult.entity == null || !editResult.entity.errors.isEmpty()) {
                    showToast("Edit errors: ${editResult.entity?.errors?.joinToString { it.message } ?: EMPTY_STRING}")
                    return@invokeAsync
                }
            }
            if (changePasswordResult != null) {
                if(!changePasswordResult.success) {
                    showToast("Change password did not successful.")
                    return@invokeAsync
                }
                if(changePasswordResult.entity?.errors?.isNotEmpty() == true) {
                    showToast("Change password errors: ${changePasswordResult.entity.errors.joinToString { it.message }}")
                    return@invokeAsync
                }
            }
            afterSuccessfulEdit()
        })
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