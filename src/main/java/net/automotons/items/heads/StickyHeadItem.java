package net.automotons.items.heads;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StickyHeadItem extends HeadItem<BlockState>{
	
	public StickyHeadItem(Settings settings){
		super(settings);
	}
	
	public boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockState state){
		BlockState fromState = automoton.getWorld().getBlockState(from);
		if(automoton.engaged && canMove(fromState, automoton.getWorld(), from)){
			PistonBehavior behavior = automoton.getWorld().getBlockState(to).getPistonBehavior();
			return automoton.getWorld().getBlockState(to).isAir() || behavior == PistonBehavior.DESTROY;
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
			if(canMove(fromState, world, from)){
				automoton.setData(fromState);
				world.setBlockState(from, Blocks.AIR.getDefaultState());
				if(!world.isClient())
					automoton.sync();
			}
		}
	}
	
	public void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockState state){
		BlockState toState = automoton.getWorld().getBlockState(to);
		if(automoton.engaged && state != null && !state.isAir() && (toState.isAir() || toState.getPistonBehavior() == PistonBehavior.DESTROY)){
			// paste block
			World world = automoton.getWorld();
			BlockRotation rotation = BlockRotation.COUNTERCLOCKWISE_90;
			if(Automotons.isClockwiseRotation(automoton.lastFacing, automoton.facing))
				rotation = BlockRotation.CLOCKWISE_90;
			
			if(!toState.isAir())
				world.breakBlock(to, true);
			
			world.setBlockState(to, state.rotate(rotation));
			// stop storing it
			automoton.setData(null);
			if(!world.isClient())
				automoton.sync();
		}
	}
	
	public void startAutomotonMoveInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockPos prevFacing, BlockPos facing, BlockState state){
		if(automoton.engaged){
			World world = automoton.getWorld();
			BlockState fromState = world.getBlockState(prevFacing);
			// cut block
			if(canMove(fromState, world, prevFacing)){
				automoton.setData(fromState);
				world.setBlockState(prevFacing, Blocks.AIR.getDefaultState());
				if(!world.isClient())
					automoton.sync();
			}
		}
	}
	
	public void endAutomotonMoveInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockPos prevFacing, BlockPos facing, BlockState state){
		BlockState toState = automoton.getWorld().getBlockState(facing);
		if(automoton.engaged && state != null && !state.isAir() && (toState.isAir() || toState.getPistonBehavior() == PistonBehavior.DESTROY)){
			// paste block
			World world = automoton.getWorld();
			if(!toState.isAir())
				world.breakBlock(facing, true);
			
			world.setBlockState(facing, state);
			// stop storing it
			automoton.setData(null);
			if(!world.isClient())
				automoton.sync();
		}
	}
	
	public void engageInto(AutomotonBlockEntity automoton, BlockPos to, BlockState state){
		super.engageInto(automoton, to, state);
		automoton.getWorld().playSound(null, automoton.getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, .5f, automoton.getWorld().random.nextFloat() * .25f + .6f);
	}
	
	public void retractFrom(AutomotonBlockEntity automoton, BlockPos from, BlockState state){
		super.retractFrom(automoton, from, state);
		automoton.getWorld().playSound(null, automoton.getPos(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, .5f, automoton.getWorld().random.nextFloat() * .15f + .6f);
	}
	
	public static boolean canMove(BlockState state, World world, BlockPos pos){
		return !state.isAir() && !(state.getBlock() instanceof BlockEntityProvider || state.getHardness(world, pos) == -1) && state.getPistonBehavior() == PistonBehavior.NORMAL;
	}
	
	public float getEngageOffset(AutomotonBlockEntity automoton, BlockState state){
		return 1.75f;
	}
}