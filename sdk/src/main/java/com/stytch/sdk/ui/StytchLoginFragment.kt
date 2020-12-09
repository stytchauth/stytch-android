package com.stytch.sdk.ui

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.stytch.sdk.R
import com.stytch.sdk.Stytch
import com.stytch.sdk.helpers.dp
import com.stytch.sdk.helpers.hideKeyboard
import com.stytch.sdk.helpers.invertedWhiteBlack
import com.stytch.sdk.views.StytchButton
import com.stytch.sdk.views.StytchEditText
import com.stytch.sdk.views.StytchTextView
import com.stytch.sdk.views.StytchWaterMarkView


/**
 * A simple [Fragment] subclass.
 * Use the [StytchLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
internal class StytchLoginFragment : Fragment() {

    private var titleTextView: StytchTextView? = null
    private var descriptionTextView: StytchTextView? = null
    private var emailEditText: StytchEditText? = null
    private var actionButton: StytchButton? = null
    private var waterMarkView: StytchWaterMarkView? = null
    private var loadingIndicator: ProgressBar? = null

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
                Stytch.instance.config.uiCustomization.backgroundId
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
        if (Stytch.instance.config.uiCustomization.showBrandLogo) {
            waterMarkView = StytchWaterMarkView(requireContext())
        }
    }

    private fun createButton() {
        actionButton = StytchButton(requireContext()).apply {
            setText(R.string.stytch_login_button_title)
            setCustomization(Stytch.instance.config.uiCustomization.buttonTextStyle)
        }
    }

    private fun createEditText() {
        emailEditText = StytchEditText(requireContext()).apply {
            setHintCustomization(Stytch.instance.config.uiCustomization.inputHintStyle)
            setTextCustomization(Stytch.instance.config.uiCustomization.inputTextStyle)
            updateHint(R.string.stytch_login_email_hint)
        }
    }

    private fun createDescriptionTextView() {
        if (Stytch.instance.config.uiCustomization.showSubtitle) {
            descriptionTextView = StytchTextView(requireContext()).apply {
                setText(R.string.stytch_login_description)
                setCustomization(Stytch.instance.config.uiCustomization.subtitleStyle)
            }
        } else {
            descriptionTextView = null
        }
    }

    private fun createTitleTextView() {
        if (Stytch.instance.config.uiCustomization.showTitle) {
            titleTextView = StytchTextView(requireContext()).apply {
                setText(R.string.stytch_login_title)
                setCustomization(Stytch.instance.config.uiCustomization.titleStyle)
            }
        } else {
            titleTextView = null
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
        titleTextView?.let { titleTextView ->
            holder.addView(titleTextView, layoutParams)
        }

        layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 24.dp.toInt()
            marginStart = 24.dp.toInt()
            marginEnd = 24.dp.toInt()
        }
        descriptionTextView?.let { descriptionTextView ->
            holder.addView(descriptionTextView, layoutParams)
        }
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
                    getString(
                        R.string.stytch_login_waiting_verification_title,
                        emailEditText?.text?.toString()
                    )
                )
                animateTextView(
                    descriptionTextView,
                    getString(R.string.stytch_login_waiting_verification_description)
                )
                actionButton?.setText(R.string.stytch_login_waiting_verification_button_title)
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
        viewModel.closeLiveData.observe(viewLifecycleOwner, {event ->
            event.getEventNotHandled()?.let {
                if(it){
                    activity?.finish()
                }
            }
        })
    }

    private fun observeLoading() {
        val color = ContextCompat.getColor(
            requireContext(),
            Stytch.instance.config.uiCustomization.backgroundId
        ).invertedWhiteBlack()

        loadingIndicator?.indeterminateTintList =
            ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color))

        viewModel.loadingLiveData.observe(viewLifecycleOwner, { showLoading ->
            loadingIndicator?.visibility = if (showLoading) View.VISIBLE else View.GONE
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