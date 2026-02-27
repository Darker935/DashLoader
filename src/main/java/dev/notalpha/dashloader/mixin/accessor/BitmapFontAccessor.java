package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gui.font.GlyphContainer;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BitmapProvider.class)
public interface BitmapFontAccessor {
@Invoker("<init>")
static BitmapProvider init(NativeImage image, GlyphContainer<BitmapProvider.BitmapFontGlyph> glyphs) {
throw new AssertionError();
}

@Accessor
GlyphContainer<BitmapProvider.BitmapFontGlyph> getGlyphs();

@Accessor
NativeImage getImage();
}
