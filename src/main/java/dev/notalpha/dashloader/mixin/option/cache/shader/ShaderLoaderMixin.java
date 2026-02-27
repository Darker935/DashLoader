package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.ShaderProgramConfig;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(ShaderManager.class)
public abstract class ShaderLoaderMixin {
	@WrapOperation(
			method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/renderer/ShaderManager$Definitions;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/packs/resources/ResourceManager;findResources(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"
			)
	)
	private Map<ResourceLocation, Resource> skipCached(ResourceManager instance, String s, Predicate<ResourceLocation> identifierPredicate, Operation<Map<ResourceLocation, Resource>> original, @Local(ordinal = 0) ImmutableMap.Builder<ResourceLocation, ShaderProgramConfig> builder, @Local(ordinal = 1) ImmutableMap.Builder<ShaderManager.ShaderSourceKey, String> builder2) {
		var sources = ShaderModule.SHADER_SOURCES.get(CacheStatus.LOAD);
		var definitions = ShaderModule.SHADER_DEFINITIONS.get(CacheStatus.LOAD);
		if (sources != null && definitions != null) {
			builder2.putAll(sources);
			builder.putAll(definitions);
			return Map.of();
		}

		return original.call(instance, s, identifierPredicate);
	}

//	@WrapOperation(
//			method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiler/ProfilerFiller;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/resource/ResourceProvider;findResources(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
//			)
//	)
//	private Map<ResourceLocation, Resource> thing(ResourceProvider instance, ResourceManager resourceManager, Operation<Map<ResourceLocation, Resource>> original, @Local(ordinal = 2) ImmutableMap.Builder<ResourceLocation, PostEffectPipeline> builder3) {
//		var post = ShaderModule.POST_EFFECTS.get(CacheStatus.LOAD);
//		if (post != null) {
//			builder3.putAll(post);
//			return Map.of();
//		}
//		return original.call(instance, resourceManager);
//	}

	@WrapMethod(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/renderer/ShaderManager$Definitions;")
	private ShaderManager.Definitions thing(ResourceManager resourceManager, ProfilerFiller profiler, Operation<ShaderManager.Definitions> original) {
		var og = original.call(resourceManager, profiler);
		ShaderModule.SHADER_SOURCES.visit(CacheStatus.SAVE, map -> map.putAll(og.shaderSources()));
		ShaderModule.SHADER_DEFINITIONS.visit(CacheStatus.SAVE, map -> map.putAll(og.programs()));
//		ShaderModule.POST_EFFECTS.visit(CacheStatus.SAVE, map -> map.putAll(og.postChains()));

		return og;
	}
}
