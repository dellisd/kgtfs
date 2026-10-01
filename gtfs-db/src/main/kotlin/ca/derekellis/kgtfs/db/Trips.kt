package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.ShapeId
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object Trips : Table() {
  public val routeId: Column<String> = text("route_id")
  public val serviceId: Column<String> = text("service_id")
  public val id: Column<String> = text("trip_id")
  public val headsign: Column<String?> = text("trip_headsign").nullable()
  public val directionId: Column<Int?> = integer("direction_id").nullable()
  public val blockId: Column<String?> = text("block_id").nullable()
  public val shapeId: Column<String?> = text("shape_id").nullable()

  override val primaryKey: PrimaryKey = PrimaryKey(id)

  public val Mapper: (ResultRow) -> Trip = {
    Trip(
      it[routeId].let(::RouteId),
      it[serviceId].let(::ServiceId),
      it[id].let(::TripId),
      it[headsign],
      directionId = it[directionId],
      blockId = it[blockId],
      shapeId = it[shapeId]?.let(::ShapeId),
    )
  }

  public fun insert(trip: Trip): InsertStatement<Number> = insert {
    it[routeId] = trip.routeId.value
    it[serviceId] = trip.serviceId.value
    it[id] = trip.id.value
    it[headsign] = trip.headsign
    it[directionId] = trip.directionId
    it[blockId] = trip.blockId
    it[shapeId] = trip.shapeId?.value
  }
}
