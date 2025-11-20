package cc.dvitski.collage

import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Util
import net.minecraft.world.level.storage.LevelResource
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Path
import java.security.MessageDigest
import java.util.Base64
import javax.imageio.ImageIO

object ServerScreenshotHandler {
    val LEVEL_RESOURCE = LevelResource("${Collage.MOD_ID}/screenshots")
    const val PERMISSION = "${Collage.MOD_ID}.screenshot"

    fun handleScreenshot(player: ServerPlayer, server: MinecraftServer, payload: ServerboundScreenshotPayload) {
        // check permission
        if (!Permissions.check(player, PERMISSION, 0)) {
            return
        }

        // resolve file
        val path = server
            .getWorldPath(LEVEL_RESOURCE)
            .resolve(player.stringUUID)

        // try save screenshot
        runCatching {
            val bytes = payload.bytes
            val filename = saveScreenshot(path, bytes)

            val logMessage = "Saved screenshot of ${player.gameProfile.name} (${player.uuid}): $filename"
            if (!server.isSingleplayerOwner(player.nameAndId())) {
                Collage.logger.info(logMessage)
                player.sendSystemMessage(Component.literal("Server saved screenshot ($filename)"), true)
            } else {
                Collage.logger.debug(logMessage)
            }
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
