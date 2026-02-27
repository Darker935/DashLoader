package dev.notalpha.dashloader.client.model.fallback;

import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.components.DashModelBakeSettings;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

import java.util.Map;

/**
 * Same as {@linkplain UnbakedBakedModel}, but respects provided bake settings
 */
public class MultiUnbakedBakedModel implements UnbakedModel {
	private final Map<DashModelBakeSettings.BakeSettings, Dazy<? extends BakedModel>> models;

	public MultiUnbakedBakedModel(Map<DashModelBakeSettings.BakeSettings, Dazy<? extends BakedModel>> models) {
		this.models = models;
	}

	@Override
	public void resolve(Resolver resolver) {
	}

	@Override
	public BakedModel bake(ModelTextures textures, Baker baker, ModelState /* TODO: verify Mojang name */ settings, boolean ambientOcclusion, boolean isSideLit, ItemTransforms transformation) {
		var model = this.models.get(new DashModelBakeSettings.BakeSettings(settings));
		return model.get(baker.getSpriteGetter());
	}
}
