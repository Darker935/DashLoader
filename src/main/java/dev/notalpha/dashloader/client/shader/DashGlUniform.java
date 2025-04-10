package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.io.IOHelper;
import dev.notalpha.dashloader.mixin.accessor.GlUniformAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.gl.GlUniform;

public final class DashGlUniform {
	public final int dataType;
	public final String name;
	public final int @DataNullable [] intData;
	public final float @DataNullable [] floatData;

	public DashGlUniform(int dataType, String name, int[] intData, float[] floatData) {
		this.dataType = dataType;
		this.name = name;
		this.intData = intData;
		this.floatData = floatData;
	}

	public DashGlUniform(GlUniform glUniform) {
		GlUniformAccessor access = (GlUniformAccessor) glUniform;
		this.intData = IOHelper.toArray(access.getIntData());
		this.floatData = IOHelper.toArray(access.getFloatData());
		this.dataType = glUniform.getDataType();
		this.name = glUniform.getName();
	}

	public GlUniform export() {
		GlUniform glUniform = new GlUniform(this.name, this.dataType, 0);
		GlUniformAccessor access = (GlUniformAccessor) glUniform;
		access.setIntData(IOHelper.fromArray(this.intData));
		access.setFloatData(IOHelper.fromArray(this.floatData));
		return glUniform;
	}
}
