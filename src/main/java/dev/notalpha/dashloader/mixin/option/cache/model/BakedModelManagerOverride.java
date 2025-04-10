package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Mixin(value = BakedModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@ModifyReturnValue(method = "method_45899", at = @At(value = "RETURN"))//, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;reloadModels(Lnet/minecraft/resource/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
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
		};

		return original;
	}

	@WrapOperation(method = "method_62663", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
	private static Map<Identifier, Resource> loadMissingModels(ResourceFinder instance, ResourceManager resourceManager, Operation<Map<Identifier, Resource>> original) {
		var modelIds = ModelModule.MISSING_MODELS.get(CacheStatus.LOAD);
		if (modelIds != null) {
			var out = new HashMap<Identifier, Resource>(modelIds.size());
			for (Identifier id : modelIds) {
				Optional<Resource> resource = resourceManager.getResource(id);
				out.put(id, resource.get()); // the id is from the resource manager, should be present
			}
			return out;
		}

		return original.call(instance, resourceManager);
	}
}
