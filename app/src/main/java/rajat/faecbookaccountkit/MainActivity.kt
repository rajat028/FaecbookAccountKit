package rajat.faecbookaccountkit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import android.view.View
import java.security.MessageDigest
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.ui.AccountKitConfiguration
import android.content.Intent
import android.widget.Toast
import android.R.attr.data
import android.R.attr.data
import com.facebook.accountkit.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var APP_REQUEST_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val info = packageManager.getPackageInfo("rajat.faecbookaccountkit",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {

        }

        phoneLogin(txtLogin)
    }

    fun phoneLogin(view: View) {
        val intent = Intent(this, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN) // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build())
        startActivityForResult(intent, APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === APP_REQUEST_CODE) { // confirm that this response matches your request
            val loginResult = data.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            val toastMessage: String
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError()!!.getErrorType().getMessage()
                Log.e("Message  = ",toastMessage)
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled"
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken()!!.getAccountId()
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode()!!.substring(0, 10))
                }
                Log.e("Message  = ",toastMessage)
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText( this,toastMessage,Toast.LENGTH_LONG).show()

            AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
                override fun onSuccess(account: Account) {
                    val accountKitId = account.id
                    val phoneNumber = account.phoneNumber
                    if (phoneNumber != null) {
                        val phoneNumberString = phoneNumber.toString()
                    }
                    val email = account.email
                }
                override fun onError(error: AccountKitError) {
                    // Handle Error
                }
            })


        }
    }

}
