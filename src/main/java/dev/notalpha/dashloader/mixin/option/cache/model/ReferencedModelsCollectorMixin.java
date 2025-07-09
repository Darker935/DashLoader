package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ReferencedModelsCollector.class)
public class ReferencedModelsCollectorMixin {

	@Inject(
			method = "method_64092",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/model/ReferencedModelsCollector;addTopLevelModel(Lnet/minecraft/client/util/ModelIdentifier;Lnet/minecraft/client/render/model/UnbakedModel;)V")
	)
	private void linkIdToModelId(Set<?> set, Identifier id, CallbackInfo ci, @Local ModelIdentifier modelIdentifier) {
		ModelModule.ITEM_MODELID_TO_ID.visit(CacheStatus.SAVE, map -> map.put(modelIdentifier, id));
	}

	@Inject(method = "addBlockStates", at = @At("HEAD"))
	private void yoinkBlockStateDefinitions(BlockStatesLoader.BlockStateDefinition definition, CallbackInfo ci) {
		ModelModule.RAW_BLOCK_STATE_MODELS.visit(CacheStatus.SAVE, map -> map.putAll(definition.models()));
	}
}
