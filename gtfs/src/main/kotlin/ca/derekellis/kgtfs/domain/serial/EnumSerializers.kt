package ca.derekellis.kgtfs.domain.serial

import ca.derekellis.kgtfs.domain.model.Route
import ca.derekellis.kgtfs.domain.model.Stop
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object RouteTypeSerializer : KSerializer<Route.Type> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RouteType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Route.Type = Route.Type.valueMap.getValue(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: Route.Type) {
        encoder.encodeInt(value.value)
    }
}

public object LocationTypeSerializer : KSerializer<Stop.LocationType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocationType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Stop.LocationType = Stop.LocationType.values()[decoder.decodeInt()]

    override fun serialize(encoder: Encoder, value: Stop.LocationType) {
        encoder.encodeInt(value.ordinal)
    }
}
