package net.automotons.broadcast;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.Module;
import net.minecraft.util.math.BlockPos;

public class Broadcast{
	
	// position
	// automoton - perhaps make this more generic for dedicated antenna block?
	// instruction
	
	boolean killed = false;
	
	BlockPos position;
	AutomotonBlockEntity source;
	Module instruction;
	
	public Broadcast(BlockPos position, AutomotonBlockEntity source, Module instruction){
		this.position = position;
		this.source = source;
		this.instruction = instruction;
	}
	
	public BlockPos getPosition(){
		return position;
	}
	
	public AutomotonBlockEntity getSource(){
		return source;
	}
	
	public Module getInstruction(){
		return instruction;
	}
	
	public boolean isKilled(){
		return killed;
	}
	
	public void setPosition(BlockPos position){
		this.position = position;
	}
	
	public void setSource(AutomotonBlockEntity source){
		this.source = source;
	}
	
	public void setInstruction(Module instruction){
		this.instruction = instruction;
	}
	
	public void kill(){
		killed = true;
	}
}