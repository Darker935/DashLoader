package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(BlockStatesLoader.class)
public abstract class BlockStatesLoaderMixin {
	@Shadow
	protected abstract void loadBlockStates(Identifier id, StateManager<Block, BlockState> stateManager);

	/**
	 * We want to not load all of the blockstate models as we have a list of them available on which ones to load to save a lot of computation
	 */

	@Redirect(
			method = "load",
			at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V")
	)
	private void loadMissingModels(Map instance, BiConsumer v) {
		// No mods should be adding to static definitions
	}

	@WrapOperation(
			method = "load",
			at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")
	)
	private boolean loadMissingModels(Iterator instance, Operation<Boolean> original) {
		var map = ModelModule.MISSING_READ.get(CacheStatus.LOAD);
		if (map != null) {
			map.values().forEach((modelIdentifier) -> {
				var id = modelIdentifier.id();
				this.loadBlockStates(modelIdentifier.id(), Registries.BLOCK.get(id).getStateManager());
			});
			DashLoader.LOG.info("Loaded {} unsupported models.", map.size());

			return false;
		}
		return original.call(instance);
	}
}
