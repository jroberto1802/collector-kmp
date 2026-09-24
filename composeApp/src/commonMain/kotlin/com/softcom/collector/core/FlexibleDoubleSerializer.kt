package com.softcom.collector.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

object FlexibleDoubleSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeDouble()

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> 0.0
            is JsonPrimitive -> {
                element.doubleOrNull
                    ?: parseLocalizedDouble(element.content)
                    ?: throw SerializationException(
                        "Failed to parse literal \"${element.content}\" as a double value",
                    )
            }
            else -> throw SerializationException("Expected number or string for Double, got $element")
        }
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeDouble(value)
    }
}
