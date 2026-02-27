package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gui.font.providers.BitmapProvider; // TODO: verify Mojang name
import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BitmapFont.BitmapFontGlyph.class)
public interface BitmapFontGlyphAccessor {
	@Invoker("<init>")
	static BitmapFont.BitmapFontGlyph init(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
		throw new AssertionError();
	}

	@Accessor
	NativeImage getImage();

	@Accessor("x")
	int getX();

	@Accessor("y")
	int getY();

	@Accessor
	float getScaleFactor();

	@Accessor
	int getWidth();

	@Accessor
	int getHeight();

	@Accessor
	int getAdvance();

	@Accessor
	int getAscent();
}
