package ca.derekellis.kgtfs.csv.serializers

import ca.derekellis.kgtfs.csv.GtfsTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object GtfsTimeSerializer : KSerializer<GtfsTime> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GtfsTime", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): GtfsTime = GtfsTime(decoder.decodeString())

  override fun serialize(encoder: Encoder, value: GtfsTime) {
    encoder.encodeString(value.toString())
  }
}
