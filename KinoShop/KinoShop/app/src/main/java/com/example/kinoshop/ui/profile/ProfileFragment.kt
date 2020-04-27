package com.example.kinoshop.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.kinoshop.*
import com.example.kinoshop.api.Api
import com.example.kinoshop.model.Account
import com.example.kinoshop.model.SessionToken
import kotlinx.android.synthetic.main.account_layout.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.sign_in_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ProfileFragment : Fragment(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    val api = Api()
    val apiService = api.serviceInitialize()

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

        launch{
            val response = apiService.getRequestTokenCoroutine()
            if (response.isSuccessful){
                val requestToken = response.body()?.requestToken
                requestToken?.let { auth(it) }
            }

        }
    }

    private fun auth(reqToken: String) {

        launch{
            val response = apiService.authorizationCoroutine(loginET.trimmedText, passwordET.trimmedText, reqToken)
            if (response.isSuccessful){
                if (response.body() == null) {
                    showError()
                }
                val requestResponse = response.body()
                requestResponse?.let {
                    if (it.success) {
                        createSession(reqToken)
                    } else {
                        showError()
                    }
                }
                hideProgressBar()
            }

        }
    }

    private fun createSession(reqToken: String) {
        launch{
            val response = apiService.createSessionCoroutine(reqToken)
            if (response.isSuccessful){
                response.body()?.sessionId?.let {
                    getAccount(it)
                    mainActivity.sessionId = it
                }

            }

        }
    }

    private fun getAccount(sessionId: String) {
        launch{
            val response = apiService.getAccountCoroutine(sessionId)
            if (response.isSuccessful){
                val responseAccount = response.body()
                responseAccount?.let { account ->
                    mainActivity.account = Account(
                        id = account.id,
                        name = account.name
                    )
                    mainActivity.isSignIn = true
                    userName?.let {
                        it.text = account.name
                    }
                    signInFrame?.let { it.visibility = View.GONE }
                    accountFrame?.let { it.visibility = View.VISIBLE }
                }
                hideProgressBar()

            }

        }
    }

    private fun hideProgressBar() {
        progressBar?.let {
            it.visibility = View.INVISIBLE
        }
    }

    private fun showError() {
        loginTIL?.let {
            it.showError(R.string.check_auth_data)
        }
        passwordTIL?.let {
            it.showError(R.string.check_auth_data)
        }
    }
}
