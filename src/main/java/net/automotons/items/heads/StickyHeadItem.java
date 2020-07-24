package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static java.lang.Math.min;

public class StickyHeadItem extends HeadItem<Object>{
	
	public StickyHeadItem(Settings settings){
		super(settings);
	}
	
	public boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object o){
		BlockState state = automoton.getWorld().getBlockState(from);
		if(automoton.engaged && !state.isAir()){
			PistonBehavior behavior = automoton.getWorld().getBlockState(to).getPistonBehavior();
			return automoton.getWorld().getBlockState(to).isAir() || behavior == PistonBehavior.DESTROY || behavior == PistonBehavior.IGNORE;
		}
		return true;
	}
	
	public void rotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object o){
		if(automoton.engaged){
			// move block in 'from' to 'to'
			World world = automoton.getWorld();
			BlockState state = world.getBlockState(from);
			if(!state.isAir() && !(state.getBlock() instanceof BlockEntityProvider)){
				BlockRotation rotation = BlockRotation.CLOCKWISE_90;
				
				if(automoton.lastFacing != null && automoton.lastFacing != automoton.facing)
					if((automoton.lastFacing.getHorizontal() < automoton.facing.getHorizontal() || automoton.lastFacing == Direction.EAST && automoton.facing == Direction.SOUTH) && !(automoton.lastFacing == Direction.SOUTH && automoton.facing == Direction.EAST))
						rotation = BlockRotation.CLOCKWISE_90;
					else
						rotation = BlockRotation.COUNTERCLOCKWISE_90;
				
				world.setBlockState(from, Blocks.AIR.getDefaultState());
				world.setBlockState(to, state.rotate(rotation));
			}
		}
	}
}