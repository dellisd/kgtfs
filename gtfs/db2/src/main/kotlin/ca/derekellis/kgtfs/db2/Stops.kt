package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object Stops : Table(name = "Stop") {
  val id: Column<String> = text("stop_id")
  val code: Column<String?> = text("stop_code").nullable()
  val name: Column<String?> = text("stop_name").nullable()
  val description: Column<String?> = text("stop_desc").nullable()
  val latitude: Column<Double?> = double("stop_lat").nullable()
  val longitude: Column<Double?> = double("stop_long").nullable()
  val zoneId: Column<String?> = text("zone_id").nullable()
  val parentStation: Column<String?> = text("parent_station").nullable()
  val url: Column<String?> = text("stop_url").nullable()
  val locationType: Column<Int?> = integer("location_type").nullable()

  override val primaryKey = PrimaryKey(id)

  val Mapper: (ResultRow) -> Stop = {
    Stop(
      it[id].let(::StopId),
      it[code],
      it[name],
      it[description],
      it[latitude],
      it[longitude],
      it[zoneId],
      it[url],
      it[locationType]?.let(locationTypes::get),
      it[parentStation]?.let(::StopId)
    )
  }

  fun insert(stop: Stop) = insert {
    it[id] = stop.id.value
    it[code] = stop.code
    it[name] = stop.name
    it[description] = stop.description
    it[latitude] = stop.latitude
    it[longitude] = stop.longitude
    it[zoneId] = stop.zoneId
    it[url] = stop.url
    it[locationType] = stop.locationType?.ordinal
    it[parentStation] = stop.parentStation?.value
  }

  private val locationTypes = Stop.LocationType.values()
}
