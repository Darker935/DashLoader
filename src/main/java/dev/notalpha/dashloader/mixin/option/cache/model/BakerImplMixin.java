package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBaker.BakerImpl.class)
public abstract class BakerImplMixin {
	@Inject(
			method = "bake(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;",
			at = @At(value = "RETURN", ordinal = 1)
	)
	private void thing(Identifier id, ModelBakeSettings settings, CallbackInfoReturnable<BakedModel> cir) {
		ModelModule.BAKED_MODEL_PARTS.visit(CacheStatus.SAVE, map -> map.put(id, cir.getReturnValue()));
	}
}
