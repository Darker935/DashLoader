package dev.notalpha.dashloader.mixin.option;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.texture.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.file.Path;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin extends AbstractTexture { // TODO: make this it's own module
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
	private void uploadAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci) throws IOException {

		TextureUtil.prepareImage(getGlId(), mipLevel, width, height);
		for (int level = 0; level <= mipLevel; level++) {
			var f = new FileInputStream("./dashloader-cache/client/" + id.toUnderscoreSeparatedString() + "_" + level + ".png");
			var nativeImage = NativeImage.read(f);
			try {
//                var nativeImage = NativeImage.read(dataToImage[level]);
				bindTexture();
				nativeImage.upload(level, 0, 0, false);
				nativeImage.close();
			}
			catch (Exception e) {
				nativeImage.close();

				throw e;
			}
		}
	}

	@WrapWithCondition(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;upload()V"))
	private boolean shouldUpload(Sprite instance) {

		return false;
	}

//    @Inject(method = "upload", at = @At("TAIL"))
//    private void saveAtlas(SpriteLoader.StitchResult stitchResult, CallbackInfo ci) {
//        String string = id.toUnderscoreSeparatedString();
////        save(id, Path.of("./dashloader-cache/client/"));
//        TextureUtil.writeAsPNG(Path.of("./dashloader-cache/client/"),
//                string, this.getGlId(), this.mipLevel, this.width, this.height);
//    }
}