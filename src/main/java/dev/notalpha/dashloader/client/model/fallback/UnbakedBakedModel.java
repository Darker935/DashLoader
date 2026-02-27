package dev.notalpha.dashloader.client.model.fallback;

import dev.notalpha.dashloader.client.Dazy;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;

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
	public BakedModel bake(ModelTextures textures, Baker baker, ModelBakeSettings settings, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation) {
		return this.bakedModel.get(baker.getSpriteGetter());
	}
}
