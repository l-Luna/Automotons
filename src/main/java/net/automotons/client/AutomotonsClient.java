package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.automotons.screens.AutomotonScreen;
import net.automotons.skins.AutomotonSkins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelAppender;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;

import java.util.function.Consumer;

import static net.automotons.AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER;

public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// TERs
		BlockEntityRendererRegistry.INSTANCE.register(AutomotonsRegistry.AUTOMOTON_BE, dispatcher -> new AutomotonBlockEntityRenderer(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
		HeadRenderer.init();
		
		// Screens and Screen Handler Types
		ScreenRegistry.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
		
		// Model Loading
		ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> AutomotonSkins.SKINS.values().forEach(skin -> {
			out.accept(new ModelIdentifier(skin.getBase(), ""));
			out.accept(new ModelIdentifier(skin.getBody(), ""));
		}));
	}
}