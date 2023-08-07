package ca.derekellis.kgtfs.raptor.models

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import io.github.dellisd.spatialk.geojson.Feature
import java.time.Duration

public sealed class Leg(
  public open val from: StopId,
  public open val to: StopId,
  public open val start: GtfsTime,
  public open val end: GtfsTime,
)

public data class TransferLeg(
  override val from: StopId,
  override val to: StopId,
  override val start: GtfsTime,
  override val end: GtfsTime,
  val duration: Duration,
  val distance: Double,
  val geometry: Feature?,
) : Leg(from, to, start, end)

public data class RouteLeg(
  override val from: StopId,
  override val to: StopId,
  override val start: GtfsTime,
  override val end: GtfsTime,
  val trip: TripId,
) : Leg(from, to, start, end)
