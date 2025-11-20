package cc.dvitski.collage.packet

import cc.dvitski.collage.Collage
import cc.dvitski.collage.ServerScreenshotHandler
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class ServerboundScreenshotPayload @Throws(IllegalArgumentException::class) constructor(
    val size: Int,
    val bytes: ByteArray,
    val hash: String
) : CustomPacketPayload {
    init {
        if (bytes.size != size) {
            throw IllegalArgumentException("Expected size and size of byte array did not match")
        }

        if (size > MAX_SCREENSHOT_SIZE) {
            throw IllegalArgumentException("Size greater than maximum allowed image size")
        }

        if (!ServerScreenshotHandler.validatePng(bytes)) {
            throw IllegalArgumentException("Not an image")
        }

        if (!ServerScreenshotHandler.validateHash(bytes, hash)) {
            throw IllegalArgumentException("Invalid hash")
        }
    }

    override fun type(): CustomPacketPayload.Type<ServerboundScreenshotPayload> {
        return CollagePacketTypes.SERVERBOUND_SCREENSHOT
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerboundScreenshotPayload

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    companion object {
        val ID: Identifier = Identifier.fromNamespaceAndPath(Collage.MOD_ID, "screenshot")

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ServerboundScreenshotPayload> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundScreenshotPayload::size,
            ByteBufCodecs.BYTE_ARRAY, ServerboundScreenshotPayload::bytes,
            ByteBufCodecs.STRING_UTF8, ServerboundScreenshotPayload::hash,
            ::ServerboundScreenshotPayload
        )

        const val MAX_SCREENSHOT_SIZE = 10 * 1024 * 1024
        const val MAX_PACKET_SIZE = MAX_SCREENSHOT_SIZE + 4 + 44
    }
}
