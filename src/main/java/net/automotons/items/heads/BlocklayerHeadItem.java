package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlocklayerHeadItem extends HeadItem<Object>{
	
	public BlocklayerHeadItem(Settings settings){
		super(settings);
	}
	
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Object o){
		World world = automoton.getWorld();
		BlockState state = world.getBlockState(facing);
		if(state.isAir() || state.getPistonBehavior() == PistonBehavior.DESTROY){
			Item item = automoton.getStoreStack().getItem();
			if(item instanceof BlockItem){
				world.breakBlock(facing, true);
				// Won't work for beds, doors, etc
				world.setBlockState(facing, ((BlockItem)item).getBlock().getDefaultState());
				automoton.getStoreStack().decrement(1);
			}
		}
	}
}