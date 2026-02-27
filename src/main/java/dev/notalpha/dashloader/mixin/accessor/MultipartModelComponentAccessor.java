package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.block.model.multipart.Condition; // TODO: verify Mojang name (MultipartModelSelector)
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultipartModelComponent.class)
public interface MultipartModelComponentAccessor {
	@Accessor()
	MultipartModelSelector getSelector();
}
