package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import net.minecraft.client.renderer.ShaderProgramConfig;

import java.util.ArrayList;
import java.util.List;


public class DashShaderProgramDefinitionUniform implements DashObject<ShaderProgramConfig.Uniform, ShaderProgramConfig.Uniform> {
	public final String name;
	public final String type;
	public final int count;
	public final float[] values;

	public DashShaderProgramDefinitionUniform(ShaderProgramConfig.Uniform uniform) {
		this.name = uniform.name();
		this.type = uniform.type();
		this.count = uniform.count();
		this.values = new float[uniform.values().size()];
		for (int i = 0; i < uniform.values().size(); i++) {
			this.values[i] = uniform.values().get(i);
		}
	}

	public DashShaderProgramDefinitionUniform(String name, String type, int count, float[] values) {
		this.name = name;
		this.type = type;
		this.count = count;
		this.values = values;
	}

	@Override
	public ShaderProgramConfig.Uniform export(RegistryReader reader) {
		List<Float> values = new ArrayList<>(this.values.length);
		for (float f : this.values) {
			values.add(f);
		}
		return new ShaderProgramConfig.Uniform(this.name, this.type, this.count, values);
	}
}
