package cc.dvitski.collage

import cc.dvitski.collage.Collage.MOD_ID
import cc.dvitski.collage.Collage.MOD_NAME
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CollageClient : ClientModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("$MOD_ID-client")

	override fun onInitializeClient() {
        logger.info("Initializing $MOD_NAME client")
	}
}
