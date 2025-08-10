package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.ObjectObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.components.BakedQuadCollection;
import dev.notalpha.dashloader.client.model.components.DashBakedQuadCollection;
import dev.notalpha.dashloader.client.model.components.DashModelTransformation;
import dev.notalpha.dashloader.client.sprite.content.DashSprite;
import dev.notalpha.dashloader.mixin.accessor.BasicBakedModelAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DashBasicBakedModel implements DashObject<BasicBakedModel, DashBasicBakedModel.DazyImpl> {
	public final int quads;
	public final ObjectObjectList<Direction, Integer> faceQuads;
	public final boolean usesAo;
	public final boolean hasDepth;
	public final boolean isSideLit;
	@DataNullable
	public final DashModelTransformation transformation;
	public final int spritePointer;

	public DashBasicBakedModel(int quads,
	                           ObjectObjectList<Direction, Integer> faceQuads,
	                           boolean usesAo, boolean hasDepth, boolean isSideLit,
	                           DashModelTransformation transformation,
	                           int spritePointer) {
		this.quads = quads;
		this.faceQuads = faceQuads;
		this.usesAo = usesAo;
		this.hasDepth = hasDepth;
		this.isSideLit = isSideLit;
		this.transformation = transformation;
		this.spritePointer = spritePointer;
	}

	public DashBasicBakedModel(BasicBakedModel basicBakedModel, RegistryWriter writer) {
		BasicBakedModelAccessor access = ((BasicBakedModelAccessor) basicBakedModel);

		Random random = Random.create();
		this.quads = writer.add(new BakedQuadCollection(basicBakedModel.getQuads(null, null, random)));
		this.faceQuads = new ObjectObjectList<>();
		for (Direction value : Direction.values()) {
			this.faceQuads.put(value, writer.add(new BakedQuadCollection(basicBakedModel.getQuads(null, value, random))));
		}

		this.usesAo = access.getUsesAo();
		this.hasDepth = access.getHasDepth();
		this.isSideLit = access.getIsSideLit();
		this.transformation = DashModelTransformation.createDashOrReturnNullIfDefault(access.getTransformation());
		this.spritePointer = writer.add(access.getSprite());
	}

	@Override
	public DazyImpl export(final RegistryReader reader) {
		final DashSprite.DazyImpl sprite = reader.get(this.spritePointer);
		final DashBakedQuadCollection.DazyImpl quads = reader.get(this.quads);

		var faceQuads = new HashMap<Direction, DashBakedQuadCollection.DazyImpl>();
		for (var entry : this.faceQuads.list()) {
			DashBakedQuadCollection.DazyImpl collection = reader.get(entry.value());
			faceQuads.put(entry.key(), collection);
		}

		return new DazyImpl(
				quads,
				faceQuads,
				usesAo,
				isSideLit,
				hasDepth,
				DashModelTransformation.exportOrDefault(this.transformation),
				sprite
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashBasicBakedModel that = (DashBasicBakedModel) o;

		if (quads != that.quads) return false;
		if (usesAo != that.usesAo) return false;
		if (hasDepth != that.hasDepth) return false;
		if (isSideLit != that.isSideLit) return false;
		if (spritePointer != that.spritePointer) return false;
		if (!faceQuads.equals(that.faceQuads)) return false;
		return Objects.equals(transformation, that.transformation);
	}

	@Override
	public int hashCode() {
		int result = quads;
		result = 31 * result + faceQuads.hashCode();
		result = 31 * result + (usesAo ? 1 : 0);
		result = 31 * result + (hasDepth ? 1 : 0);
		result = 31 * result + (isSideLit ? 1 : 0);
		result = 31 * result + (transformation != null ? transformation.hashCode() : 0);
		result = 31 * result + spritePointer;
		return result;
	}

	public static class DazyImpl extends Dazy<BasicBakedModel> {
		public final DashBakedQuadCollection.DazyImpl quads;
		public final Map<Direction, DashBakedQuadCollection.DazyImpl> faceQuads;
		public final boolean usesAo;
		public final boolean isSideLit;
		public final boolean hasDepth;
		public final ModelTransformation transformation;
		public final DashSprite.DazyImpl sprite;

		public DazyImpl(DashBakedQuadCollection.DazyImpl quads,
		                Map<Direction, DashBakedQuadCollection.DazyImpl> faceQuads,
		                boolean usesAo,
		                boolean isSideLit,
		                boolean hasDepth,
		                ModelTransformation transformation,
		                DashSprite.DazyImpl sprite) {
			this.quads = quads;
			this.faceQuads = faceQuads;
			this.usesAo = usesAo;
			this.isSideLit = isSideLit;
			this.hasDepth = hasDepth;
			this.transformation = transformation;
			this.sprite = sprite;
		}

		@Override
		protected BasicBakedModel resolve(SpriteGetter spriteLoader) {
			List<BakedQuad> quads = this.quads.get(spriteLoader);
			var faceQuadsOut = new HashMap<Direction, List<BakedQuad>>();
			this.faceQuads.forEach((direction, dazy) -> faceQuadsOut.put(direction, dazy.get(spriteLoader)));

			Sprite sprite = this.sprite.get(spriteLoader);
			return new BasicBakedModel(quads, faceQuadsOut, usesAo, isSideLit, hasDepth, sprite, transformation);
		}
	}
}
