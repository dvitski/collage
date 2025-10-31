package cc.dvitski.collage

import cc.dvitski.collage.packet.ServerboundScreenshotPayload
import com.mojang.blaze3d.platform.NativeImage
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.Util
import net.minecraft.network.chat.Component
import java.io.File
import java.util.UUID
import java.util.function.Consumer

object ScreenshotHandler {
    fun onScreenshot(consumer: Consumer<Component>, nativeImage: NativeImage) {
        Util.ioPool().execute {
            sendtoServer(consumer, nativeImage)
        }
    }

    fun sendtoServer(consumer: Consumer<Component>, nativeImage: NativeImage) {
        val file = File.createTempFile(UUID.randomUUID().toString(), "png")
        nativeImage.writeToFile(file)
        val bytes = file.readBytes()
        ClientPlayNetworking.send(ServerboundScreenshotPayload(bytes))
    }
}
