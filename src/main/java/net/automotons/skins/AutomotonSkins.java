package net.automotons.skins;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static net.automotons.Automotons.autoId;

public class AutomotonSkins{
	
	public static final Map<Identifier, AutomotonSkin> SKINS = new HashMap<>();
	
	static{
		SKINS.put(autoId("regular"), new AutomotonSkin(autoId("automotons/regular_body"), autoId("automotons/regular_base")));
	}
}
