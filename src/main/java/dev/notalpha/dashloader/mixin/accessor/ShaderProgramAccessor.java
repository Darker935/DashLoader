package dev.notalpha.dashloader.mixin.accessor;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ShaderProgram.class)
public interface ShaderProgramAccessor {
	@Accessor
	List<ShaderProgramDefinition.Sampler> getSamplers();

	@Accessor
	@Mutable
	void setSamplers(List<ShaderProgramDefinition.Sampler> samplers);

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
	List<GlUniform> getUniforms();

	@Accessor
	@Mutable
	void setUniforms(List<GlUniform> uniforms);

	@Accessor
	Map<String, GlUniform> getUniformsByName();

	@Accessor
	@Mutable
	void setUniformsByName(Map<String, GlUniform> uniformsByName);

	@Accessor
	@Mutable
	Map<String, ShaderProgramDefinition.Uniform> getUniformDefinitionsByName();

	@Accessor
	@Mutable
	void setGlRef(int glRef);
}


