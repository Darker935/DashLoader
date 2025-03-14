package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = ModelLoader.class, priority = 69420)
public abstract class ModelLoaderMixin {
	@Mutable
	@Shadow
	@Final
	private Map<Identifier, UnbakedModel> unbakedModels;
	@Mutable
	@Shadow
	@Final
	private Map<ModelIdentifier, UnbakedModel> modelsToBake;

	@Inject(
			method = "<init>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BlockStatesLoader;<init>(Ljava/util/Map;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/client/render/model/UnbakedModel;Lnet/minecraft/client/color/block/BlockColors;Ljava/util/function/BiConsumer;)V", shift = At.Shift.BEFORE)
	)
	private void injectLoadedModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<ModelLoader>> blockStates, CallbackInfo ci) {
		ModelModule.MODELS_LOAD.visit(CacheStatus.LOAD, dashModels -> {
			int total = dashModels.size();
			dashModels.keySet().removeIf(k -> unbakedModels.containsKey(k.id()));
			this.modelsToBake.keySet().forEach(dashModels::remove);
			DashLoader.LOG.info("Injecting {}/{} Cached Models", dashModels.size(), total);

			dashModels.forEach((k, v) -> {
				var id = Objects.equals(k.getVariant(), ModelIdentifier.INVENTORY_VARIANT) ? k.id().withPrefixedPath("item/") : k.id();
				this.unbakedModels.put(id, v);
			});
			this.modelsToBake.putAll(dashModels);
		});
	}

	@Inject(
			method = "bake",
			at = @At(
					value = "HEAD"
			)
	)
	private void countModels(ModelLoader.SpriteGetter spliteGetter, CallbackInfo ci) {
		if (ModelModule.MODELS_LOAD.active(CacheStatus.LOAD)) {
			// Cache stats
			int cachedModels = 0;
			int fallbackModels = 0;
			for (UnbakedModel value : this.modelsToBake.values()) {
				if (value instanceof UnbakedBakedModel) {
					cachedModels += 1;
				} else {
					fallbackModels += 1;
				}
			}
			long totalModels = cachedModels + fallbackModels;
			DashLoader.LOG.info("{}% Cache coverage", (int) (((cachedModels / (float) totalModels) * 100)));
			DashLoader.LOG.info("with {} Fallback models", fallbackModels);
			DashLoader.LOG.info("and  {} Cached models", cachedModels);
		}
	}

//    @Inject(
//        method = "bake",
//        at = @At(
//            value = "TAIL"
//        )
//    )
//    private void debug(BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader, CallbackInfo ci) {
//var models = new HashMap<Identifier, BakedModel>();
//this.bakedModels.forEach((identifier, bakedModel) -> {
//	if (
//			bakedModel.getClass() == BasicBakedModel.class ||
//			bakedModel.getClass() == MultipartBakedModel.class ||
//			bakedModel.getClass() == WeightedBakedModel.class ||
//					bakedModel.getClass() == BuiltinBakedModel.class
//	) {
//		return;
//	}
//
//	models.put(identifier, bakedModel);
//});
//		System.out.println();

//
	//String dump = ObjectDumper.dump(new ObjectDumper.Wrapper(models));
	//try {
	//	Files.writeString(Path.of("./output." + DashLoaderClient.CACHE.getStatus()), dump);
	//} catch (IOException e) {
	//	throw new RuntimeException(e);
	//}
//    }
}
