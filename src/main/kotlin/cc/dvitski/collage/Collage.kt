package cc.dvitski.collage

import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.chat.Component
import net.minecraft.world.level.storage.LevelResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Collage : ModInitializer {
    const val MOD_ID = "collage"
    const val MOD_NAME = "Collage"

    private val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Initializing $MOD_NAME")

        PayloadTypeRegistry.playC2S().register(ServerboundScreenshotPayload.TYPE, ServerboundScreenshotPayload.STREAM_CODEC)

        ServerPlayNetworking.registerGlobalReceiver(ServerboundScreenshotPayload.TYPE) { payload, context ->
            val player = context.player()
            player.sendSystemMessage(Component.literal("Received screenshot (${payload.bytes.size}B)"), true)

            val server = context.server()
            val path = server.getWorldPath(LevelResource("$MOD_ID/screenshots"))
            val file = path.toFile()
            file.mkdirs()
            file.resolve("screenshot.png").writeBytes(payload.bytes)

            player.sendSystemMessage(Component.literal("Saved screenshot (${payload.bytes.size}B)"), true)
        }
    }
}
