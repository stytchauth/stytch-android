package com.stytch.sdk.common.dfp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

internal class ActivityProvider(
    application: Application,
) : Application.ActivityLifecycleCallbacks {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _currentActivity = WeakReference<Activity>(null)
    internal val currentActivity: Activity?
        get() = _currentActivity.get()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        bundle: Bundle?,
    ) {
        // noop
    }

    override fun onActivityStarted(activity: Activity) {
        // noop
    }

    override fun onActivityResumed(activity: Activity) {
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // noop
    }

    override fun onActivityStopped(activity: Activity) {
        // noop
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) {
        // noop
    }

    override fun onActivityDestroyed(activity: Activity) {
        // noop
    }
}
