package automotons.client;

import automotons.AutomotonsRegistry;
import automotons.screens.AutomotonScreen;
import automotons.skins.AutomotonSkins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import static automotons.AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER;

public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// TERs
		BlockEntityRendererFactories.register(AutomotonsRegistry.AUTOMOTON_BE, context -> new AutomotonBlockEntityRenderer(context.getItemRenderer()));
		HeadRenderer.init();
		
		// Screens and Screen Handler Types
		HandledScreens.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
		
		// Model Loading
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> AutomotonSkins.SKINS.values().forEach(skin -> {
			out.accept(skin.base());
			out.accept(skin.body());
		}));
	}
}