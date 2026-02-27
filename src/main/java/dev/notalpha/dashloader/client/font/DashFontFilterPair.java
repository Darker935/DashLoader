package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.FilterMapAccessor;
import net.minecraft.client.gui.font.FontFilterType;
import net.minecraft.client.gui.font.providers.GlyphProvider;

import java.util.HashMap;
import java.util.Map;

public class DashFontFilterPair implements DashObject<GlyphProvider.FilterPair, GlyphProvider.FilterPair> {
public final int provider;
public final IntIntList filter;

public DashFontFilterPair(int provider, IntIntList filter) {
this.provider = provider;
this.filter = filter;
}

public DashFontFilterPair(GlyphProvider.FilterPair fontFilterPair, RegistryWriter writer) {
this.provider = writer.add(fontFilterPair.provider());

filter = new IntIntList();
((FilterMapAccessor) fontFilterPair.filter()).getActiveFilters().forEach(
(key, value) -> filter.put(key.ordinal(), value ? 1 : 0));
}

@Override
public GlyphProvider.FilterPair export(RegistryReader reader) {
Map<FontFilterType, Boolean> activeFilters = new HashMap<>();
filter.forEach((key, value) -> activeFilters.put(FontFilterType.values()[key], value == 1));
return new GlyphProvider.FilterPair(reader.get(provider), new FontFilterType.FilterMap(activeFilters));
}
}
