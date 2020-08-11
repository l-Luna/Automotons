package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.automotons.screens.AutomotonScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;

import static net.automotons.AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER;

@SuppressWarnings("unused")
public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// TERs
		BlockEntityRendererRegistry.INSTANCE.register(AutomotonsRegistry.AUTOMOTON_BE, dispatcher -> new AutomotonBlockEntityRenderer(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
		HeadRenderer.init();
		
		// Screens and Screen Handler Types
		ScreenRegistry.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
	}
}