package dev.notalpha.dashloader.client.sprite.content;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import java.util.function.Function; // TODO: was SpriteGetter - verify replacement with Function or equivalent
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteIdentifier; // TODO: verify Mojang name

public class DashSprite implements DashObject<TextureAtlasSprite, DashSprite.DazyImpl> {
	public final int id;

	public DashSprite(int id) {
		this.id = id;
	}

	public DashSprite(TextureAtlasSprite sprite, RegistryWriter writer) {
		this.id = writer.add(new SpriteIdentifier(sprite.getAtlasId(), sprite.getContents().getId()));
	}

	@Override
	public DazyImpl export(final RegistryReader registry) {
		return new DazyImpl(registry.get(id));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashSprite that = (DashSprite) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public static class DazyImpl extends Dazy<TextureAtlasSprite> {
		public final SpriteIdentifier location;

		public DazyImpl(SpriteIdentifier location) {
			this.location = location;
		}

		@Override
		protected TextureAtlasSprite resolve(Function<ResourceLocation, TextureAtlasSprite> /* TODO: verify replacement */ spriteLoader) {
			return spriteLoader.get(location);
		}
	}
}
