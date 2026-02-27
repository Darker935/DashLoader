package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.providers.GlyphProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.ProviderIndex.class)
public interface FontManagerProviderIndexAccessor {
@Invoker("<init>")
static FontManager.ProviderIndex create(Map<ResourceLocation, List<GlyphProvider.FilterPair>> providers, List<GlyphProvider> allProviders) {
throw new AssertionError();
}
}
