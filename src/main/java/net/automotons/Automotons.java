package net.automotons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

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
	
	public static boolean isClockwiseRotation(Direction from, Direction to){
		return from.getHorizontal() < to.getHorizontal() && !(from == Direction.SOUTH && to == Direction.EAST) || from == Direction.EAST && to == Direction.SOUTH;
	}
}