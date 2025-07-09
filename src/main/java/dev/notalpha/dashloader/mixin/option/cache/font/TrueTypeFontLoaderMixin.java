package dev.notalpha.dashloader.mixin.option.cache.font;

import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.font.DashTrueTypeFont;
import dev.notalpha.dashloader.client.font.FontModule;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.util.freetype.FT_Face;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrueTypeFontLoader.class)
public abstract class TrueTypeFontLoaderMixin {
	@Shadow
	public abstract float size();

	@Shadow
	public abstract Identifier location();

	@Shadow
	public abstract String skip();

	@Inject(
			method = "load",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TrueTypeFont;<init>(Ljava/nio/ByteBuffer;Lorg/lwjgl/util/freetype/FT_Face;FFFFLjava/lang/String;)V")
	)
	private void loadInject(ResourceManager resourceManager, CallbackInfoReturnable<Font> cir, @Local FT_Face ft_face) {
		FontModule.FONT_TO_DATA.visit(CacheStatus.SAVE, map -> map.put(ft_face, new DashTrueTypeFont.FontPrams(location(), size(), skip())));
	}
}
