package net.automotons.items;

import net.minecraft.item.Item;

public class HeadItem<Data> extends Item implements Head<Data>{
	
	public HeadItem(Settings settings){
		super(settings);
	}
}