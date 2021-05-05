package com.stytch.sdk

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.RuntimeException

public class StytchUI private constructor() {

    public var uiListener: StytchUIListener? = null

    // TODO ??
    public var uiCustomization: StytchUICustomization = StytchUICustomization()
		set(value) {
			try {
				Stytch.instance.config!!.uiCustomization = value
			} catch (ex: Exception) {
				throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
			}
        }

    public interface StytchUIListener {
        public fun onEvent(event: StytchEvent) {}
        public fun onSuccess(result: StytchResult)
        public fun onFailure()
    }

    public companion object {
        private const val ACTIVITY_REQUEST_CODE = 674

        public val instance: StytchUI = StytchUI()
    }
}

public class StytchUICustomization {
    public var buttonCornerRadius: Float = 5.dp
    public var buttonBackgroundColor: StytchColor = StytchColor.fromColorId(R.color.buttonBg)

    public var inputBackgroundColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundColor)
    public var inputBackgroundBorderColor: StytchColor =
        StytchColor.fromColorId(R.color.editTextBackgroundBorderColor)
    public var inputCornerRadius: Float = 5.dp

    public var backgroundColor: StytchColor = StytchColor.fromColorId(R.color.colorBackground)
    public var showBrandLogo: Boolean = true

    public var showTitle: Boolean = true
    public var showSubtitle: Boolean = true

    public var titleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.titleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 30.dp
    }

    public var subtitleStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.subtitleTextColor)
        font = Typeface.create(null as Typeface?, Typeface.NORMAL)
        size = 16.dp
    }

    public var inputTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editTextColor)
        size = 16.dp
    }

    public var inputHintStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.editHintTextColor)
        size = 16.dp
    }

    public var buttonTextStyle: StytchTextStyle = StytchTextStyle().apply {
        color = StytchColor.fromColorId(R.color.buttonTextColor)
        font = Typeface.create(null as Typeface?, Typeface.BOLD)
        size = 16.dp
    }

}

public class StytchColor private constructor(
    private val colorId: Int? = null,
    private val color: Int? = null,
) {

    @ColorInt
    public fun getColor(context: Context): Int {
        if (colorId != null) {
            return ContextCompat.getColor(context, colorId)
        }
        if (color != null) {
            return color
        }

        throw RuntimeException("StytchColor bad status. Please check color initialization.")
    }

    public companion object {
        public fun fromColorId(@ColorRes colorId: Int): StytchColor {
            return StytchColor(colorId = colorId)
        }

        public fun fromColor(@ColorInt color: Int): StytchColor {
            return StytchColor(color = color)
        }
    }
}

public class StytchTextStyle {
    public var size: Float = 10.dp
    public var color: StytchColor = StytchColor.fromColorId(R.color.editTextColor)
    public var font: Typeface? = null
}

internal class StytchButton constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle,
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        setTextColor(ContextCompat.getColor(context, R.color.buttonTextColor))
        setBackgroundResource(R.drawable.stytch_button_bg_rounded)
        isAllCaps = false
        Stytch.instance.config?.let { config ->
            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled),
            )

            val color = config.uiCustomization.buttonBackgroundColor.getColor(context)

            val colors = intArrayOf(
                color,
                color
            )
            backgroundTintList = ColorStateList(states, colors);
        }

        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                config.uiCustomization.buttonBackgroundColor.getColor(
                    context
                ),
                config.uiCustomization.buttonCornerRadius
            )
        }

    }

    fun setCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        typeface = style.font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
    }

    private fun getBackgroundShape(backgroundColor: Int, borderRadius: Float): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }

}

internal class StytchEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle,
    forEmailAddress: Boolean = true,
) : AppCompatEditText(context, attributeSet, defStyleAttr) {

    private var hintStyle: StytchTextStyle? = null

    init {
        setPadding(16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt(), 16.dp.toInt())
        Stytch.instance.config?.let { config ->
            background = getBackgroundShape(
                config.uiCustomization.inputBackgroundColor.getColor(context),
                config.uiCustomization.inputBackgroundBorderColor.getColor(context),
                config.uiCustomization.inputCornerRadius,
                1.dp.toInt(),
            )
        }
        if (forEmailAddress) {
            inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    fun setHintCustomization(style: StytchTextStyle) {
        hintStyle = style
        setHintTextColor(style.color.getColor(context))
    }

    fun setTextCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        typeface = style.font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
        setCursorDrawableColor(style.color.getColor(context))
    }

    fun updateHint(textId: Int) {
        val text = context.getString(textId)
        val spannableText = SpannableString(text)

        hintStyle?.let { customization ->

            customization.font?.let { font ->
                spannableText.setSpan(
                    CustomTypefaceSpan(null, font),
                    0,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }

            spannableText.setSpan(
                AbsoluteSizeSpan(customization.size.toInt(), false),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )

        }

        hint = spannableText
    }

    fun setCursorDrawableColor(@ColorInt color: Int) {
//        TODO: change cursor color
    }

    private fun getBackgroundShape(
        backgroundColor: Int,
        borderColor: Int,
        borderRadius: Float,
        borderWidth: Int
    ): Drawable? {
        val drawable = GradientDrawable()
        drawable.cornerRadius = borderRadius
        drawable.setStroke(borderWidth, borderColor)
        drawable.colors = intArrayOf(backgroundColor, backgroundColor)
        return drawable
    }

}

internal class StytchTextView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setCustomization(style: StytchTextStyle) {
        setTextColor(style.color.getColor(context))
        typeface = style.font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, style.size)
    }

}

internal class StytchWaterMarkView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var poweredByText: StytchTextView
    lateinit var poweredByImage: AppCompatImageView

    init {
        createTextView()
        createLogo()

        addViews()
    }

    private fun addViews() {
        addView(poweredByText)
        addView(poweredByImage)

        ConstraintSet().apply {

            constrainHeight(poweredByText.id, ConstraintSet.WRAP_CONTENT)
            constrainWidth(poweredByText.id, ConstraintSet.WRAP_CONTENT)

            constrainHeight(poweredByImage.id, 56.dp.toInt())
            constrainWidth(poweredByImage.id, 56.dp.toInt())

            connect(
                poweredByText.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
            )
            connect(
                poweredByText.id,
                ConstraintSet.END,
                poweredByImage.id,
                ConstraintSet.START,
                8.dp.toInt(),
            )
            centerVertically(poweredByText.id, ConstraintSet.PARENT_ID)

            connect(poweredByImage.id, ConstraintSet.START, poweredByText.id, ConstraintSet.END)
            connect(
                poweredByImage.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
            )
            centerVertically(poweredByImage.id, ConstraintSet.PARENT_ID)

            createHorizontalChain(
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                intArrayOf(poweredByText.id, poweredByImage.id),
                null,
                ConstraintSet.CHAIN_PACKED,
            )

            applyTo(this@StytchWaterMarkView)
        }

    }

    private fun createTextView() {
        poweredByText = StytchTextView(context).apply {
            id = View.generateViewId()
            setText(R.string.stytch_watermark_powered_by)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.poweredByColor))
        }
    }

    private fun createLogo() {
        Stytch.instance.config?.let { config ->
            val color = config.uiCustomization.backgroundColor.getColor(context).invertedWhiteBlack()

            poweredByImage = AppCompatImageView(context).apply {
                id = View.generateViewId()
                setImageResource(R.drawable.ic_stytch_logo)
            }

            poweredByImage.imageTintList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color))
        }
    }

}

public class StytchMainActivity : AppCompatActivity() {

    private var loginFragment: StytchLoginFragment = StytchLoginFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stytch_main)
        showFragment()
    }

    private fun showFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, loginFragment, LOGIN_FRAGMENT_TAG)
            commit()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        val action: String? = intent?.action
        val data = intent?.data ?: return

        if (action == Intent.ACTION_VIEW) {
            loginFragment.onNewIntent(data)
        }
        super.onNewIntent(intent)
    }

    public companion object {
        private const val TAG = "StytchMainActivity"
        private const val LOGIN_FRAGMENT_TAG = "loginFragment"
    }
}

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

        Stytch.instance.config?.let {
            view.setBackgroundColor(
                it.uiCustomization.backgroundColor.getColor(requireContext())

            )
        }

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        createViews(view.findViewById(R.id.holderLayout))
        checkConfig()
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
        if (Stytch.instance.config?.uiCustomization?.showBrandLogo == true) {
            waterMarkView = StytchWaterMarkView(requireContext())
        }
    }

    private fun createButton() {
        actionButton = StytchButton(requireContext()).apply {
            setText(R.string.stytch_login_button_title)
            Stytch.instance.config?.let { config ->
                setCustomization(config.uiCustomization.buttonTextStyle)
            }
        }
    }

    private fun createEditText() {
        emailEditText = StytchEditText(requireContext()).apply {
            Stytch.instance.config?.let { config ->
                setHintCustomization(config.uiCustomization.inputHintStyle)
                setTextCustomization(config.uiCustomization.inputTextStyle)
            }

            updateHint(R.string.stytch_login_email_hint)
        }
    }

    private fun createDescriptionTextView() {
        if (Stytch.instance.config?.uiCustomization?.showSubtitle == true) {
            descriptionTextView = StytchTextView(requireContext()).apply {
                setText(R.string.stytch_login_description)
                Stytch.instance.config?.let { config ->
                    setCustomization(config.uiCustomization.subtitleStyle)
                }
            }
        } else {
            descriptionTextView = null
        }
    }

    private fun createTitleTextView() {
        if (Stytch.instance.config?.uiCustomization?.showTitle == true) {
            titleTextView = StytchTextView(requireContext()).apply {
                setText(R.string.stytch_login_title)
                Stytch.instance.config?.let { config ->
                    setCustomization(config.uiCustomization.titleStyle)
                }
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
        viewModel.closeLiveData.observe(viewLifecycleOwner, { event ->
            event.getEventNotHandled()?.let {
                if (it) {
                    activity?.finish()
                }
            }
        })
    }

    private fun observeLoading() {
        Stytch.instance.config?.let { config ->
            val color =
                config.uiCustomization.backgroundColor.getColor(requireContext()).invertedWhiteBlack()

            loadingIndicator?.indeterminateTintList =
                ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color))
        }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, { showLoading ->
            loadingIndicator?.visibility = if (showLoading) View.VISIBLE else View.GONE
        })
    }

    fun onNewIntent(uri: Uri) {
        viewModel.verifyToken(uri)
    }

    private fun checkConfig() {
        if (Stytch.instance.config == null) {
            StytchUI.instance.uiListener?.onFailure()
            activity?.finish()
        }
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
        fun newInstance() = StytchLoginFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}

internal class LoginViewModel : ViewModel() {

    val stateLiveData = MutableLiveData<State>().apply { value = State.Login }
    val loadingLiveData = MutableLiveData<Boolean>().apply { value = false }
    val errorManager = ErrorManager()
    val closeLiveData = MutableLiveData<Event<Boolean>>()

    val stytchListener = object : Stytch.StytchListener {
        override fun onSuccess(result: StytchResult) {
            loadingLiveData.value = false
            StytchUI.instance.uiListener?.onSuccess(result)
            closeLiveData.value = Event(true)
        }

        override fun onFailure(error: StytchError) {
            loadingLiveData.value = false
            when (error) {
                StytchError.InvalidMagicToken,
                StytchError.Connection -> {
                    errorManager.showError(error.messageId)
                }
                StytchError.Unknown,
                StytchError.InvalidEmail -> {
                    errorManager.showError(error.messageId)
                    stateLiveData.value = State.Login
                }
                StytchError.InvalidConfiguration -> {
                    loadingLiveData.value = true
                    StytchUI.instance.uiListener?.onFailure()
                    closeLiveData.value = Event(true)
                }
            }
        }

        override fun onMagicLinkSent(email: String) {
            LoggerLocal.d(TAG, "sendMagicLink success")
            loadingLiveData.value = false
        }
    }.apply {
        Stytch.instance.listener = this
    }

    fun signInClicked(email: String?) {
        LoggerLocal.d(TAG, "checkEmail: $email")
        if (email.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorManager.showError(R.string.stytch_error_invalid_input)
            return
        }
        loadingLiveData.value = true
        stateLiveData.value = State.WaitingVerification
        Stytch.instance.login(email)
    }

    fun resendClicked() {
        stateLiveData.value = State.Login
    }

    fun verifyToken(uri: Uri) {
        loadingLiveData.value = true
        Stytch.instance.handleDeepLink(uri)
    }

    enum class State {
        Login,
        WaitingVerification
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
