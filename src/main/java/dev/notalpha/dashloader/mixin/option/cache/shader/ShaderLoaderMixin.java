package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.gl.*;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.*;

@Mixin(ShaderLoader.class)
public abstract class ShaderLoaderMixin {
	@Shadow
	private ShaderLoader.Cache cache;

	@WrapOperation(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0))
	private Set skipCached(Map instance, Operation<Set<Map.Entry<Identifier, Resource>>> original, @Local(name = "builder") ImmutableMap.Builder<Identifier, ShaderProgramDefinition> builder, @Local(name = "builder2") ImmutableMap.Builder<ShaderLoader.ShaderSourceKey, String> builder2) {
		var og = original.call(instance);
		ShaderModule.SHADER_SOURCES.visit(CacheStatus.LOAD, map -> {
			og.removeIf(entry -> map.containsKey(entry.getKey()));
			builder2.putAll(map.values());
		});
		ShaderModule.SHADER_DEFINITIONS.visit(CacheStatus.LOAD, map -> {
			og.removeIf(entry -> map.containsKey(entry.getKey()));
			builder.putAll(map.values());
		});

		return og;
	}

//	@WrapOperation(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 1))
//	private Set a(Map instance, Operation<Set> original) {
//		var og = original.call(instance); //TODO
//
//		return og;
//	}

	@ModifyArgs(method = "loadShaderSource", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", remap = false))
	private static void thing(Args args, @Local(name = "id") Identifier id){
		ShaderModule.SHADER_SOURCES.visit(CacheStatus.SAVE, map -> map.put(id, Map.entry(args.get(0), args.get(1))));
	}

	@ModifyArgs(method = "loadDefinition", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", remap = false))
	private static void thing2(Args args, @Local(name = "id") Identifier id) {
		ShaderModule.SHADER_DEFINITIONS.visit(CacheStatus.SAVE, map -> map.put(id, Map.entry(args.get(0), args.get(1))));
	}

	@ModifyArgs(method = "loadPostEffect(Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/Resource;Lcom/google/common/collect/ImmutableMap$Builder;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
	private static void thing3(Args args, @Local(name = "id") Identifier id) {
		ShaderModule.POST_EFFECTS.visit(CacheStatus.SAVE, map -> map.put(id, Map.entry(args.get(0), args.get(1))));
	}

//	@Inject(method = "apply(Lnet/minecraft/client/gl/ShaderLoader$Definitions;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
//	private void yankShaders(ShaderLoader.Definitions definitions, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
//		ShaderModule.SHADERS.visit(CacheStatus.SAVE, map -> {
//			cache.shaderPrograms.forEach((k, v) -> {
//				v.ifPresent(shaderProgram -> map.put(k, shaderProgram));
//			});
//		});
//	}
//
//	@WrapOperation(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/gl/ShaderLoader$Definitions;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0))
//	private Set skipCached(Map instance, Operation<Set<Map.Entry<Identifier, Resource>>> original) {
//		var og = original.call(instance);
//		ShaderModule.SHADERS.visit(CacheStatus.LOAD, map -> {
//			map.keySet().forEach(key -> og.removeIf(entry -> key.configId() == entry.getKey()));
//		});
//
//		return og;
//	}
}
