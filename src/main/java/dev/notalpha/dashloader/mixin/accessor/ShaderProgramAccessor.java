package dev.notalpha.dashloader.mixin.accessor;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderProgramConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(CompiledShaderProgram.class)
public interface ShaderProgramAccessor {
	@Accessor
	List<ShaderProgramConfig.Sampler> getSamplers();

	@Accessor
	@Mutable
	void setSamplers(List<ShaderProgramConfig.Sampler> samplers);

	@Accessor
	Object2IntMap<String> getSamplerTextures();

	@Accessor
	@Mutable
	void setSamplerTextures(Object2IntMap<String> samplerTextures);

	@Accessor
	IntList getSamplerLocations();

	@Accessor
	@Mutable
	void setSamplerLocations(IntList samplerLocations);

	@Accessor
	List<Uniform> getUniforms();

	@Accessor
	@Mutable
	void setUniforms(List<Uniform> uniforms);

	@Accessor
	Map<String, Uniform> getUniformsByName();

	@Accessor
	@Mutable
	void setUniformsByName(Map<String, Uniform> uniformsByName);

	@Accessor
	@Mutable
	Map<String, ShaderProgramConfig.Uniform> getUniformDefinitionsByName();

	@Accessor
	@Mutable
	void setGlRef(int glRef);
}


