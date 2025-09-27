package automotons.skins;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static automotons.Automotons.autoId;

public class AutomotonSkins{
	
	public static final Map<Identifier, AutomotonSkin> SKINS = new HashMap<>();
	
	public static final AutomotonSkin REGULAR = new AutomotonSkin(autoId("automoton/regular_body"), autoId("automoton/regular_base"));
	
	static{
		SKINS.put(autoId("regular"), REGULAR);
		SKINS.put(autoId("charcoal"), new AutomotonSkin(autoId("automoton/charcoal_body"), autoId("automoton/charcoal_base")));
		SKINS.put(autoId("wood"), new AutomotonSkin(autoId("automoton/wood_body"), autoId("automoton/wood_base")));
		SKINS.put(autoId("factory"), new AutomotonSkin(autoId("automoton/factory_body"), autoId("automoton/factory_base")));
	}
	
	public static AutomotonSkin getSkin(Identifier id){
		return SKINS.getOrDefault(id, REGULAR);
	}
}