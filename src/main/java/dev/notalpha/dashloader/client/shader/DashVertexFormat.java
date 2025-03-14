package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashVertexFormat implements DashObject<VertexFormat, VertexFormat> {
	public static final List<VertexFormat> BUILT_IN = new ArrayList<>();

	static {
		BUILT_IN.add(VertexFormats.BLIT_SCREEN);
		BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
		BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		BUILT_IN.add(VertexFormats.POSITION);
		BUILT_IN.add(VertexFormats.POSITION_COLOR);
		BUILT_IN.add(VertexFormats.LINES);
		BUILT_IN.add(VertexFormats.POSITION_COLOR_LIGHT);
		BUILT_IN.add(VertexFormats.POSITION_TEXTURE);
		BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR);
		BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
		BUILT_IN.add(VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
		BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
	}

	@DataNullable
	public final Map<String, DashVertexFormatElement> elementMap;
	public final int builtin;

	public DashVertexFormat(Map<String, DashVertexFormatElement> elementMap, int builtin) {
		this.elementMap = elementMap;
		this.builtin = builtin;
	}

	public DashVertexFormat(VertexFormat vertexFormat) {
		int builtin = -1;
		for (int i = 0; i < BUILT_IN.size(); i++) {
			VertexFormat format = BUILT_IN.get(i);
			if (format == vertexFormat) {
				builtin = i;
				break;
			}
		}
		this.builtin = builtin;
		if (builtin == -1) {
			this.elementMap = new HashMap<>();
			for (int i = 0; i < vertexFormat.getAttributeNames().size(); i++) {
				elementMap.put(vertexFormat.getAttributeNames().get(i), new DashVertexFormatElement(vertexFormat.getElements().get(i)));
			}
		} else {
			this.elementMap = null;
		}
	}

	@Override
	public VertexFormat export(RegistryReader reader) {
		if (this.builtin != -1) {
			return BUILT_IN.get(this.builtin);
		} else {
			var builder = VertexFormat.builder();
			elementMap.forEach((s, dashVertexFormatElement) -> builder.add(s, dashVertexFormatElement.export(reader)));

			return builder.build();
		}
	}
}
