package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;

public interface Head{
	
	default float getEngageOffset(AutomotonBlockEntity abe){
		return 3.5f;
	}
}