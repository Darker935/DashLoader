package dev.notalpha.dashloader.mixin.accessor;

import dev.notalpha.hyphen.thr.HyphenException;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BlockStatesLoader; // TODO: verify Mojang name
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockStatesLoader.class)
public interface ModelLoaderAccessor {
	@Accessor("ITEM_FRAME_STATE_MANAGER")
	static StateDefinition<Block, BlockState> getTheItemFrameThing() {
		throw new HyphenException("froge", "your dad");
	}

	@Accessor("STATIC_DEFINITIONS")
	static Map<ResourceLocation, StateDefinition<Block, BlockState>> getStaticDefinitions() {
		throw new HyphenException("froge", "your dad");
	}
}
