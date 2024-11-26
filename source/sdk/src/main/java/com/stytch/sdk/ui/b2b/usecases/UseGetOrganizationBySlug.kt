package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BSearchOrganizationResponseData
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseGetOrganizationBySlug(
    private val scope: CoroutineScope,
    private val dispatch: Dispatch,
    private val request: PerformRequest<B2BSearchOrganizationResponseData>,
) {
    operator fun invoke(organizationSlug: String) {
        scope.launch(Dispatchers.IO) {
            dispatch(SetLoading(true))
            request {
                StytchB2BClient.searchManager.searchOrganization(
                    SearchManager.SearchOrganizationParameters(
                        organizationSlug = organizationSlug,
                    ),
                )
            }.onSuccess { response ->
                dispatch(SetLoading(false))
                if (response.organization == null) {
                    dispatch(SetB2BError(B2BErrorType.Organization))
                } else {
                    dispatch(SetActiveOrganization(response.organization))
                }
            }
        }
    }
}
