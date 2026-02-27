package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.MultiPartBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

@Mixin(MultiPartBakedModel.class)
public interface MultipartBakedModelAccessor {
	@Accessor
	List<MultiPartBakedModel.Selector> getSelectors();

	@Accessor
	Map<BlockState, BitSet> getStateCache();

	@Accessor
	@Mutable
	void setStateCache(Map<BlockState, BitSet> stateBitSetMap);
}
