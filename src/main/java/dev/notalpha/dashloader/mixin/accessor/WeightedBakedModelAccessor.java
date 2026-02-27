package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.util.random.DataPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccessor {
@Accessor
DataPool<BakedModel> getModels();
}
