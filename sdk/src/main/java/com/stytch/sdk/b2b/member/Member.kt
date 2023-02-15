package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.network.MemberData

/**
 * Member interface that encompasses retrieving a Member
 */
public interface Member {

    /**
     * Wraps Stytch’s organization/members/me endpoint.
     * @return MemberResponse response from backend
     */
    public suspend fun get(): MemberResponse

    /**
     * Wraps Stytch’s organization/members/me endpoint.
     * @param callback calls callback with MemberResponse response from backend
     */
    public fun get(callback: (MemberResponse) -> Unit)

    /**
     * Get member from memory without network call
     * @return locally stored member
     */
    public fun getSync(): MemberData?
}
