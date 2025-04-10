package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.ItemModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemModel.BakedItemModel.class)
public interface BakedItemModelAccessor {

	@Accessor
	ModelOverrideList getOverrides();
}
