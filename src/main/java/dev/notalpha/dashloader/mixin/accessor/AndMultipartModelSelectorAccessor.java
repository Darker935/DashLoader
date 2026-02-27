package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition; // TODO: verify Mojang name (MultipartModelSelector)
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AndMultipartModelSelector.class)
public interface AndMultipartModelSelectorAccessor {
	@Accessor
	Iterable<? extends MultipartModelSelector> getSelectors();
}
