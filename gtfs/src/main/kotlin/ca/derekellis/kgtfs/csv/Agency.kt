package ca.derekellis.kgtfs.csv

@JvmInline
public value class AgencyId(public val value: String) {
  override fun toString(): String = value
}

public data class Agency(
  val id: AgencyId? = null,
  val name: String,
  val url: String,
  val timezone: String,
  val lang: String? = null,
  val phone: String? = null,
  val fareUrl: String? = null,
  val email: String? = null,
) : Gtfs
