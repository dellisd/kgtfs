package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object Agencies : Table(name = "Agency") {
  public val id: Column<String?> = text("agency_id").nullable()
  public val name: Column<String> = text("agency_name")
  public val url: Column<String> = text("agency_url")
  public val timezone: Column<String> = text("agency_timezone")
  public val language: Column<String?> = text("agency_lang").nullable()
  public val phone: Column<String?> = text("agency_phone").nullable()
  public val fareUrl: Column<String?> = text("agency_fare_url").nullable()
  public val email: Column<String?> = text("agency_email").nullable()

  override val primaryKey: PrimaryKey = PrimaryKey(id)

  public val Mapper: (ResultRow) -> Agency = {
    Agency(it[id]?.let(::AgencyId), it[name], it[url], it[timezone], it[language], it[phone], it[fareUrl], it[email])
  }

  public fun insert(agency: Agency): InsertStatement<Number> = insert {
    it[id] = agency.id?.value
    it[name] = agency.name
    it[url] = agency.url
    it[timezone] = agency.timezone
    it[language] = agency.lang
    it[phone] = agency.phone
    it[fareUrl] = agency.fareUrl
    it[email] = agency.email
  }
}
