package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel; // TODO: verify Mojang name
import net.minecraft.util.random.WeightedRandomList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccessor {
	@Accessor
	DataPool<BakedModel> getModels();
}
