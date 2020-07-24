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
		int i = (width - backgroundWidth) / 2;
		int j = (height - backgroundHeight) / 2;
		drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight);
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}