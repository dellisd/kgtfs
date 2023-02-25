package ca.derekellis.kgtfs.csv.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal object DateSerializer : KSerializer<LocalDate> {
    private val pattern = DateTimeFormatter.ofPattern("yyyyMMdd")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString(), pattern)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(pattern))
    }
}
