package dev.notalpha.dashloader.client.model.predicates;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.multipart.Condition; // TODO: verify Mojang name (MultipartModelSelector)
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Predicate;

public class BooleanSelector implements MultipartModelSelector {
	public final boolean selector;

	public BooleanSelector(boolean selector) {
		this.selector = selector;
	}

	public BooleanSelector(MultipartModelSelector selector) {
		this.selector = selector == MultipartModelSelector.TRUE;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> stateFactory) {
		return blockState -> selector;
	}
}
