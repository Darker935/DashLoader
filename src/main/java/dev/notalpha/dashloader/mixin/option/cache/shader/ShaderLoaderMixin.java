package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {
	@Shadow private ShaderLoader.Cache cache;

	@WrapMethod(method = "preload")
	private void injectShaders(ResourceFactory factory, ShaderProgramKey[] keys, Operation<Void> original) {
		HashMap<Identifier, ShaderProgram> shaders = ShaderModule.SHADERS.get(CacheStatus.LOAD);

		if (shaders != null) {
			ArrayList<ShaderProgramKey> unloaded = new ArrayList<>();
			for (ShaderProgramKey key : keys) {
				var shader = shaders.get(key.configId());
				if (shader != null) {
					this.cache.shaderPrograms.put(key, Optional.of(shader));
				} else {
					unloaded.add(key);
				}
			}

			if (!unloaded.isEmpty()) {
				original.call(factory, unloaded.toArray(ShaderProgramKey[]::new));
			}
		} else {
			original.call(factory, keys);
		}
	}

	@ModifyArgs(method = "preload", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private void put(Args args){
		ShaderModule.SHADERS.visit(CacheStatus.SAVE, map -> {
			map.put(((ShaderProgramKey) args.get(0)).configId(), args.get(1));
		});
	}
}
