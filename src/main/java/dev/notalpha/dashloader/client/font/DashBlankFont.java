package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import net.minecraft.client.gui.font.providers.BlankProvider;

public final class DashBlankFont implements DashObject<BlankProvider, BlankProvider> {
@Override
public BlankProvider export(RegistryReader exportHandler) {
return new BlankProvider();
}
}
