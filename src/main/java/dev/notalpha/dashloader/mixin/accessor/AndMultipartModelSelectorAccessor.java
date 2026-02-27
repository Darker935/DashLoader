package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AndCondition.class)
public interface AndMultipartModelSelectorAccessor {
@Accessor
Iterable<? extends Condition> getSelectors();
}
