package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gui.font.GlyphContainer; // TODO: verify Mojang name
import net.minecraft.client.gui.font.providers.UnihexProvider; // TODO: verify Mojang name
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UnihexFont.class)
public interface UnihexFontAccessor {
	@Invoker("<init>")
	static UnihexFont create(GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphs) {
		throw new AssertionError();
	}

	@Accessor
	GlyphContainer<UnihexFont.UnicodeTextureGlyph> getGlyphs();
}
