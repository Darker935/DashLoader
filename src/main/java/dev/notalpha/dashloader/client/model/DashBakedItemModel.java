package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.components.DashModelOverrideList;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ItemModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.function.Function;

public class DashBakedItemModel implements DashObject<ItemModel.BakedItemModel, DashBakedItemModel.DazyImpl> {
	public final int wrapped;
	public final DashModelOverrideList overrides;

	public DashBakedItemModel(int wrapped, DashModelOverrideList overrides) {
		this.wrapped = wrapped;
		this.overrides = overrides;
	}

	public DashBakedItemModel(ItemModel.BakedItemModel model, RegistryWriter writer) {
		this.wrapped = writer.add(model.wrapped);
		this.overrides = new DashModelOverrideList(model.getOverrides(), writer);
	}

	@Override
	public DazyImpl export(RegistryReader reader) {
		return new DazyImpl(reader.get(this.wrapped), this.overrides.export(reader));
	}

	public static class DazyImpl extends Dazy<ItemModel.BakedItemModel> {
		private final Dazy<? extends BakedModel> wrapped;
		private final DashModelOverrideList.DazyImpl overrides;

		public DazyImpl(Dazy<? extends BakedModel> wrapped, DashModelOverrideList.DazyImpl overrides) {
			this.wrapped = wrapped;
			this.overrides = overrides;
		}

		@Override
		protected ItemModel.BakedItemModel resolve(Function<SpriteIdentifier, Sprite> spriteLoader) {
			return new ItemModel.BakedItemModel(this.wrapped.get(spriteLoader), this.overrides.get(spriteLoader));
		}
	}
}
