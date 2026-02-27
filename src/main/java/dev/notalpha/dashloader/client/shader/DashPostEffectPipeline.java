package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.renderer.PostEffectPipeline;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class DashPostEffectPipeline implements DashObject<PostEffectPipeline, PostEffectPipeline> {
	public final IntIntList targets;
	public final int[] passes;

	public DashPostEffectPipeline(PostEffectPipeline pipeline, RegistryWriter writer) {
		this.targets = new IntIntList(new ArrayList<>(pipeline.internalTargets().size()));
		pipeline.internalTargets().forEach((identifier, targets) -> {
			this.targets.put(writer.add(identifier), writer.add(targets));
		});

		this.passes = new int[pipeline.passes().size()];
		for (int i = 0; i < this.passes.length; i++) {
			this.passes[i] = writer.add(pipeline.passes().get(i));
		}
	}

	public DashPostEffectPipeline(IntIntList targets, int[] passes) {
		this.targets = targets;
		this.passes = passes;
	}

	@Override
	public PostEffectPipeline export(RegistryReader reader) {
		var targets = new HashMap<ResourceLocation, PostEffectPipeline.Targets>(this.targets.list().size());
		var passes = new ArrayList<PostEffectPipeline.Pass>(this.passes.length);
		this.targets.forEach((key, value) -> targets.put(reader.get(key), reader.get(value)));
		for (int pass : this.passes) {
			passes.add(reader.get(pass));
		}
		return new PostEffectPipeline(targets, passes);
	}
}
