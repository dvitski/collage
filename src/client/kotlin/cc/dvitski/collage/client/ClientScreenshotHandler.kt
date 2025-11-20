package cc.dvitski.collage.client

import cc.dvitski.collage.ServerScreenshotHandler
import cc.dvitski.collage.packet.CollagePacketTypes
import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import com.mojang.blaze3d.platform.NativeImage
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.util.Util
import java.io.File
import java.util.UUID

object ClientScreenshotHandler {
    fun onScreenshot(nativeImage: NativeImage) {
        Util.ioPool().execute {
            sendtoServer(nativeImage)
        }
    }

    fun sendtoServer(nativeImage: NativeImage) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.singleplayerServer != null) {
            return
        }

        if (!ClientPlayNetworking.canSend(CollagePacketTypes.SERVERBOUND_SCREENSHOT)) {
            CollageClient.logger.debug("Did not send screenshot, not in world")
            return
        }

        val bytes = readBytes(nativeImage)
        val hash = ServerScreenshotHandler.hashBytes(bytes)
        runCatching {
            val packet = ServerboundScreenshotPayload(bytes.size, bytes, hash)
            ClientPlayNetworking.send(packet)
        }.exceptionOrNull()?.printStackTrace()
    }

    private fun readBytes(nativeImage: NativeImage): ByteArray {
        // get image bytes
        val file = File.createTempFile(UUID.randomUUID().toString(), "png")
        nativeImage.writeToFile(file)
        val bytes = file.readBytes()

        // try delete temp file
        runCatching {
            file.delete()
        }

        file.deleteOnExit()

        return bytes
    }
}
