package com.stytch.sdk.common.sso

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService

internal data class Browser(
    val packageName: String,
    val supportsCustomTabs: Boolean,
)

internal object BrowserSelector {
    private var allBrowsers: List<Browser> = emptyList()
    private const val HTTP = "http"
    private const val HTTPS = "https"

    // An HTTP intent to use for querying for supported browsers
    private val BROWSER_INTENT = Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_BROWSABLE)
        data = Uri.parse("http://test.test/test")
    }

    fun getBestBrowser(context: Context): Browser? =
        getAllBrowsers(context).firstOrNull { it.supportsCustomTabs } ?: allBrowsers.firstOrNull()

    /**
     * Produces a list of all capable browsers found on device, with the users default browser first
     */
    private fun getAllBrowsers(context: Context): List<Browser> = allBrowsers.ifEmpty {
        val browsers = mutableListOf<Browser>()
        val pm = context.packageManager
        val queryFlag = PackageManager.GET_RESOLVED_FILTER or PackageManager.MATCH_ALL
        val defaultBrowserPackage: String? = pm.resolveActivity(BROWSER_INTENT, 0)?.activityInfo?.packageName
        pm.queryIntentActivities(BROWSER_INTENT, queryFlag).forEach { info ->
            // ignore handlers which are not browsers
            if (!info.filter.isRealBrowser()) return@forEach
            try {
                val packageInfo = pm.getPackageInfo(info.activityInfo.packageName, 0)
                val browser = Browser(packageInfo.packageName, supportsCustomTabs(pm, info.activityInfo.packageName))
                if (info.activityInfo.packageName == defaultBrowserPackage) {
                    // if this is the default browser, add it to the beginning of the list
                    browsers.add(0, browser)
                } else {
                    browsers.add(browser)
                }
            } catch (_: PackageManager.NameNotFoundException) {
                // a descriptor cannot be generated without the package info
            }
        }
        browsers.also { allBrowsers = it }
    }

    private fun IntentFilter.isRealBrowser(): Boolean {
        return when {
            // must have supported  action
            !hasAction(Intent.ACTION_VIEW) ||
                // must have supported category
                !hasCategory(Intent.CATEGORY_BROWSABLE) ||
                // must have at least one scheme
                schemesIterator() == null ||
                // must not be restricted to any authorities
                authoritiesIterator() != null ||
                // must support both HTTP and HTTPS.
                !schemesIterator().supportsHttpAndHttps() -> false
            // every check has passed, this is a real browser
            else -> true
        }
    }

    private fun Iterator<String>.supportsHttpAndHttps(): Boolean {
        var supportsHttp = false
        var supportsHttps = false
        forEach {
            if (supportsHttp && supportsHttps) return@forEach
            supportsHttp = supportsHttp || (it == HTTP)
            supportsHttps = supportsHttps || (it == HTTPS)
        }
        return supportsHttp && supportsHttps
    }

    private fun supportsCustomTabs(pm: PackageManager, packageName: String): Boolean {
        val intent = Intent().apply {
            action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            setPackage(packageName)
        }
        return pm.resolveService(intent, 0) != null
    }
}
