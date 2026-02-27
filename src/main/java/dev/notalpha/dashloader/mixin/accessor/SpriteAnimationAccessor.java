package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SpriteContents.AnimatedTexture.class)
public interface SpriteAnimationAccessor {
	@Invoker("<init>")
	static SpriteContents.AnimatedTexture init(SpriteContents parent, List<SpriteContents.FrameInfo> frames, int frameCount, boolean interpolation) {
		throw new AssertionError();
	}

	@Accessor
	List<SpriteContents.FrameInfo> getFrames();

	@Accessor
	int getFrameCount();

	@Accessor
	boolean getInterpolation();
}
