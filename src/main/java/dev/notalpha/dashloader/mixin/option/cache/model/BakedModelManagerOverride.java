package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = BakedModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@Shadow
	private Map<ModelIdentifier, BakedModel> models;

	@Inject(method = "upload",
			at = @At(value = "TAIL")
	)
	private void yankAssets(BakedModelManager.BakingResult bakingResult, Profiler profiler, CallbackInfo ci) {
		ModelModule.MODELS_SAVE.visit(CacheStatus.SAVE, map -> {
			DashLoader.LOG.info("Yanking Minecraft Assets");
			map.putAll(this.models);
		});
	}

	// dont compute things that wont be used
	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;reloadModels(Lnet/minecraft/resource/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
	private CompletableFuture<Map<Identifier, JsonUnbakedModel>> maybeBypassReloadModels(ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<Map<Identifier, JsonUnbakedModel>>> original) {
		if (ModelModule.MODELS_LOAD.active(CacheStatus.LOAD)) {
			return CompletableFuture.completedFuture(new HashMap<>());
		}
		return original.call(resourceManager, executor);
	}

	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;reloadBlockStates(Lnet/minecraft/resource/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
	private CompletableFuture<Map<Identifier, List<BlockStatesLoader.SourceTrackedData>>> maybeBypassReloadBlockStates(ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<Map<Identifier, List<BlockStatesLoader.SourceTrackedData>>>> original) {
		if (ModelModule.MODELS_LOAD.active(CacheStatus.LOAD)) {
			return CompletableFuture.completedFuture(new HashMap<>());
		}
		return original.call(resourceManager, executor);
	}
}
