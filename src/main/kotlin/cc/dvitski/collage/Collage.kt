package cc.dvitski.collage

import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.LevelResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object Collage : ModInitializer {
    const val MOD_ID = "collage"
    const val MOD_NAME = "Collage"

    private val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Initializing $MOD_NAME")

        PayloadTypeRegistry.playC2S().register(ServerboundScreenshotPayload.TYPE, ServerboundScreenshotPayload.STREAM_CODEC)

        ServerPlayNetworking.registerGlobalReceiver(ServerboundScreenshotPayload.TYPE) { payload, context ->
            handleScreenshot(context.player(), context.server(), payload.bytes)
        }
    }

    private fun handleScreenshot(player: ServerPlayer, server: MinecraftServer, bytes: ByteArray) {
        if (!validatePng(bytes)) {
            return
        }

        player.sendSystemMessage(Component.literal("Received screenshot (${bytes.size}B)"), true)

        val filename = Util.getFilenameFormattedDateTime()

        val path = server.getWorldPath(LevelResource("$MOD_ID/screenshots"))
        val file = path.toFile()
        file.mkdirs()
        file.resolve("$filename.png").writeBytes(bytes)

        player.sendSystemMessage(Component.literal("Server saved screenshot ($filename)"), true)
    }

    private fun validatePng(bytes: ByteArray): Boolean {
        return runCatching {
            ImageIO.read(ByteArrayInputStream(bytes))
        }.getOrNull() != null // not isSuccess, result of ImageIO.read can be null
    }
}
