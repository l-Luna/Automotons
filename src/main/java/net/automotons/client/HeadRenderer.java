package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.Head;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public interface HeadRenderer<Data>{
	
	Map<Head<?>, HeadRenderer<?>> RENDERERS = new HashMap<>();
	
	static void init(){
		RENDERERS.put(AutomotonsRegistry.STICKY_HEAD, new StickyHeadRenderer());
		RENDERERS.put(AutomotonsRegistry.DRILL_HEAD, new DrillHeadRenderer());
	}
	
	default void render(AutomotonBlockEntity automoton, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Data data, int light, int overlay){}
	
	default boolean doNormalRender(AutomotonBlockEntity automoton, Data data){
		return true;
	}
}