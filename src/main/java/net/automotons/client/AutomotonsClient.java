package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("unused")
public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// TERs
		BlockEntityRendererRegistry.INSTANCE.register(AutomotonsRegistry.AUTOMOTON_BE, dispatcher -> new AutomotonBlockEntityRenderer(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
		HeadRenderer.init();
	}
}