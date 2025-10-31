package cc.dvitski.collage.mixin.client;

import cc.dvitski.collage.ScreenshotHandler;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
    @Inject(
            method = "method_68157",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/TracingExecutor;execute(Ljava/lang/Runnable;)V",
                    shift = At.Shift.AFTER
            )
    )
    private static void postScreenshot(File file, String string, Consumer<Component> consumer, NativeImage nativeImage, CallbackInfo ci) {
        ScreenshotHandler.INSTANCE.onScreenshot(consumer, nativeImage);
    }
}
