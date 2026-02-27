package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.Baker; // TODO: verify Mojang name
import net.minecraft.client.renderer.block.model.MultipartUnbakedModel; // TODO: verify Mojang name
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
