package cc.dvitski.collage

import cc.dvitski.collage.packet.CollagePacketTypes
import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Collage : ModInitializer {
    const val MOD_ID = "collage"
    const val MOD_NAME = "Collage"

    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Initializing $MOD_NAME")

        PayloadTypeRegistry.playC2S().registerLarge(CollagePacketTypes.SERVERBOUND_SCREENSHOT, ServerboundScreenshotPayload.STREAM_CODEC, ServerboundScreenshotPayload.MAX_PACKET_SIZE)

        ServerPlayNetworking.registerGlobalReceiver(CollagePacketTypes.SERVERBOUND_SCREENSHOT) { payload, context ->
            ServerScreenshotHandler.handleScreenshot(context.player(), context.server(), payload)
        }
    }
}
