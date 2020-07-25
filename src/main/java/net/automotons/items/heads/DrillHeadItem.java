package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Extra data is breaking progress
public class DrillHeadItem extends HeadItem<Integer>{
	
	public DrillHeadItem(Settings settings){
		super(settings);
	}
	
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Integer breakingTime){
		World world = automoton.getWorld();
		if(automoton.engaged && world != null){
			BlockState state = world.getBlockState(facing);
			if(!state.isAir() && state.getHardness(world, facing) != -1){
				// Is -1 possible for a player to have?
				if(breakingTime == null)
					breakingTime = 0;
				if(breakingTime < 10)
					world.setBlockBreakingInfo(-1, facing, breakingTime);
				else{
					if(!world.isClient())
						world.breakBlock(facing, true);
					world.setBlockBreakingInfo(-1, facing, -1);
				}
			}else
				breakingTime = 0;
			automoton.setData(breakingTime + 1);
		}
	}
	
	public CompoundTag getExtraData(Integer breakingTime){
		CompoundTag tag = new CompoundTag();
		tag.putInt("breakingTime", breakingTime != null ? breakingTime : 0);
		return tag;
	}
	
	public Integer readExtraData(CompoundTag tag){
		return tag.getInt("breakingTime");
	}
}