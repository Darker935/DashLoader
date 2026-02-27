package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleBakedModel.class)
public interface BasicBakedModelAccessor {
	@Accessor
	boolean getUsesAo();

	@Accessor
	boolean getHasDepth();

	@Accessor
	boolean getIsSideLit();

	@Accessor
	TextureAtlasSprite getSprite();

	@Accessor
	ItemTransforms getTransformation();
}
