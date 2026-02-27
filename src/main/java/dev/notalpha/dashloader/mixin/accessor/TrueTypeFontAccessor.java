package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gui.font.GlyphContainer; // TODO: verify Mojang name
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderBuilder; // TODO: verify Mojang name
import org.lwjgl.util.freetype.FT_Face;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.ByteBuffer;

@Mixin(TrueTypeFont.class)
public interface TrueTypeFontAccessor {
	@Accessor
	@Mutable
	void setBuffer(ByteBuffer thing);

	@Accessor
	FT_Face getFace();

	@Accessor
	@Mutable
	void setFace(FT_Face thing);

	@Accessor
	float getOversample();

	@Accessor
	@Mutable
	void setOversample(float thing);

	@Accessor
	@Mutable
	void setContainer(GlyphContainer<TrueTypeFont.LazyGlyph> container);
}
