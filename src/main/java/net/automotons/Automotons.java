package net.automotons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class Automotons implements ModInitializer{
	
	public static final String MODID = "automotons";
	
	public static final ItemGroup ITEMS = FabricItemGroupBuilder
			.create(autoId("items"))
			.icon(() -> new ItemStack(AutomotonsRegistry.AUTOMOTON.asItem()))
			.build();
	
	public void onInitialize(){
		AutomotonsRegistry.registerObjects();
	}
	
	public static Identifier autoId(String path){
		return new Identifier(MODID, path);
	}
}