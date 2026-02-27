package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.DataPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccessor {
	@Accessor
	DataPool<BakedModel> getModels();
}
