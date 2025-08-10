package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Mixin(value = BakedModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@WrapOperation(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelBaker;bake(Lnet/minecraft/client/render/model/ModelBaker$ErrorCollectingSpriteGetter;)Lnet/minecraft/client/render/model/ModelBaker$BakedModels;"))
	private static ModelBaker.BakedModels yoinkBakedModels(ModelBaker instance, ModelBaker.ErrorCollectingSpriteGetter spriteGetter, Operation<ModelBaker.BakedModels> original) {
		var bakedModels = original.call(instance, spriteGetter);
//		ModelModule.ITEM_MODELS_SAVE.visit(CacheStatus.SAVE, map -> map.putAll(bakedModels.itemStackModels()));
//		ModelModule.ITEM_PROPERTIES.visit(CacheStatus.SAVE, map -> map.putAll(bakedModels.itemProperties()));
		ModelModule.BLOCK_MODELS_SAVE.visit(CacheStatus.SAVE, map -> map.putAll(bakedModels.blockStateModels()));
		ModelModule.BAKED_MODEL_PARTS.visit(CacheStatus.SAVE, map -> map.putAll(instance.bakedModelCache));
		return bakedModels;
	}

	@ModifyReturnValue(method = "method_45899", at = @At(value = "RETURN"))
	private static CompletionStage<?> injectModels(CompletionStage<Map<Identifier, UnbakedModel>> original) {
		var models = ModelModule.MODEL_PARTS.get(CacheStatus.LOAD);
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
		var modelIds = ModelModule.MISSING_MODEL_PARTS.get(CacheStatus.LOAD);
		if (modelIds != null) {
			var out = new HashMap<Identifier, Resource>(modelIds.size());
			modelIds.forEach(id -> resourceManager.getResource(id).ifPresent(resource -> out.put(id, resource)));
			return out;
		}

		return original.call(instance, resourceManager);
	}
}
