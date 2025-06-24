package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.ShaderLoader.ShaderSourceKey;

public class DashShaderSourceKey implements DashObject<ShaderSourceKey, ShaderSourceKey> {
	public final int id;
	public final int type;

	public DashShaderSourceKey(ShaderSourceKey key, RegistryWriter writer) {
		this.id = writer.add(key.id());
		this.type = key.type().ordinal();
	}

	public DashShaderSourceKey(int id, int type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public ShaderSourceKey export(RegistryReader reader) {
		return new ShaderSourceKey(reader.get(this.id), CompiledShader.Type.values()[this.type]);
	}
}
