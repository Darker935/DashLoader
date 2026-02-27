package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer; // TODO: verify Mojang name (ResourceMetadata)
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
	@Accessor
	NativeImage getImage();

	@Accessor
	@Mutable
	void setImage(NativeImage image);

	@Accessor
	SpriteContents.AnimatedTexture getAnimation();

	@Accessor
	@Mutable
	void setAnimation(SpriteContents.AnimatedTexture animation);

	@Accessor
	@Mutable
	void setMipmapLevelsImages(NativeImage[] mipmapLevelsImages);

	@Accessor
	@Mutable
	void setId(ResourceLocation id);

	@Accessor
	@Mutable
	void setWidth(int width);

	@Accessor
	@Mutable
	void setHeight(int height);

	@Accessor
	@Mutable
	void setMetadata(ResourceMetadata animation);
}
