package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.state.StateManager;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MultipartUnbakedModel.Serialized.class)
public abstract class MultipartUnbakedModelSerializedMixin {
	@Shadow
	@Final
	private List<MultipartModelComponent> selectors;

	@Inject(method = "toModel", at = @At(value = "RETURN"))
	private void thing(StateManager<Block, BlockState> stateManager, CallbackInfoReturnable<MultipartUnbakedModel> cir) {
		ModelModule.MULTIPART_PREDICATES.visit(CacheStatus.SAVE, map -> {
			map.put(cir.getReturnValue(), Pair.of(selectors, stateManager));
		});
	}
}
