package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.MultipartUnbakedModel;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultipartUnbakedModel.Selector.class)
public interface MultipartModelComponentAccessor {
@Accessor()
Condition getSelector();
}
