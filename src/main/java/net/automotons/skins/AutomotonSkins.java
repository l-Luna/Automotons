package net.automotons.skins;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static net.automotons.Automotons.autoId;

public class AutomotonSkins{
	
	public static final Map<Identifier, AutomotonSkin> SKINS = new HashMap<>();
	
	public static final AutomotonSkin REGULAR = new AutomotonSkin(autoId("automotons/regular_body"), autoId("automotons/regular_base"));
	
	static{
		SKINS.put(autoId("regular"), REGULAR);
		SKINS.put(autoId("charcoal"), new AutomotonSkin(autoId("automotons/charcoal_body"), autoId("automotons/charcoal_base")));
		SKINS.put(autoId("wood"), new AutomotonSkin(autoId("automotons/wood_body"), autoId("automotons/wood_base")));
	}
	
	public static AutomotonSkin getSkin(Identifier id){
		return SKINS.getOrDefault(id, REGULAR);
	}
}