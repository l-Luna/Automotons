package net.automotons.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.automotons.Automotons.autoId;

public class AutomotonScreen extends HandledScreen<AutomotonScreenHandler>{
	
	private static final Identifier TEXTURE = autoId("textures/gui/automoton.png");
	
	public AutomotonScreen(AutomotonScreenHandler handler, PlayerInventory inventory, Text title){
		super(handler, inventory, title);
	}
	
	protected void init(){
		super.init();
		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
	}
	
	@SuppressWarnings("deprecation")
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		client.getTextureManager().bindTexture(TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
		if(handler.automoton != null && !handler.automoton.hasNoModules()){
			int module = handler.automoton.module;
			if(module >= 6)
				module = 11 - (module - 6);
			int modX = (module % 6) * 18 + 52 + x;
			int modY = (module / 6) * 20 + 24 + y;
			drawTexture(matrices, modX, modY, handler.automoton.errored ? 18 : 0, 166, 18, 18);
		}
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}