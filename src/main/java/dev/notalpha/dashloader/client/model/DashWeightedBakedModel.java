package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.components.DashWeightedModelEntry;
import dev.notalpha.dashloader.mixin.accessor.WeightedBakedModelAccessor;
import java.util.function.Function;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.random.DataPool;

import java.util.ArrayList;
import java.util.List;

public final class DashWeightedBakedModel implements DashObject<WeightedBakedModel, DashWeightedBakedModel.DazyImpl> {
	public final List<DashWeightedModelEntry> models;

	public DashWeightedBakedModel(List<DashWeightedModelEntry> models) {
		this.models = models;
	}

	public DashWeightedBakedModel(WeightedBakedModel model, RegistryWriter writer) {
		this.models = new ArrayList<>();
		for (var weightedModel : ((WeightedBakedModelAccessor) model).getModels().getEntries()) {
			this.models.add(new DashWeightedModelEntry(weightedModel, writer));
		}
	}

	@Override
	public DazyImpl export(RegistryReader reader) {
		var modelsOut = new ArrayList<DazyImpl.Entry>();
		for (DashWeightedModelEntry model : this.models) {
			modelsOut.add(model.export(reader));
		}
		return new DazyImpl(modelsOut);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashWeightedBakedModel that = (DashWeightedBakedModel) o;

		return models.equals(that.models);
	}

	@Override
	public int hashCode() {
		return models.hashCode();
	}

	public static class DazyImpl extends Dazy<WeightedBakedModel> {
		public final List<Entry> entries;

		public DazyImpl(List<Entry> entries) {
			this.entries = entries;
		}

		@Override
		protected WeightedBakedModel resolve(Function<Material, TextureAtlasSprite> spriteLoader) {
			DataPool.Builder<BakedModel> models = DataPool.builder();
			for (Entry entry : this.entries) {
				BakedModel model = entry.model.get(spriteLoader);
				models.add(model, entry.weight);
			}
			return new WeightedBakedModel(models.build());
		}

		public static class Entry {
			public final int weight;
			public final Dazy<? extends BakedModel> model;

			public Entry(int weight, Dazy<? extends BakedModel> model) {
				this.weight = weight;
				this.model = model;
			}
		}
	}
}
