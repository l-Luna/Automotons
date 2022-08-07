package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.automotons.screens.AutomotonScreen;
import net.automotons.skins.AutomotonSkins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.util.ModelIdentifier;

import static net.automotons.AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER;

public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// TERs
		BlockEntityRendererRegistry.register(AutomotonsRegistry.AUTOMOTON_BE, context -> new AutomotonBlockEntityRenderer(context.getItemRenderer()));
		HeadRenderer.init();
		
		// Screens and Screen Handler Types
		HandledScreens.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
		
		// Model Loading
		ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> AutomotonSkins.SKINS.values().forEach(skin -> {
			out.accept(new ModelIdentifier(skin.base(), ""));
			out.accept(new ModelIdentifier(skin.body(), ""));
		}));
	}
}