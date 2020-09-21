package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.broadcast.Broadcast;

public interface Module{
	
	boolean execute(AutomotonBlockEntity block);
	
	default boolean executeFromBroadcast(AutomotonBlockEntity block, Broadcast broadcast){
		return execute(block);
	}
}