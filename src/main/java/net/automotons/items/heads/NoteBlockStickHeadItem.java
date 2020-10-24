package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class NoteBlockStickHeadItem extends HeadItem<Object>{
	
	public NoteBlockStickHeadItem(Settings settings){
		super(settings);
	}
	
	public void engageInto(AutomotonBlockEntity automoton, BlockPos to, Object unused){
		ItemStack stack = automoton.getStack(13);
		World world = automoton.getWorld();
		if(world != null)
			if(stack.isEmpty() || !(stack.getItem() instanceof MusicDiscItem)){
				Instrument instrument = (!stack.isEmpty() && stack.getItem() instanceof BlockItem) ? Instrument.fromBlockState(((BlockItem)stack.getItem()).getBlock().getDefaultState()) : Instrument.HARP;
				// 0-23
				float pitchRaw = Math.min(23, stack.getCount());
				float pitch = (float)Math.pow(2, (double)(pitchRaw - 12) / 12.0D);
				world.playSound(null, automoton.getPos(), instrument.getSound(), SoundCategory.RECORDS, 3, pitch);
				world.addParticle(ParticleTypes.NOTE, automoton.getPos().getX() + .5 + automoton.facing.getOffsetX() * .5, automoton.getPos().getY() + 1, automoton.getPos().getZ() + .5 + automoton.facing.getOffsetZ() * .5, pitchRaw / 24, 0, 0);
			}else
				world.syncWorldEvent(1010, automoton.getPos(), Registry.ITEM.getRawId(stack.getItem()));
	}
	
	public void retractFrom(AutomotonBlockEntity automoton, BlockPos from, Object unused){
		World world = automoton.getWorld();
		if(world != null)
			world.syncWorldEvent(1010, automoton.getPos(), 0);
	}
}