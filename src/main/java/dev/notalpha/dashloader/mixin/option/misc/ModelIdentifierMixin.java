package dev.notalpha.dashloader.mixin.option.misc;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(value = ModelResourceLocation.class, priority = 999)
public abstract class ModelIdentifierMixin {
	@Shadow
	@Final
	private ResourceLocation id;
	@Shadow
	@Final
	private String variant;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ModelResourceLocation that)) {
			return false;
		}
		if (!this.id.equals(that.id())) {
			return false;
		}

		return Objects.equals(this.variant, that.getVariant());
	}

	@Override
	public int hashCode() {
		return 31 * id.hashCode() + variant.hashCode();
	}
}
