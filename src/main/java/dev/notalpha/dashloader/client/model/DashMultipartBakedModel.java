package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.mixin.accessor.MultipartBakedModelAccessor;
import dev.notalpha.dashloader.mixin.accessor.MultipartModelComponentAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class DashMultipartBakedModel implements DashObject<MultipartBakedModel, DashMultipartBakedModel.DazyImpl> {
	public final List<Component> components;

	public DashMultipartBakedModel(List<Component> components) {
		this.components = components;
	}

	public DashMultipartBakedModel(MultipartBakedModel model, RegistryWriter writer) {
		var access = ((MultipartBakedModelAccessor) model);
		var accessComponents = access.getComponents();
		int size = accessComponents.size();
		this.components = new ArrayList<>();

		var a = ModelModule.UNBAKED_TO_BAKED_MULTIPART_MODELS.get(CacheStatus.SAVE);
		var asdf = ModelModule.MULTIPART_PREDICATES.get(CacheStatus.SAVE);
		var theThing = asdf.get(a.get(model));

		var stateManagerIdentifier = ModelModule.getStateManagerIdentifier(theThing.getRight());
		for (int i = 0; i < size; i++) {
			var componentModel = accessComponents.get(i).model();
			var selector = ((MultipartModelComponentAccessor) theThing.getLeft().get(i)).getSelector();

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
			Identifier compIdentifier = reader.get(component.identifier);
			MultipartModelSelector compSelector = reader.get(component.selector);
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

	public static class DazyImpl extends Dazy<MultipartBakedModel> {
		public final List<Component> components;

		public DazyImpl(List<Component> components) {
			this.components = components;
		}

		@Override
		protected MultipartBakedModel resolve(Function<SpriteIdentifier, Sprite> spriteLoader) {
			List<MultipartBakedModel.class_10204> componentsOut = new ArrayList<>(this.components.size());

			for (Component component : components) {
				var model = component.model.get(spriteLoader);
				var selector = component.selector;
				componentsOut.add(new MultipartBakedModel.class_10204(selector, model));
			}

			MultipartBakedModel multipartBakedModel = new MultipartBakedModel(componentsOut);
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
