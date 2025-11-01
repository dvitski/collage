package cc.dvitski.collage

import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.LevelResource
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Path
import java.security.MessageDigest
import java.util.Base64
import javax.imageio.ImageIO

object ServerScreenshotHandler {
    val LEVEL_RESOURCE = LevelResource("${Collage.MOD_ID}/screenshots")

    fun handleScreenshot(player: ServerPlayer, server: MinecraftServer, payload: ServerboundScreenshotPayload) {
        val bytes = payload.bytes

        val path = server
            .getWorldPath(LEVEL_RESOURCE)
            .resolve(player.stringUUID)

        runCatching {
            val filename = saveScreenshot(path, bytes)
            player.sendSystemMessage(Component.literal("Server saved screenshot ($filename)"), true)
        }.exceptionOrNull()?.printStackTrace()
    }

    @Throws(IOException::class)
    private fun saveScreenshot(path: Path, bytes: ByteArray): String? {
        val datetime = Util.getFilenameFormattedDateTime()

        val file = path.toFile()

        // create folders
        file.mkdirs()

        // create png file
        val pngFile = file.resolve("$datetime.png")
        pngFile.writeBytes(bytes)

        return datetime
    }

    fun validatePng(bytes: ByteArray): Boolean {
        return runCatching {
            ImageIO.read(ByteArrayInputStream(bytes))
        }.getOrNull() != null // not isSuccess, result of ImageIO.read can be null
    }

    fun validateHash(bytes: ByteArray, expectedHash: String): Boolean {
        val hash = hashBytes(bytes)
        return hash == expectedHash
    }

    fun hashBytes(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .let(Base64.getEncoder()::encodeToString)
    }
}
