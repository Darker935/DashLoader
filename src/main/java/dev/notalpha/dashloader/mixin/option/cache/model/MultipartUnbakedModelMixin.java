package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MultipartUnbakedModel.class)
public abstract class MultipartUnbakedModelMixin {
	@Inject(
			method = "bake",
			at = @At(value = "RETURN")
	)
	private void addPredicateInfo(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, CallbackInfoReturnable<BakedModel> cir) {
		ModelModule.UNBAKED_TO_BAKED_MULTIPART_MODELS.visit(CacheStatus.SAVE, map -> {
			map.put(cir.getReturnValue(), (MultipartUnbakedModel) (Object) this);
		});
	}
}
