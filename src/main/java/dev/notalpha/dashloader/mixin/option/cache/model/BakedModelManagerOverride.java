package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Mixin(value = BakedModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@WrapOperation(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelBaker;getBakedModels()Ljava/util/Map;"))
	private Map<ModelIdentifier, BakedModel> yoinkBakedModels(ModelBaker instance, Operation<Map<ModelIdentifier, BakedModel>> original) {
		var bakedModels = original.call(instance);
		ModelModule.MODELS_SAVE.visit(CacheStatus.SAVE, map -> map.putAll(bakedModels));
		ModelModule.BAKED_MODEL_PARTS.visit(CacheStatus.SAVE, map -> map.putAll(instance.bakedModelCache));
		return bakedModels;
	}

	@ModifyReturnValue(method = "method_45899", at = @At(value = "RETURN"))
	private static CompletionStage<?> injectModels(CompletionStage<Map<Identifier, UnbakedModel>> original) {
		var models = ModelModule.MODELS.get(CacheStatus.LOAD);
		if (models != null) {
			return original.thenApply(unbakedModels -> {
				if (!(unbakedModels instanceof HashMap)) {
					unbakedModels = new HashMap<>(unbakedModels);
				}

				unbakedModels.putAll(models);
				return unbakedModels;
			});
		}

		return original;
	}

	@WrapOperation(method = "method_62663", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
	private static Map<Identifier, Resource> loadMissingModels(ResourceFinder instance, ResourceManager resourceManager, Operation<Map<Identifier, Resource>> original) {
		var modelIds = ModelModule.MISSING_MODELS.get(CacheStatus.LOAD);
		if (modelIds != null) {
			var out = new HashMap<Identifier, Resource>(modelIds.size());
			for (Identifier id : modelIds) {
				resourceManager.getResource(id).ifPresent(resource -> out.put(id, resource));
			}
			return out;
		}

		return original.call(instance, resourceManager);
	}

	@Inject(method = "method_62658", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BlockStatesLoader$BlockStateDefinition;<init>(Ljava/util/Map;)V"))
	private static void injectBlockStateModels(List<BlockStatesLoader.BlockStateDefinition> blockStatesx, CallbackInfoReturnable<BlockStatesLoader.BlockStateDefinition> cir, @Local Map<ModelIdentifier, BlockStatesLoader.BlockModel> map) {
		ModelModule.BLOCK_STATE_MODELS.visit(CacheStatus.LOAD, map::putAll);
	}

	@Inject(method = "method_62653", at = @At(value = "TAIL", target = "Lnet/minecraft/resource/ResourceFinder;findAllResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
	private static void thing(ResourceManager resourceManager, CallbackInfoReturnable<Map<Identifier, Resource>> cir) {
		ModelModule.MISSING_BLOCK_STATE_MODELS.visit(CacheStatus.LOAD, cir.getReturnValue().keySet()::retainAll);
	}
}
