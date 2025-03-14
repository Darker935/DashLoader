package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.render.VertexFormatElement;

import java.util.ArrayList;
import java.util.List;

public class DashVertexFormatElement implements DashObject<VertexFormatElement, VertexFormatElement> {
	public static final List<VertexFormatElement> BUILT_IN = new ArrayList<>();

	static {
		BUILT_IN.add(VertexFormatElement.POSITION);
		BUILT_IN.add(VertexFormatElement.COLOR);
		BUILT_IN.add(VertexFormatElement.UV_0);
		BUILT_IN.add(VertexFormatElement.UV_1);
		BUILT_IN.add(VertexFormatElement.UV_2);
		BUILT_IN.add(VertexFormatElement.NORMAL);
	}

	@DataNullable
	public final DashVertexFormatElementData data;
	public final int builtin;

	public DashVertexFormatElement(@DataNullable DashVertexFormatElementData data, int builtin) {
		this.data = data;
		this.builtin = builtin;
	}

	public DashVertexFormatElement(VertexFormatElement element) {
		var builtin = -1;
		for (int i = 0; i < BUILT_IN.size(); i++) {
			if (BUILT_IN.get(i) == element) {
				builtin = i;
				break;
			}
		}
		this.data = builtin == -1 ? new DashVertexFormatElementData(element) : null;
		this.builtin = builtin;
	}

	@Override
	public VertexFormatElement export(RegistryReader reader) {
		if (this.builtin != -1) {
			return BUILT_IN.get(this.builtin);
		} else {
			return new VertexFormatElement(this.data.id, this.data.uvIndex, this.data.type, this.data.usage, this.data.count);
		}
	}

	public static class DashVertexFormatElementData {
		public final VertexFormatElement.ComponentType type;
		public final VertexFormatElement.Usage usage;
		public final int id;
		public final int uvIndex;
		public final int count;

		public DashVertexFormatElementData(VertexFormatElement.ComponentType type, VertexFormatElement.Usage usage, int id, int i, int count) {
			this.type = type;
			this.usage = usage;
			this.id = id;
			this.uvIndex = i;
			this.count = count;
		}

		public DashVertexFormatElementData(VertexFormatElement element) {
			this.type = element.type();
			this.usage = element.usage();
			this.id = element.id();
			this.uvIndex = element.uvIndex();
			this.count = element.count();
		}
	}
}
