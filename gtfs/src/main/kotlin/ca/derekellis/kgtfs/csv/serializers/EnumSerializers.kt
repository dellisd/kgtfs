package ca.derekellis.kgtfs.csv.serializers

import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.Stop
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object RouteTypeSerializer : KSerializer<Route.Type> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RouteType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Route.Type = Route.Type.valueMap.getValue(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: Route.Type) {
        encoder.encodeInt(value.value)
    }
}

internal object LocationTypeSerializer : KSerializer<Stop.LocationType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocationType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Stop.LocationType = Stop.LocationType.values()[decoder.decodeInt()]

    override fun serialize(encoder: Encoder, value: Stop.LocationType) {
        encoder.encodeInt(value.ordinal)
    }
}
