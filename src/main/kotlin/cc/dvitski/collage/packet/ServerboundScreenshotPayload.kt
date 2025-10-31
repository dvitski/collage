package cc.dvitski.collage.packet

import cc.dvitski.collage.Collage
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class ServerboundScreenshotPayload(val bytes: ByteArray) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ServerboundScreenshotPayload> {
        return TYPE
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
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Collage.MOD_ID, "screenshot")
        val TYPE: CustomPacketPayload.Type<ServerboundScreenshotPayload> = CustomPacketPayload.Type(ID)

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ServerboundScreenshotPayload> = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY, ServerboundScreenshotPayload::bytes,
            ::ServerboundScreenshotPayload
        )
    }
}
