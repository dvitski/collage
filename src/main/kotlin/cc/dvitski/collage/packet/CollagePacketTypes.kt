package cc.dvitski.collage.packet

import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object CollagePacketTypes {
    val SERVERBOUND_SCREENSHOT: CustomPacketPayload.Type<ServerboundScreenshotPayload> = CustomPacketPayload.Type(ServerboundScreenshotPayload.ID)
}
