package com.example.kinoshop.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.kinoshop.*
import com.example.kinoshop.model.Account
import com.example.kinoshop.model.AuthResponse
import com.example.kinoshop.model.RequestToken
import com.example.kinoshop.model.SessionToken
import kotlinx.android.synthetic.main.account_layout.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.sign_in_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity
        if (mainActivity.isSignIn) {
            accountFrame.visibility = View.VISIBLE
            userName.text = mainActivity.account?.name
        } else {
            signInFrame.visibility = View.VISIBLE
        }
        signInBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            runAuthorization()
        }
        loginET.addTextChangedListener { loginTIL.hideError() }
        passwordET.addTextChangedListener { passwordTIL.hideError() }
    }

    private fun runAuthorization() {
        mainActivity.apiService.getRequestToken().enqueue(object : Callback<RequestToken> {
            override fun onFailure(call: Call<RequestToken>, t: Throwable) {
                hideProgressBar()
            }

            override fun onResponse(call: Call<RequestToken>, response: Response<RequestToken>) {
                val requestToken = response.body()?.requestToken
                requestToken?.let { auth(it) }
            }
        })
    }

    private fun auth(reqToken: String) {
        mainActivity.apiService.authorization(loginET.trimmedText, passwordET.trimmedText, reqToken)
            .enqueue(object :
                Callback<AuthResponse> {
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    loginTIL.showError(R.string.check_auth_data)
                    passwordTIL.showError(R.string.check_auth_data)
                    hideProgressBar()
                }

                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.body() == null) {
                        loginTIL.showError(R.string.check_auth_data)
                        passwordTIL.showError(R.string.check_auth_data)
                    }
                    val requestResponse = response.body()
                    requestResponse?.let {
                        if (it.success) {
                            createSession(reqToken)
                        } else {
                            loginTIL.showError(R.string.check_auth_data)
                            passwordTIL.showError(R.string.check_auth_data)
                        }
                    }
                    hideProgressBar()
                }
            })
    }

    private fun createSession(reqToken: String) {
        mainActivity.apiService.createSession(reqToken).enqueue(object : Callback<SessionToken> {
            override fun onFailure(call: Call<SessionToken>, t: Throwable) {
                loginTIL.showError(R.string.check_auth_data)
                passwordTIL.showError(R.string.check_auth_data)
                hideProgressBar()
            }

            override fun onResponse(call: Call<SessionToken>, response: Response<SessionToken>) {
                response.body()?.sessionId?.let {
                    getAccount(it)
                    mainActivity.sessionId = it
                }
            }
        })
    }

    private fun getAccount(sessionId: String) {
        mainActivity.apiService.getAccount(sessionId).enqueue(object : Callback<Account> {
            override fun onFailure(call: Call<Account>, t: Throwable) {
                loginTIL.showError(R.string.check_auth_data)
                passwordTIL.showError(R.string.check_auth_data)
                hideProgressBar()
            }

            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                val responseAccount = response.body()
                responseAccount?.let { account ->
                    mainActivity.account = Account(
                        id = account.id,
                        name = account.name
                    )
                    mainActivity.isSignIn = true
                    userName.text = account.name
                    signInFrame.visibility = View.GONE
                    accountFrame.visibility = View.VISIBLE
                }
                hideProgressBar()
            }
        })
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

}
