package ca.derekellis.kgtfs.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class AgencyId(public val value: String) {
    override fun toString(): String = value
}

@Serializable
public data class Agency(
    @SerialName("agency_id") val id: AgencyId? = null,
    @SerialName("agency_name") val name: String,
    @SerialName("agency_url") val url: String,
    @SerialName("agency_timezone") val timezone: String,
    @SerialName("agency_lang") val lang: String? = null,
    @SerialName("agency_phone") val phone: String? = null,
    @SerialName("agency_fare_url") val fareUrl: String? = null,
    @SerialName("agency_email") val email: String? = null
)
