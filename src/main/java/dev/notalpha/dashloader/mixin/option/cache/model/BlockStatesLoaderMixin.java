package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;

@Mixin(BlockStatesLoader.class)
public abstract class BlockStatesLoaderMixin {
	@ModifyReturnValue(method = "method_65719", at = @At("TAIL"))
	private static BlockStatesLoader.BlockStateDefinition inject(BlockStatesLoader.BlockStateDefinition original) {
		ModelModule.RAW_BLOCK_MODELS.visit(CacheStatus.SAVE, map -> map.putAll(original.models()));
		ModelModule.BLOCK_MODELS.visit(CacheStatus.LOAD, original.models()::putAll);
		return original;
	}

	@ModifyReturnValue(method = "method_65717", at = @At("TAIL"))
	private static Map<Identifier, List<Resource>> loadMissing(Map<Identifier, List<Resource>> original) {
		ModelModule.MISSING_BLOCK_MODELS.visit(CacheStatus.LOAD, original.keySet()::retainAll);
		return original;
	}
}
