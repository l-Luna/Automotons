package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlocklayerHeadItem extends HeadItem<Object>{
	
	public BlocklayerHeadItem(Settings settings){
		super(settings);
	}
	
	@SuppressWarnings("deprecation")
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Object o){
		World world = automoton.getWorld();
		BlockState state = world == null ? null : world.getBlockState(facing);
		if(automoton.engaged && state != null && automoton.moduleTime == automoton.moduleSpeed() - 1 && (state.isAir() || state.getPistonBehavior() == PistonBehavior.DESTROY)){
			Item item = automoton.getStoreStack().getItem();
			if(item instanceof BlockItem){
				Block block = ((BlockItem)item).getBlock();
				if(block.canPlaceAt(block.getDefaultState(), world, facing)){
					// Don't break block - block replacement doesn't do so
					// Won't work for beds, doors, etc
					world.setBlockState(facing, block.getDefaultState());
					automoton.getStoreStack().decrement(1);
					// Make some kind of effect
					BlockSoundGroup group = block.getSoundGroup(block.getDefaultState());
					world.playSound(facing.getX(), facing.getY(), facing.getZ(), group.getPlaceSound(), SoundCategory.BLOCKS, (group.getVolume() + 1) / 2, group.getPitch() * .8f, false);
				}
			}
		}
	}
}