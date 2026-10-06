package ca.derekellis.kgtfs.csv

@JvmInline
public value class RouteId(public val value: String) {
  override fun toString(): String = value
}

public data class Route(
  val id: RouteId,
  val shortName: String?,
  val longName: String?,
  val desc: String?,
  val type: Type,
  val url: String? = null,
  val color: String? = null,
  val textColor: String? = null,
) : Gtfs {
  public enum class Type(public val value: Int) {
    Tram(0),
    Subway(1),
    Rail(2),
    Bus(3),
    Ferry(4),
    CableTram(5),
    AerialLift(6),
    Funicular(7),
    Trolleybus(11),
    Monorail(12),
    ;

    public companion object {
      public val valueMap: Map<Int, Type> = mapOf(
        Tram.value to Tram,
        Subway.value to Subway,
        Rail.value to Rail,
        Bus.value to Bus,
        Ferry.value to Ferry,
        CableTram.value to CableTram,
        AerialLift.value to AerialLift,
        Funicular.value to Funicular,
        Trolleybus.value to Trolleybus,
        Monorail.value to Monorail,
      )
    }
  }
}
