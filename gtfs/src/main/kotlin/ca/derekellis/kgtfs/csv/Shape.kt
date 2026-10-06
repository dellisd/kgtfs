package ca.derekellis.kgtfs.csv

@JvmInline
public value class ShapeId(public val value: String) {
  override fun toString(): String = value
}

public data class Shape(
  val id: ShapeId,
  val latitude: Double,
  val longitude: Double,
  val sequence: Int,
) : Gtfs
