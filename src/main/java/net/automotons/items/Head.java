package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface Head<Data>{
	
	/**
	 * Returns how far the head should extend when engaged. This is purely a visual effect.
	 *
	 * @param automoton
	 * 		The automoton being rendered.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return How far the head should extend visually when engaged.
	 */
	default float getEngageOffset(AutomotonBlockEntity automoton, Data data){
		return 3.5f;
	}
	
	/**
	 * Writes the automoton's extra data to an NBT tag for serialization.
	 *
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return The extra data in serialized form.
	 */
	default NbtCompound writeExtraData(Data data){
		return new NbtCompound();
	}
	
	/**
	 * Reads extra data from the head from an NBT tag for deserialization.
	 *
	 * @param tag
	 * 		The extra data in serialized form.
	 * @return The extra data stored for the head. May be null.
	 */
	default Data readExtraData(NbtCompound tag){
		return null;
	}
	
	/**
	 * Returns whether an automoton holding this head can rotate to face another block.
	 *
	 * @param automoton
	 * 		The automoton holding this head attempting to rotate.
	 * @param to
	 * 		The block the automoton is attempting to rotate to.
	 * @param from
	 * 		The block the automoton is rotating from.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return Whether the automoton can rotate.
	 */
	default boolean canRotateInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
		return true;
	}
	
	/**
	 * Called when an automoton holding this head finishes engaging into a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that has engaged.
	 * @param to
	 * 		The block the automoton is facing.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void engageInto(AutomotonBlockEntity automoton, BlockPos to, Data data){
		moveInto(automoton, to, data);
	}
	
	/**
	 * Called when an automoton holding this head finishes retracting from a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that has disengaged.
	 * @param from
	 * 		The block the automoton is facing.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void retractFrom(AutomotonBlockEntity automoton, BlockPos from, Data data){
	}
	
	/**
	 * Called when an automoton starts rotating towards a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that is rotating.
	 * @param to
	 * 		The block the automoton is rotating to face.
	 * @param from
	 * 		The block the automoton is rotating away from.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void startRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
	}
	
	/**
	 * Called when an automoton finishes rotating towards a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that is rotating.
	 * @param to
	 * 		The block the automoton is rotating to face.
	 * @param from
	 * 		The block the automoton is rotating away from.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
		moveInto(automoton, to, data);
	}
	
	/**
	 * Returns whether an automoton holding this head can move into a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that is attempting to move.
	 * @param to
	 * 		The block the automoton is attempting to move into.
	 * @param from
	 * 		The block the automoton is attemting to move from.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return Whether the automoton can move.
	 */
	default boolean canAutomotonMoveInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Data data){
		return true;
	}
	
	/**
	 * Called when an automoton holding this head begins moving into a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that is moving.
	 * @param to
	 * 		The block the automoton is moving to.
	 * @param from
	 * 		The block the automoton is moving from.
	 * @param prevFacing
	 * 		The block the automoton was previously facing.
	 * @param facing
	 * 		The block the automoton is moving to face.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void startAutomotonMoveInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockPos prevFacing, BlockPos facing, Data data){
	}
	
	/**
	 * Called when an automoton holding this head finishes moving into a block.
	 *
	 * @param automoton
	 * 		The automoton holding this head that is moving.
	 * @param to
	 * 		The block the automoton is moving to.
	 * @param from
	 * 		The block the automoton is moving from.
	 * @param prevFacing
	 * 		The block the automoton was previously facing.
	 * @param facing
	 * 		The block the automoton is moving to face.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void endAutomotonMoveInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, BlockPos prevFacing, BlockPos facing, Data data){
		moveInto(automoton, facing, data);
	}
	
	/**
	 * Called when an automoton holding this head rotates or moves to face a block, or engages into a block.
	 * Called regardless of whether the automoton is engaged.
	 *
	 * @param automoton
	 * 		The automoton holding this head.
	 * @param to
	 * 		The block the automoton is facing.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void moveInto(AutomotonBlockEntity automoton, BlockPos to, Data data){
	}
	
	/**
	 * Called when an automoton holding this head ticks.
	 *
	 * @param automoton
	 * 		The automoton holding this head.
	 * @param facing
	 * 		The block the automoton is facing.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 */
	default void tick(AutomotonBlockEntity automoton, BlockPos facing, Data data){
	}
	
	/**
	 * Returns the amount of strong redstone power an automoton holding this head should emit in
	 * a particular direction.
	 *
	 * @param automoton
	 * 		The automoton holding this head.
	 * @param direction
	 * 		The direction for which redstone power is being queried.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return The amount of strong redstone power emitted.
	 */
	default int getStrongPowerTo(AutomotonBlockEntity automoton, Direction direction, Data data){
		return 0;
	}
	
	/**
	 * Returns the amount of weak redstone power an automoton holding this head should emit in
	 * a particular direction.
	 *
	 * @param automoton
	 * 		The automoton holding this head.
	 * @param direction
	 * 		The direction for which redstone power is being queried.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return The amount of weak redstone power emitted.
	 */
	default int getWeakPowerTo(AutomotonBlockEntity automoton, Direction direction, Data data){
		return 0;
	}
	
	/**
	 * Returns whether an automoton holding this head should be able to begin broadcasting.
	 *
	 * @param automoton
	 * 		The automoton holding this head.
	 * @param data
	 * 		The extra data stored for the head. May be null.
	 * @return Whether an automoton should be able to broadcast.
	 */
	default boolean canGenerateBroadcast(AutomotonBlockEntity automoton, Data data){
		return false;
	}
}