package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object Agencies : Table(name = "Agency") {
  val id: Column<String?> = text("agency_id").nullable()
  val name: Column<String> = text("agency_name")
  val url: Column<String> = text("agency_url")
  val timezone: Column<String> = text("agency_timezone")
  val language: Column<String?> = text("agency_lang").nullable()
  val phone: Column<String?> = text("agency_phone").nullable()
  val fareUrl: Column<String?> = text("agency_fare_url").nullable()
  val email: Column<String?> = text("agency_email").nullable()

  override val primaryKey = PrimaryKey(id)

  val Mapper: (ResultRow) -> Agency = {
    Agency(it[id]?.let(::AgencyId), it[name], it[url], it[timezone], it[language], it[phone], it[fareUrl], it[email])
  }

  fun insert(agency: Agency) = insert {
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
