package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object Routes : Table(name = "Route") {
  val id: Column<String> = text("route_id")
  val shortName: Column<String?> = text("route_short_name").nullable()
  val longName: Column<String?> = text("route_long_name").nullable()
  val description: Column<String?> = text("route_desc").nullable()
  val type: Column<Int> = integer("route_type")
  val url: Column<String?> = text("route_url").nullable()
  val color: Column<String?> = text("route_color").nullable()
  val textColor: Column<String?> = text("route_text_color").nullable()

  override val primaryKey = PrimaryKey(id)

  val mapper: (ResultRow) -> Route = {
    Route(
      it[id].let(::RouteId),
      it[shortName],
      it[longName],
      it[description],
      it[type].let(Route.Type.valueMap::getValue),
      it[url],
      it[color],
      it[textColor]
    )
  }

  fun insert(route: Route) = insert {
    it[id] = route.id.value
    it[shortName] = route.shortName
    it[longName] = route.longName
    it[description] = route.desc
    it[type] = route.type.value
    it[url] = route.url
    it[color] = route.color
    it[textColor] = route.textColor
  }
}
