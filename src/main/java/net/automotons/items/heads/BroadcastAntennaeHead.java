package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;

public class BroadcastAntennaeHead extends HeadItem<Object>{
	
	public BroadcastAntennaeHead(Settings settings){
		super(settings);
	}
	
	public boolean canGenerateBroadcast(AutomotonBlockEntity automoton, Object o){
		return automoton.engaged;
	}
}