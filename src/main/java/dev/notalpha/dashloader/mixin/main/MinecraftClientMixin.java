package dev.notalpha.dashloader.mixin.main;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.DashLoaderClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	@Shadow
	protected abstract void render(boolean tick);

	@Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;reloadResources(ZLnet/minecraft/client/Minecraft$LoadingContext;)Ljava/util/concurrent/CompletableFuture;"))
	private void requestReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		DashLoaderClient.NEEDS_RELOAD = true;
	}

	@Inject(method = "reloadResources(ZLnet/minecraft/client/Minecraft$LoadingContext;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "RETURN"))
	private void reloadComplete(boolean force, Minecraft.LoadingContext loadingContext, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.getReturnValue().thenRun(() -> {
			// If the state is SAVE, then this will reset before the caching process can initialize from the splash screen.
			if (DashLoaderClient.CACHE.getStatus() != CacheStatus.SAVE) {
				DashLoaderClient.CACHE.reset();
			}
		});
	}
}
