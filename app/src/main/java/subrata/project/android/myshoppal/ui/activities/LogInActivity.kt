package subrata.project.android.myshoppal.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import subrata.project.android.myshoppal.R
import subrata.project.android.myshoppal.databinding.ActivityLogInBinding
import subrata.project.android.myshoppal.models.User
import subrata.project.android.myshoppal.utils.Constants
import subrata.project.android.myshoppal.utils.FirestoreClass

class LogInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        binding.tvRegister.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LogInActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)

                }

                R.id.btn_login -> {
                    loginRegisteredUSer()
                }

                R.id.tv_register -> {
                    val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loginRegisteredUSer() {

        if (validateLoginDetails()) {
            showProgressDialog("Please Wait")

            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LogInActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        if(user.profileCompleted == 0) {
            val intent = Intent(this@LogInActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LogInActivity, DashboardActivity::class.java))
        }
        finish()
    }
}