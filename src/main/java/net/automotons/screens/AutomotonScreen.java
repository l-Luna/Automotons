package net.automotons.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.automotons.Automotons.autoId;

public class AutomotonScreen extends HandledScreen<AutomotonScreenHandler>{
	
	private static final Identifier TEXTURE = autoId("textures/gui/automoton.png");
	
	public AutomotonScreen(AutomotonScreenHandler handler, PlayerInventory inventory, Text title){
		super(handler, inventory, title);
		backgroundHeight = 186;
	}
	
	protected void init(){
		super.init();
		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
		playerInventoryTitleY = -20;
	}
	
	@SuppressWarnings("deprecation")
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		client.getTextureManager().bindTexture(TEXTURE);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
		AutomotonBlockEntity automoton = handler.automoton;
		if(automoton != null){
			if(!automoton.hasNoModules()){
				int module = automoton.module;
				if(module >= 6)
					module = 11 - (module - 6);
				int modX = (module % 6) * 18 + 52 + x;
				int modY = (module / 6) * 20 + 24 + y;
				drawTexture(matrices, modX, modY, automoton.errored ? 18 : 0, 186, 18, 18);
			}
			int errorTex = automoton.stopOnError ? 176 : 189;
			drawTexture(matrices, x + 26, y + 74, errorTex, 0, 12, 11);
			int engagedTex = automoton.engaged ? 176 : 195;
			drawTexture(matrices, x + 83, y + 76, engagedTex, 12, 18, 9);
			if(automoton.facing == Direction.NORTH || automoton.facing == Direction.SOUTH){
				int facingTex = automoton.facing == Direction.NORTH ? 176 : 187;
				drawTexture(matrices, x + 143, y + 74, facingTex, 22, 10, 13);
			}else{
				int facingTex = automoton.facing == Direction.EAST ? 198 : 212;
				drawTexture(matrices, x + 141, y + 75, facingTex, 22, 13, 10);
			}
			String errorText = I18n.translate(automoton.stopOnError ? "gui.automoton.throws" : "gui.automoton.suppresses");
			textRenderer.draw(matrices, errorText, x + (32 - textRenderer.getWidth(errorText) / 2f), y + 91, 0);
			String engagedText = I18n.translate(automoton.engaged ? "gui.automoton.engaged" : "gui.automoton.disengaged");
			textRenderer.draw(matrices, engagedText, x + (92 - textRenderer.getWidth(engagedText) / 2f), y + 91, 0);
			String facingText = I18n.translate("gui.automoton." + automoton.facing.getName());
			textRenderer.draw(matrices, facingText, x + (148 - textRenderer.getWidth(facingText) / 2f), y + 91, 0);
		}
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
}