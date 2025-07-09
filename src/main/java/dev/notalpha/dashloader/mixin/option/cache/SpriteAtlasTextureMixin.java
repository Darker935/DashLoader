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
import org.apache.commons.codec.digest.DigestUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

	@Shadow
	public abstract void load(ResourceManager manager);

	@Shadow
	public abstract void save(Identifier id, Path path);

	@Shadow
	private List<Sprite.TickableAnimation> animatedSprites;

	@Inject(method = "upload", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", ordinal = 1))
	private void uploadAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci, @Share("cached") LocalRef<Boolean> cached) {
		cached.set(false);
		AtlasModule.ATLASES.visit(CacheStatus.LOAD, map -> {
			var tasks = map.get(this.id.toUnderscoreSeparatedString());
			if (tasks == null || mipLevel >= tasks.size()) {
				return;
			}

			cached.set(true);
			bindTexture();
			try {
				for (int i = 0; i < tasks.size(); i++) {
					tasks.get(i).get().upload(i, 0, 0, true);
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@WrapWithCondition(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;upload()V"))
	private boolean shouldUpload(Sprite instance, @Share("cached") LocalRef<Boolean> cached) {
		return !cached.get();
	}

	@Inject(method = "upload", at = @At("TAIL"))
	private void saveAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci, @Share("cached") LocalRef<Boolean> cached) throws IOException {
		if (cached.get()) {
			return;
		}

		var stringId = this.id.toUnderscoreSeparatedString();

		AtlasModule.ATLASES.visit(CacheStatus.SAVE, map -> {
			map.put(stringId, null); // just for the string
		});

		var atlasFolder = AtlasModule.getAtlasFolder();
		try {
			Files.createDirectories(atlasFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		RenderSystem.assertOnRenderThread();
		GlStateManager._bindTexture(this.getGlId());

		for (int i = 0; i <= this.mipLevel; i++) {
			try (NativeImage nativeImage = new NativeImage(width >> i, height >> i, false)) {
				nativeImage.loadFromTextureImage(i, false);
				var path = atlasFolder.resolve(DigestUtils.md5Hex(stringId + i).toUpperCase());
				nativeImage.writeTo(path);
			}
		}
	}
}
