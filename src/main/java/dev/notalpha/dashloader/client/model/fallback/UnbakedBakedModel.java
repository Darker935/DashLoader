package dev.notalpha.dashloader.client.model.fallback;

import dev.notalpha.dashloader.client.Dazy;
import net.minecraft.client.renderer.block.model.*; // TODO: verify Mojang package
import net.minecraft.client.renderer.block.model.ItemTransforms;

/**
 * An unbaked model which holds a baked model, used for fallback to reuse cached models.
 */
public class UnbakedBakedModel implements UnbakedModel {
	private final Dazy<? extends BakedModel> bakedModel;

	public UnbakedBakedModel(Dazy<? extends BakedModel> bakedModel) {
		this.bakedModel = bakedModel;
	}

	@Override
	public void resolve(Resolver resolver) {
	}

	@Override
	public BakedModel bake(ModelTextures textures, Baker baker, ModelState /* TODO: verify Mojang name */ settings, boolean ambientOcclusion, boolean isSideLit, ItemTransforms transformation) {
		return this.bakedModel.get(baker.getSpriteGetter());
	}
}
