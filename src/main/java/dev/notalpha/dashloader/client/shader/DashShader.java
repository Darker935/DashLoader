package dev.notalpha.dashloader.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.ShaderProgramAccessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramDefinition;

import java.util.*;

public final class DashShader implements DashObject<ShaderProgram, ShaderProgram> {
	public final List<String> samplers;
	public final List<DashGlUniform> glUniforms;
	public transient ShaderProgram toApply;

	public DashShader(List<String> samplers, List<DashGlUniform> glUniforms) {
		this.samplers = samplers;
		this.glUniforms = glUniforms;
	}

	public DashShader(ShaderProgram shader, RegistryWriter writer) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) shader;

		this.samplers = new ArrayList<>(shaderAccess.getSamplers().size());
		shaderAccess.getSamplers().forEach((s) -> this.samplers.add(s.name()));
		this.glUniforms = new ArrayList<>();
		shaderAccess.getUniforms().forEach((glUniform) -> this.glUniforms.add(new DashGlUniform(glUniform)));
	}


	@Override
	public ShaderProgram export(RegistryReader reader) {
		this.toApply = UnsafeHelper.allocateInstance(ShaderProgram.class);
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
		//object init
		shaderAccess.setSamplerTextures(new Object2IntArrayMap<>());
		shaderAccess.setSamplerLocations(new IntArrayList());

		shaderAccess.getSamplerTextures().defaultReturnValue(-1);

		shaderAccess.setUniformsByName(new HashMap<>());
//		shaderAccess.setUniformDefinitionsByName(new HashMap());

		//<init> top
//		shaderAccess.setName(this.name);
//		shaderAccess.setFormat(reader.get(this.format));

		var samplersOut = new ArrayList<ShaderProgramDefinition.Sampler>();
		this.samplers.forEach(s -> samplersOut.add(new ShaderProgramDefinition.Sampler(s)));
		shaderAccess.setSamplers(samplersOut);

		final ArrayList<GlUniform> uniforms = new ArrayList<>();
		shaderAccess.setUniforms(uniforms);

		var uniformsOut = new HashMap<String, GlUniform>();
		this.glUniforms.forEach((dashGlUniform) -> {
			GlUniform glUniform = dashGlUniform.export();
			uniforms.add(glUniform);
//			glUniform.getName();
//			glUniform.getDataType();
//			glUniform.getCount();
//			glUniform.getFloatData();
			uniformsOut.put(dashGlUniform.name, glUniform);
//			shaderAccess.getUniformDefinitionsByName().put(dashGlUniform.name, glUniform);
		});


		this.toApply.modelViewMat = uniformsOut.get("ModelViewMat");
		this.toApply.projectionMat = uniformsOut.get("ProjMat");
		this.toApply.textureMat = uniformsOut.get("TextureMat");
		this.toApply.screenSize = uniformsOut.get("ScreenSize");
		this.toApply.colorModulator = uniformsOut.get("ColorModulator");
		this.toApply.light0Direction = uniformsOut.get("Light0_Direction");
		this.toApply.light1Direction = uniformsOut.get("Light1_Direction");
		this.toApply.fogStart = uniformsOut.get("FogStart");
		this.toApply.fogEnd = uniformsOut.get("FogEnd");
		this.toApply.fogColor = uniformsOut.get("FogColor");
		this.toApply.fogShape = uniformsOut.get("FogShape");
		this.toApply.lineWidth = uniformsOut.get("LineWidth");
		this.toApply.gameTime = uniformsOut.get("GameTime");
		return this.toApply;
	}


	@Override
	public void postExport(RegistryReader reader) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
//		shaderAccess.setBlendState(this.blendState.export()); // TODO: link shader parts and stuff
//		shaderAccess.setVertexShader(this.vertexShader.exportProgram());
//		shaderAccess.setFragmentShader(this.fragmentShader.exportProgram());

		final int programId = GlStateManager.glCreateProgram();
		shaderAccess.setGlRef(programId);

//		List<String> names = this.toApply.getFormat().getAttributeNames();
//		for (int i = 0; i < names.size(); i++) {
//			String attributeName = names.get(i);
//			GlUniform.bindAttribLocation(programId, i, attributeName);
//		}
//		GlProgramManager.linkProgram(this.toApply);
//		shaderAccess.loadref();

		for(GlUniform uniform : shaderAccess.getUniforms()) {
			int i = GlUniform.getUniformLocation(programId, uniform.getName());
			if (i != -1) {
				uniform.setLocation(i);
			}
		}

		for(ShaderProgramDefinition.Sampler sampler : shaderAccess.getSamplers()) {
			int j = GlUniform.getUniformLocation(programId, sampler.name());
			shaderAccess.getSamplerLocations().add(j);
		}

		if (programId <= 0) {
			throw new RuntimeException(new ShaderLoader.LoadException("Could not create shader program (returned program ID " + programId + ")"));
		} else {
			// TODO
//			format.bindAttributes(programId);
//			GlStateManager.glAttachShader(programId, vertexShader.getHandle());
//			GlStateManager.glAttachShader(programId, fragmentShader.getHandle());
			GlStateManager.glLinkProgram(programId);
//			int j = GlStateManager.glGetProgrami(programId, 35714);
//			if (j == 0) {
//				String string = GlStateManager.glGetProgramInfoLog(programId, 32768);
//				String var10002 = String.valueOf(vertexShader.getId());
//				throw new ShaderLoader.LoadException("Error encountered when linking program containing VS " + var10002 + " and FS " + String.valueOf(fragmentShader.getId()) + ". Log output: " + string);
//			}
		}
	}

//	public static class Sampler {
//		@DataNullable
//		@DataSubclasses({Integer.class, String.class})
//		public final Object sampler;
//
//		public Sampler(Object sampler) {
//			this.sampler = sampler;
//		}
//	}
}
