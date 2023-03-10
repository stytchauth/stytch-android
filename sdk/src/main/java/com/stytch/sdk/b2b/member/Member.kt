package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.network.models.MemberData

/**
 * The Member interface provides methods for retrieving the current authenticated user.
 */
public interface Member {

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
}
