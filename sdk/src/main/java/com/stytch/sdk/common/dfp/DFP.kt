package com.stytch.sdk.common.dfp

public interface DFP {
    /**
     * Fetches a DFP Telemetry ID for use in backend lookup calls
     * @return String a dpf_telemetry_id
     */
    public suspend fun getTelemetryId(): String

    /**
     * Fetches a DFP Telemetry ID for use in backend lookup calls
     * @param callback  a callback that receives a dpf_telemetry_id
     */
    public fun getTelemetryId(callback: (String) -> Unit)
}
