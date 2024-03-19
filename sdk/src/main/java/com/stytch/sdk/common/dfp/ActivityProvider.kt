package com.stytch.sdk.common.dfp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

internal class ActivityProvider(application: Application) : Application.ActivityLifecycleCallbacks {
    private var _currentActivity = WeakReference<Activity>(null)
    internal val currentActivity: Activity?
        get() = _currentActivity.get()

    private var lastResumedActivityName: String? = null

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        bundle: Bundle?,
    ) {
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        lastResumedActivityName = activity.localClassName
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (lastResumedActivityName != activity.localClassName) return
        _currentActivity = WeakReference(null)
    }

    override fun onActivityStopped(activity: Activity) {
        if (lastResumedActivityName != activity.localClassName) return
        _currentActivity = WeakReference(null)
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) {
        // noop
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (lastResumedActivityName != activity.localClassName) return
        _currentActivity = WeakReference(null)
    }
}
