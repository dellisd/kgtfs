package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object Routes : Table(name = "Route") {
  public val id: Column<String> = text("route_id")
  public val shortName: Column<String?> = text("route_short_name").nullable()
  public val longName: Column<String?> = text("route_long_name").nullable()
  public val description: Column<String?> = text("route_desc").nullable()
  public val type: Column<Int> = integer("route_type")
  public val url: Column<String?> = text("route_url").nullable()
  public val color: Column<String?> = text("route_color").nullable()
  public val textColor: Column<String?> = text("route_text_color").nullable()

  override val primaryKey: PrimaryKey = PrimaryKey(id)

  public val mapper: (ResultRow) -> Route = {
    Route(
      it[id].let(::RouteId),
      it[shortName],
      it[longName],
      it[description],
      it[type].let(Route.Type.valueMap::getValue),
      it[url],
      it[color],
      it[textColor],
    )
  }

  public fun insert(route: Route): InsertStatement<Number> = insert {
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
