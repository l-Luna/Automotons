package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public interface Head<Data>{
	
	default float getEngageOffset(AutomotonBlockEntity automoton, Data data){
		return 3.5f;
	}
	
	default CompoundTag getExtraData(Data data){
		return new CompoundTag();
	}
	
	default Data readExtraData(CompoundTag tag){
		return null;
	}
	
	default boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
		return true;
	}
	
	default void engageInto(AutomotonBlockEntity automoton, BlockPos to, Data data){
		moveInto(automoton, to, data);
	}
	
	default void startRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){}
	
	default void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
		moveInto(automoton, to, data);
	}
	
	default void moveInto(AutomotonBlockEntity automoton, BlockPos to, Data data){}
	
	default void tick(AutomotonBlockEntity automoton, BlockPos facing, Data data){}
}