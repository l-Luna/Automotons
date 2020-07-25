package net.automotons.items.heads;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StickyHeadItem extends HeadItem<BlockState>{
	
	public StickyHeadItem(Settings settings){
		super(settings);
	}
	
	public boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockState state){
		BlockState fromState = automoton.getWorld().getBlockState(from);
		if(automoton.engaged && !fromState.isAir()){
			PistonBehavior behavior = automoton.getWorld().getBlockState(to).getPistonBehavior();
			return automoton.getWorld().getBlockState(to).isAir() || behavior == PistonBehavior.DESTROY || behavior == PistonBehavior.IGNORE;
		}
		return true;
	}
	
	public CompoundTag getExtraData(BlockState state){
		CompoundTag tag = new CompoundTag();
		if(state != null)
			tag.put("blockstate", NbtHelper.fromBlockState(state));
		return tag;
	}
	
	public BlockState readExtraData(CompoundTag tag){
		return NbtHelper.toBlockState(tag.getCompound("blockstate"));
	}
	
	public void startRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockState state){
		if(automoton.engaged){
			World world = automoton.getWorld();
			BlockState fromState = world.getBlockState(from);
			// cut block
			if(!fromState.isAir() && !(fromState.getBlock() instanceof BlockEntityProvider)){
				automoton.setData(fromState);
				world.setBlockState(from, Blocks.AIR.getDefaultState());
			}
		}
	}
	
	public void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockState state){
		BlockState toState = automoton.getWorld().getBlockState(to);
		if(automoton.engaged && state != null && !state.isAir() && toState.isAir()){
			// paste block
			World world = automoton.getWorld();
			BlockRotation rotation = BlockRotation.COUNTERCLOCKWISE_90;
			if(Automotons.isClockwiseRotation(automoton.lastFacing, automoton.facing))
				rotation = BlockRotation.CLOCKWISE_90;
			
			world.setBlockState(to, state.rotate(rotation));
			// stop storing it
			automoton.setData(null);
		}
	}
	
	public float getEngageOffset(AutomotonBlockEntity automoton, BlockState state){
		return 1.75f;
	}
}