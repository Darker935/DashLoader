package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.renderer.ShaderProgramConfig;

import java.util.ArrayList;

public class DashShaderProgramDefinition implements DashObject<ShaderProgramConfig, ShaderProgramConfig> {
	public final int vertex;
	public final int fragment;
	public final String[] samplers;
	public final int[] uniforms;
	public final int defines;

	public DashShaderProgramDefinition(ShaderProgramConfig thing, RegistryWriter writer) {
		this.vertex = writer.add(thing.vertex());
		this.fragment = writer.add(thing.fragment());
		this.samplers = new String[thing.samplers().size()];
		for (int i = 0; i < this.samplers.length; i++) {
			this.samplers[i] = thing.samplers().get(i).name();
		}
		this.uniforms = new int[thing.uniforms().size()];
		for (int i = 0; i < this.uniforms.length; i++) {
			this.uniforms[i] = writer.add(thing.uniforms().get(i));
		}
		this.defines = writer.add(thing.defines());
	}

	public DashShaderProgramDefinition(int vertex, int fragment, String[] samplers, int[] uniforms, int defines) {
		this.vertex = vertex;
		this.fragment = fragment;
		this.samplers = samplers;
		this.uniforms = uniforms;
		this.defines = defines;
	}

	@Override
	public ShaderProgramConfig export(RegistryReader reader) {
		var samplers = new ArrayList<ShaderProgramConfig.Sampler>(this.samplers.length);
		var uniforms = new ArrayList<ShaderProgramConfig.Uniform>(this.uniforms.length);

		for (String sampler : this.samplers) {
			samplers.add(new ShaderProgramConfig.Sampler(sampler));
		}
		for (int uniform : this.uniforms) {
			uniforms.add(reader.get(uniform));
		}

		return new ShaderProgramConfig(reader.get(this.vertex), reader.get(this.fragment),
				samplers, uniforms, reader.get(this.defines));
	}
}
