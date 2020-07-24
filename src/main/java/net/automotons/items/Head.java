package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public interface Head{
	
	default float getEngageOffset(AutomotonBlockEntity automoton){
		return 3.5f;
	}
	
	default CompoundTag getExtraData(){
		return new CompoundTag();
	}
	
	default void readExtraData(CompoundTag tag){}
	
	default boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from){
		return true;
	}
	
	default void engageInto(AutomotonBlockEntity automoton, BlockPos to){
		moveInto(automoton, to);
	}
	
	default void rotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from){
		moveInto(automoton, to);
	}
	
	default void moveInto(AutomotonBlockEntity automoton, BlockPos to){}
}