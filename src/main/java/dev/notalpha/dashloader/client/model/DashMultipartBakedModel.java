package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.mixin.accessor.MultipartBakedModelAccessor;
import dev.notalpha.dashloader.mixin.accessor.MultipartModelComponentAccessor;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Function;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.MultiPartBakedModel;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashObject<MultiPartBakedModel, DashMultipartBakedModel.DazyImpl> {
	public final List<Component> components;

	public DashMultipartBakedModel(List<Component> components) {
		this.components = components;
	}

	public DashMultipartBakedModel(MultiPartBakedModel model, RegistryWriter writer) {
		var access = ((MultipartBakedModelAccessor) model);
		var accessComponents = access.getSelectors();
		int size = accessComponents.size();
		this.components = new ArrayList<>();

		var modelMap = ModelModule.UNBAKED_TO_BAKED_MULTIPART_MODELS.get(CacheStatus.SAVE);
		var predicates = ModelModule.MULTIPART_PREDICATES.get(CacheStatus.SAVE);
		var pair = predicates.get(modelMap.get(model));

		var stateManagerIdentifier = ModelModule.getStateManagerIdentifier(pair.getRight());
		for (int i = 0; i < size; i++) {
			var componentModel = accessComponents.get(i).model();
			var selector = ((MultipartModelComponentAccessor) pair.getLeft().get(i)).getSelector();

			this.components.add(new Component(
					writer.add(componentModel),
					writer.add(selector),
					writer.add(stateManagerIdentifier)
			));
		}
	}

	@Override
	public DazyImpl export(RegistryReader reader) {
		List<DazyImpl.Component> componentsOut = new ArrayList<>(this.components.size());
		this.components.forEach(component -> {
			Dazy<? extends BakedModel> compModel = reader.get(component.model);
			ResourceLocation compIdentifier = reader.get(component.identifier);
			Condition compSelector = reader.get(component.selector);
			Predicate<BlockState> predicate = compSelector.getPredicate(ModelModule.getStateManager(compIdentifier));
			componentsOut.add(new DazyImpl.Component(compModel, predicate));
		});
		return new DazyImpl(componentsOut);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashMultipartBakedModel that = (DashMultipartBakedModel) o;

		return components.equals(that.components);
	}

	@Override
	public int hashCode() {
		return components.hashCode();
	}

	public static final class Component {
		public final int model;
		public final int selector;
		public final int identifier;

		public Component(int model, int selector, int identifier) {
			this.model = model;
			this.selector = selector;
			this.identifier = identifier;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Component component = (Component) o;

			if (model != component.model) return false;
			if (selector != component.selector) return false;
			return identifier == component.identifier;
		}

		@Override
		public int hashCode() {
			int result = model;
			result = 31 * result + selector;
			result = 31 * result + identifier;
			return result;
		}
	}

	public static class DazyImpl extends Dazy<MultiPartBakedModel> {
		public final List<Component> components;

		public DazyImpl(List<Component> components) {
			this.components = components;
		}

		@Override
		protected MultiPartBakedModel resolve(Function<Material, TextureAtlasSprite> spriteLoader) {
			List<MultiPartBakedModel.Selector> componentsOut = new ArrayList<>(this.components.size());

			for (Component component : components) {
				var model = component.model.get(spriteLoader);
				var selector = component.selector;
				componentsOut.add(new MultiPartBakedModel.Selector(selector, model));
			}

			MultiPartBakedModel multipartBakedModel = new MultiPartBakedModel(componentsOut);
			MultipartBakedModelAccessor access = (MultipartBakedModelAccessor) multipartBakedModel;
			// Fixes race condition which strangely does not happen in vanilla a ton?
			access.setStateCache(Collections.synchronizedMap(access.getStateCache()));
			return multipartBakedModel;
		}

		public static class Component {
			public final Dazy<? extends BakedModel> model;
			public final Predicate<BlockState> selector;

			public Component(Dazy<? extends BakedModel> model, Predicate<BlockState> selector) {
				this.model = model;
				this.selector = selector;
			}
		}
	}
}
