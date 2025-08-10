package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultipartUnbakedModel.class)
public abstract class MultipartUnbakedModelMixin {
	@Inject(
			method = "bake",
			at = @At(value = "RETURN")
	)
	private void addPredicateInfo(Baker baker, CallbackInfoReturnable<BakedModel> cir) {
		ModelModule.UNBAKED_TO_BAKED_MULTIPART_MODELS.visit(CacheStatus.SAVE, map -> {
			map.put(cir.getReturnValue(), (MultipartUnbakedModel) (Object) this);
		});
	}
}
