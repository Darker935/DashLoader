package dev.notalpha.dashloader.mixin.option.cache;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.atlas.AtlasModule;
import net.minecraft.client.texture.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.ArrayList;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin extends AbstractTexture {
	@Final
	@Shadow
	private Identifier id;

	@Shadow
	private int mipLevel;
	@Shadow
	private int width;
	@Shadow
	private int height;

	@Shadow public abstract void load(ResourceManager manager);

	@Shadow
	public abstract void save(Identifier id, Path path);

	@Inject(method = "upload", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", ordinal = 1))
	private void uploadAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci, @Share("cached") LocalRef<Boolean> cached) {
		cached.set(false);
		AtlasModule.ATLASES.visit(CacheStatus.LOAD, map -> {
			// TODO: save unloaded atlases... while in LOAD mode
			if (!map.containsKey(this.id)) {
				return;
			}

			var images = map.get(this.id);
			if (images.size() -1 < mipLevel) {
				return;
			}

			cached.set(true);
			bindTexture();
			for (int i = 0; i < images.size(); i++) {
				images.get(i).upload(i, 0, 0, true);
			}
		});
	}

	@WrapWithCondition(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;upload()V"))
	private boolean shouldUpload(Sprite instance, @Share("cached") LocalRef<Boolean> cached) {
		return !cached.get();
	}

    @Inject(method = "upload", at = @At("TAIL"))
    private void saveAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci) {
		AtlasModule.ATLASES.visit(CacheStatus.SAVE, map -> {
			RenderSystem.assertOnRenderThread();
			GlStateManager._bindTexture(this.getGlId());

			var atlases = new ArrayList<NativeImage>(this.mipLevel);
			for (int i = 0; i <= this.mipLevel; i++) {
				NativeImage nativeImage = new NativeImage(width >> i, height >> i, false);
				try {
					nativeImage.loadFromTextureImage(i, false);
					atlases.add(nativeImage);
				} catch (Exception e) {
					atlases.forEach(NativeImage::close);
					map.values().forEach(images -> images.forEach(NativeImage::close));
					map.clear();

					throw e;
                }
			}

			map.put(this.id, atlases);
		});
    }
}