package com.stytch.sdk.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchUI
import com.stytch.sdk.helpers.dp
import com.stytch.sdk.helpers.hideKeyboard
import com.stytch.sdk.views.StytchButton
import com.stytch.sdk.views.StytchEditText
import com.stytch.sdk.views.StytchTextView
import com.stytch.sdk.views.StytchWaterMarkView


/**
 * A simple [Fragment] subclass.
 * Use the [StytchLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StytchLoginFragment : Fragment() {

    private var titleTextView: StytchTextView? = null
    private var descriptionTextView: StytchTextView? = null
    private var emailEditText: StytchEditText? = null
    private var actionButton: StytchButton? = null
    private var waterMarkView: StytchWaterMarkView? = null
    private var loadingIndicator: View? = null

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stytch_login, container, false)

        view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                Stytch.instance.config.customization.backgroundId
            )
        )
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        createViews(view.findViewById(R.id.holderLayout))
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        observe()

    }

    private fun observe() {
        observeState()
        observeLoading()
        observeErrors()
        observeLoggedInSuccessfullyLiveData()
    }

    private fun createViews(holder: LinearLayoutCompat) {
        createTitleTextView()
        createDescriptionTextView()
        createEditText()
        createButton()
        createWaterMarkView()

        addViews(holder)
    }

    private fun createWaterMarkView() {
        if (Stytch.instance.config.customization.showBrandLogo) {
            waterMarkView = StytchWaterMarkView(requireContext())
        }
    }

    private fun createButton() {
        actionButton = StytchButton(requireContext()).apply {
            setText(R.string.stytch_login_button_title)
            setCustomization(Stytch.instance.config.customization.buttonTextCustomization)
        }
    }

    private fun createEditText() {
        emailEditText = StytchEditText(requireContext()).apply {
            setHintCustomization(Stytch.instance.config.customization.editHintCustomization)
            setTextCustomization(Stytch.instance.config.customization.editTextCustomization)
            updateHint(R.string.stytch_login_email_hint)
        }
    }

    private fun createDescriptionTextView() {
        descriptionTextView = StytchTextView(requireContext()).apply {
            setText(R.string.stytch_login_description)
            setCustomization(Stytch.instance.config.customization.subtitleCustomization)
        }
    }

    private fun createTitleTextView() {
        titleTextView = StytchTextView(requireContext()).apply {
            setText(R.string.stytch_login_title)
            setCustomization(Stytch.instance.config.customization.titleCustomization)
        }
    }

    private fun addViews(holder: LinearLayoutCompat) {
        var layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 32.dp.toInt()
            marginStart = 24.dp.toInt()
            marginEnd = 24.dp.toInt()
        }
        holder.addView(titleTextView, layoutParams)

        layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 24.dp.toInt()
            marginStart = 24.dp.toInt()
            marginEnd = 24.dp.toInt()
        }
        holder.addView(descriptionTextView, layoutParams)
        holder.addView(emailEditText, layoutParams)
        holder.addView(actionButton, layoutParams)

        waterMarkView?.let { waterMarkView ->
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 0.dp.toInt()
                marginStart = 24.dp.toInt()
                marginEnd = 24.dp.toInt()
            }
            holder.addView(waterMarkView, layoutParams)
        }

    }


    private fun setupViewByState(state: LoginViewModel.State) {
        when (state) {
            LoginViewModel.State.Login -> {
                animateTextView(titleTextView, getString(R.string.stytch_login_title))
                animateTextView(descriptionTextView, getString(R.string.stytch_login_description))
                actionButton?.setText(R.string.stytch_login_button_title)
                emailEditText?.visibility = View.VISIBLE
                actionButton?.setOnClickListener {
                    viewModel.signInClicked(emailEditText?.text?.toString())
                }
            }
            LoginViewModel.State.WaitingVerification -> {
                hideKeyboard()
                animateTextView(
                    titleTextView,
                    getString(R.string.stytch_login_waitnig_verification_title)
                )
                animateTextView(
                    descriptionTextView,
                    getString(R.string.stytch_login_waitnig_verification_description)
                )
                actionButton?.setText(R.string.stytch_login_waitnig_verification_button_title)
                emailEditText?.visibility = View.GONE
                actionButton?.setOnClickListener {
                    viewModel.resendClicked()
                }
            }
        }
    }

    private fun animateTextView(view: TextView?, newString: String?) {
        if (view == null) return
        view.text = newString
    }

    private fun observeState() {
        viewModel.stateLiveData.observe(viewLifecycleOwner, { state ->
            setupViewByState(state)
        })
    }

    private fun observeErrors() {
        viewModel.errorManager.observeErrors(this, viewLifecycleOwner, TAG)
    }

    private fun observeLoading() {
        viewModel.loadingLiveData.observe(viewLifecycleOwner, { showLoading ->
            loadingIndicator?.visibility = if (showLoading) View.VISIBLE else View.GONE
        })
    }

    private fun observeLoggedInSuccessfullyLiveData() {
        viewModel.loggedInSuccessfullyLiveData.observe(viewLifecycleOwner, { event ->
            event.getEventNotHandled()?.let {
//                TODO: return success status from SDK
            }
        })
    }

    fun onNewIntent(uri: Uri) {
        viewModel.verifyToken(uri)
    }


    companion object {

        private const val TAG = "StytchLoginFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StytchLoginFragment.
         */

        @JvmStatic
        fun newInstance() =
            StytchLoginFragment().apply {
                arguments = Bundle().apply {
                }
            }


    }
}