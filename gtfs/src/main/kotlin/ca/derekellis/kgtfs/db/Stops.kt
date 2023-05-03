package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object Stops : Table(name = "Stop") {
  public val id: Column<String> = text("stop_id")
  public val code: Column<String?> = text("stop_code").nullable()
  public val name: Column<String?> = text("stop_name").nullable()
  public val description: Column<String?> = text("stop_desc").nullable()
  public val latitude: Column<Double?> = double("stop_lat").nullable()
  public val longitude: Column<Double?> = double("stop_long").nullable()
  public val zoneId: Column<String?> = text("zone_id").nullable()
  public val parentStation: Column<String?> = text("parent_station").nullable()
  public val url: Column<String?> = text("stop_url").nullable()
  public val locationType: Column<Int?> = integer("location_type").nullable()

  override val primaryKey: PrimaryKey = PrimaryKey(id)

  public val Mapper: (ResultRow) -> Stop = {
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

  public fun insert(stop: Stop): InsertStatement<Number> = insert {
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
