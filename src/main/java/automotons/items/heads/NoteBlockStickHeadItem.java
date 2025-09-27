package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NoteBlockStickHeadItem extends HeadItem<Object>{
	
	public NoteBlockStickHeadItem(Settings settings){
		super(settings);
	}
	
	public void endEngageInto(AutomotonBlockEntity automoton, BlockPos to, Object unused){
		ItemStack stack = automoton.getStack(13);
		World world = automoton.getWorld();
		if(world != null)
			if(stack.isEmpty() || !(stack.getItem() instanceof MusicDiscItem)){
				Instrument instrument = (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) ? Instrument.fromBelowState(bi.getBlock().getDefaultState()) : Instrument.HARP;
				// 0-23
				float pitchRaw = Math.min(23, stack.getCount());
				float pitch = (float)Math.pow(2, (double)(pitchRaw - 12) / 12.0D);
				double x = automoton.getPos().getX() + .5 + automoton.facing.getOffsetX() * .5;
				double y = automoton.getPos().getY() + 1;
				double z = automoton.getPos().getZ() + .5 + automoton.facing.getOffsetZ() * .5;
				world.playSound(null, x, y, z, instrument.getSound(), SoundCategory.RECORDS, 3, pitch, world.random.nextLong());
				world.addParticle(ParticleTypes.NOTE, x, y, z, pitchRaw / 24, 0, 0);
			}else
				world.syncWorldEvent(1010, automoton.getPos(), Registries.ITEM.getRawId(stack.getItem()));
	}
	
	public void endRetractFrom(AutomotonBlockEntity automoton, BlockPos from, Object unused){
		World world = automoton.getWorld();
		if(world != null)
			world.syncWorldEvent(1010, automoton.getPos(), 0);
	}
}