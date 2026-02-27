package dev.notalpha.dashloader.client;

import java.util.function.Function; // TODO: was SpriteGetter - verify replacement with Function or equivalent
import org.jetbrains.annotations.Nullable;

// its lazy, but dash! Used for resolution of sprites.
public abstract class Dazy<V> {
	@Nullable
	private transient V loaded;

	protected abstract V resolve(Function<ResourceLocation, TextureAtlasSprite> /* TODO: verify replacement */ spriteLoader);

	public V get(Function<ResourceLocation, TextureAtlasSprite> /* TODO: verify replacement */ spriteLoader) {
		if (loaded != null) {
			return loaded;
		}

		loaded = resolve(spriteLoader);
		return loaded;
	}
}
