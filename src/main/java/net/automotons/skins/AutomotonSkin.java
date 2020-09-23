package net.automotons.skins;

import net.minecraft.util.Identifier;

public class AutomotonSkin{
	
	private Identifier body, base;
	
	public AutomotonSkin(Identifier body, Identifier base){
		this.body = body;
		this.base = base;
	}
	
	public Identifier getBody(){
		return body;
	}
	
	public Identifier getBase(){
		return base;
	}
}