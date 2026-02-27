package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.Condition; // TODO: verify Mojang name (MultipartModelSelector)
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrMultipartModelSelector.class)
public interface OrMultipartModelSelectorAccessor {
	@Accessor
	Iterable<? extends MultipartModelSelector> getSelectors();
}
