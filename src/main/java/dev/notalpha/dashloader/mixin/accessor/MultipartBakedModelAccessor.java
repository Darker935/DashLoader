package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

@Mixin(MultipartBakedModel.class)
public interface MultipartBakedModelAccessor {
	@Accessor
	List<MultipartBakedModel.Selector> getSelectors();

	@Accessor
	Map<BlockState, BitSet> getStateCache();

	@Accessor
	@Mutable
	void setStateCache(Map<BlockState, BitSet> stateBitSetMap);
}
