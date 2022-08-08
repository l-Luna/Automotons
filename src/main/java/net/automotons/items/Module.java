package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.broadcast.Broadcast;

@FunctionalInterface
public interface Module{
	
	/**
	 * Executes this module. Returns whether the execution was successful.
	 *
	 * @param block
	 * 		The automoton executing this module.
	 * @return Whether execution was successful.
	 */
	boolean execute(AutomotonBlockEntity block);
	
	/**
	 * Executes this module with the context of a broadcast. Returns whether the execution was successful.
	 * Used when this module is being received from a broadcast, and allows using information from the sender.
	 *
	 * @param block
	 * 		The automoton executing this module.
	 * @param broadcast
	 * 		The broadcast from which this module is received.
	 * @return Whether execution was successful.
	 */
	default boolean executeFromBroadcast(AutomotonBlockEntity block, Broadcast broadcast){
		return execute(block);
	}
	
	default boolean shouldExecuteOnClient(){
		return false;
	}
}