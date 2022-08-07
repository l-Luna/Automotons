package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

public class RedstoneHeadItem extends HeadItem<Boolean>{
	
	public RedstoneHeadItem(Settings settings){
		super(settings);
	}
	
	public int getWeakPowerTo(AutomotonBlockEntity automoton, Direction direction, Boolean muted){
		return automoton.engaged ? (muted != null && muted) ? 1 : 15 : 0;
	}
	
	public Boolean readExtraData(NbtCompound tag){
		return tag.getBoolean("muted");
	}
	
	public NbtCompound writeExtraData(Boolean muted){
		NbtCompound tag = new NbtCompound();
		tag.putBoolean("muted", muted != null && muted);
		return tag;
	}
}