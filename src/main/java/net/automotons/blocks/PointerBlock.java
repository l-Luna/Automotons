package net.automotons.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;

public class PointerBlock extends HorizontalFacingBlock{
	
	public PointerBlock(Settings settings){
		super(settings);
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder){
		builder.add(FACING);
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx){
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}
}