package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgramDefinition;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(ShaderLoader.class)
public abstract class ShaderLoaderMixin {
	@WrapOperation(
			method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resource/ResourceManager;findResources(Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/Map;"
			)
	)
	private Map<Identifier, Resource> skipCached(ResourceManager instance, String s, Predicate<Identifier> identifierPredicate, Operation<Map<Identifier, Resource>> original, @Local(ordinal = 0) ImmutableMap.Builder<Identifier, ShaderProgramDefinition> builder, @Local(ordinal = 1) ImmutableMap.Builder<ShaderLoader.ShaderSourceKey, String> builder2) {
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
//			method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"
//			)
//	)
//	private Map<Identifier, Resource> thing(ResourceFinder instance, ResourceManager resourceManager, Operation<Map<Identifier, Resource>> original, @Local(ordinal = 2) ImmutableMap.Builder<Identifier, PostEffectPipeline> builder3) {
//		var post = ShaderModule.POST_EFFECTS.get(CacheStatus.LOAD);
//		if (post != null) {
//			builder3.putAll(post);
//			return Map.of();
//		}
//		return original.call(instance, resourceManager);
//	}

	@WrapMethod(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;")
	private ShaderLoader.Definitions thing(ResourceManager resourceManager, Profiler profiler, Operation<ShaderLoader.Definitions> original) {
		var og = original.call(resourceManager, profiler);
		ShaderModule.SHADER_SOURCES.visit(CacheStatus.SAVE, map -> map.putAll(og.shaderSources()));
		ShaderModule.SHADER_DEFINITIONS.visit(CacheStatus.SAVE, map -> map.putAll(og.programs()));
//		ShaderModule.POST_EFFECTS.visit(CacheStatus.SAVE, map -> map.putAll(og.postChains()));

		return og;
	}
}
