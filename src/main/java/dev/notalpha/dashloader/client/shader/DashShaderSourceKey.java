package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import com.mojang.blaze3d.shaders.ShaderType;
import net.minecraft.client.renderer.ShaderManager;

public class DashShaderSourceKey implements DashObject<ShaderManager.ShaderSourceKey, ShaderManager.ShaderSourceKey> {
	public final int id;
	public final int type;

	public DashShaderSourceKey(ShaderManager.ShaderSourceKey key, RegistryWriter writer) {
		this.id = writer.add(key.id());
		this.type = key.type().ordinal();
	}

	public DashShaderSourceKey(int id, int type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public ShaderManager.ShaderSourceKey export(RegistryReader reader) {
		return new ShaderManager.ShaderSourceKey(reader.get(this.id), ShaderType.values()[this.type]);
	}
}
