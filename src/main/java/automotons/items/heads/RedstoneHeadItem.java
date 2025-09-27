package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneHeadItem extends HeadItem<Boolean>{
	
	public RedstoneHeadItem(Settings settings){
		super(settings);
	}
	
	public int getWeakPowerTo(AutomotonBlockEntity automoton, Direction direction, Boolean muted){
		return automoton.engaged ? (muted != null && muted) ? 1 : 15 : 0;
	}
	
	public Boolean readExtraData(World world, NbtCompound tag){
		return tag.getBoolean("muted");
	}
	
	public NbtCompound writeExtraData(World world, Boolean muted){
		NbtCompound tag = new NbtCompound();
		tag.putBoolean("muted", muted != null && muted);
		return tag;
	}
}