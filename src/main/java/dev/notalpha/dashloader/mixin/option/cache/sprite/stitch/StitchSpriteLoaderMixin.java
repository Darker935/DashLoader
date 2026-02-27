package dev.notalpha.dashloader.mixin.option.cache.sprite.stitch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.sprite.stitch.DashTextureStitcher;
import dev.notalpha.dashloader.client.sprite.stitch.SpriteStitcherModule;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader; // TODO: verify Mojang name
import net.minecraft.client.renderer.texture.Stitcher; // TODO: verify Mojang name (Stitcher)
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public final class StitchSpriteLoaderMixin {
	@Shadow
	@Final
	private ResourceLocation id;

	@WrapOperation(
			method = "stitch",
			at = @At(value = "NEW", target = "(III)Lnet/minecraft/client/renderer/texture/Stitcher;")
	)
	private Stitcher<?> dashloaderStitcherLoad(int maxWidth, int maxHeight, int mipLevel, Operation<Stitcher<?>> original) {
		var map = SpriteStitcherModule.STITCHERS_LOAD.get(CacheStatus.LOAD);
		if (map != null) {
			var data = map.get(id);
			if (data != null) {
				return new DashTextureStitcher<>(maxWidth, maxHeight, mipLevel, data);
			}
		}

		return original.call(maxWidth, maxHeight, mipLevel);
	}

	@Inject(
			method = "stitch",
			at = @At(value = "RETURN"),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void dashloaderStitcherSave(List<SpriteContents> sprites, int mipLevel, Executor executor, CallbackInfoReturnable<SpriteLoader.StitchResult> cir, int i, Stitcher<SpriteContents> textureStitcher) {
		SpriteStitcherModule.STITCHERS_SAVE.visit(CacheStatus.SAVE, map -> map.add(Pair.of(id, textureStitcher)));
	}
}
