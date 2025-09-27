package automotons.client;

import automotons.AutomotonsRegistry;
import automotons.screens.AutomotonScreen;
import automotons.skins.AutomotonSkins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import static automotons.AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER;

public class AutomotonsClient implements ClientModInitializer{
	
	public void onInitializeClient(){
		// Automoton rendering
		BlockEntityRendererFactories.register(AutomotonsRegistry.AUTOMOTON_BE, context -> new AutomotonBlockEntityRenderer(context.getItemRenderer()));
		HeadRenderer.init();
		
		// Screens handling
		HandledScreens.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
		
		// Model loading for automoton variants
		ModelLoadingPlugin.register(ctx -> AutomotonSkins.SKINS.values().forEach((x)-> ctx.addModels(x.base(), x.body())));
	}
}