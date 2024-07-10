package com.stytch.sdk.common.utils

import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UtilsImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val storageHelper: StorageHelper,
) : Utils {
    override suspend fun getPKCEPair(): PKCECodePair =
        withContext(dispatchers.io) {
            storageHelper.getPKCECodePair()
        }

    override fun getPKCEPair(callback: (PKCECodePair) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(getPKCEPair())
        }
    }
}
