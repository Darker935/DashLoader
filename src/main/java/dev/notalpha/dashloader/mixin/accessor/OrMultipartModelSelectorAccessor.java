package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrCondition.class)
public interface OrMultipartModelSelectorAccessor {
@Accessor
Iterable<? extends Condition> getSelectors();
}
