package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.DeleteMemberAuthenticationFactorResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.UpdateMemberResponse
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.MfaMethod
import kotlinx.coroutines.flow.StateFlow

/**
 * The Member interface provides methods for retrieving and updating the current authenticated member.
 */
public interface Member {
    /**
     * Exposes a flow of member data
     */
    public val onChange: StateFlow<MemberData?>

    /**
     * Assign a callback that will be called when the member data changes
     */

    public fun onChange(callback: (MemberData?) -> Unit)

    /**
     * Wraps Stytch’s organization/members/me endpoint.
     * @return [MemberResponse]
     */
    public suspend fun get(): MemberResponse

    /**
     * Wraps Stytch’s organization/members/me endpoint.
     * @param callback a callback that receives a [MemberResponse]
     */
    public fun get(callback: (MemberResponse) -> Unit)

    /**
     * Get member from memory without network call
     * @return locally stored [MemberData]
     */
    public fun getSync(): MemberData?

    /**
     * Data class used for wrapping parameters used with Member updates
     * @property name the name of the member
     * @property untrustedMetadata a map of untrusted metadata to assign to the member
     */
    public data class UpdateParams(
        val name: String? = null,
        val untrustedMetadata: Map<String, Any>? = null,
        val mfaEnrolled: Boolean? = null,
        val mfaPhoneNumber: String? = null,
        val defaultMfaMethod: MfaMethod? = null,
    )

    /**
     * Updates the currently authenticated member
     * @param params required to update the member
     * @return [UpdateMemberResponse]
     */
    public suspend fun update(params: UpdateParams): UpdateMemberResponse

    /**
     * Updates the currently authenticated member
     * @param params required to update the member
     * @param callback a callback that receives an [UpdateMemberResponse]
     */
    public fun update(
        params: UpdateParams,
        callback: (UpdateMemberResponse) -> Unit,
    )

    /**
     * Deletes a [MemberAuthenticationFactor] from the currently authenticated member
     * @return [DeleteMemberAuthenticationFactorResponse]
     */
    public suspend fun deleteFactor(factor: MemberAuthenticationFactor): DeleteMemberAuthenticationFactorResponse

    /**
     * Deletes a [MemberAuthenticationFactor] from the currently authenticated member
     * @param callback A callback that receives a [DeleteMemberAuthenticationFactorResponse]
     */
    public fun deleteFactor(
        factor: MemberAuthenticationFactor,
        callback: (DeleteMemberAuthenticationFactorResponse) -> Unit,
    )
}
