package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Extra data is breaking progress
public class DrillHeadItem extends HeadItem<Float>{
	
	private static final ItemStack DIAMOND_PICK_REF = new ItemStack(Items.DIAMOND_PICKAXE);
	
	public DrillHeadItem(Settings settings){
		super(settings);
	}
	
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Float breakingTime){
		World world = automoton.getWorld();
		if(automoton.engaged && world != null && !world.isReceivingRedstonePower(automoton.getPos())){
			BlockState state = world.getBlockState(facing);
			float hardness = state.getHardness(world, facing);
			BlockSoundGroup soundGroup = state.getSoundGroup();
			if(!state.isAir() && hardness != -1 && (!state.isToolRequired() || ToolManager.handleIsEffectiveOn(state, DIAMOND_PICK_REF, null))){
				if(breakingTime == null)
					breakingTime = 0f;
				if(breakingTime < 9){
					// Is -1 a possible ID for a player to have?
					world.setBlockBreakingInfo(-1, facing, (int)Math.ceil(breakingTime));
					if(world.getTime() % 4 == 0 && !world.isClient())
						world.playSound(null, facing, soundGroup.getHitSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1) / 8, soundGroup.getPitch() * .5f);
				}else{
					if(!world.isClient()){
						world.breakBlock(facing, true);
						world.playSound(null, facing, soundGroup.getBreakSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1) / 2, soundGroup.getPitch() * .8f);
					}
					world.setBlockBreakingInfo(-1, facing, 10);
				}
				automoton.setData(breakingTime + 1.5f / hardness);
			}else
				automoton.setData(0f);
		}
	}
	
	public CompoundTag getExtraData(Float breakingTime){
		CompoundTag tag = new CompoundTag();
		tag.putFloat("breakingTime", breakingTime != null ? breakingTime : 0);
		return tag;
	}
	
	public Float readExtraData(CompoundTag tag){
		return tag.getFloat("breakingTime");
	}
}