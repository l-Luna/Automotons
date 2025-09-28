package automotons.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import automotons.blocks.AutomotonBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static automotons.Automotons.autoId;

public class AutomotonScreen extends HandledScreen<AutomotonScreenHandler>{
	
	private static final Identifier TEXTURE = autoId("textures/gui/automoton.png");
	
	public AutomotonScreen(AutomotonScreenHandler handler, PlayerInventory inventory, Text title){
		super(handler, inventory, title);
		backgroundHeight = 186;
	}
	
	protected void init(){
		super.init();
		titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
	}
	
	protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY){
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		ctx.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
		AutomotonBlockEntity automoton = handler.automoton;
		if(automoton != null){
			if(!automoton.hasNoModules()){
				int module = automoton.module;
				if(module >= 6)
					module = 11 - (module - 6);
				int modX = (module % 6) * 18 + 52 + x;
				int modY = (module / 6) * 20 + 24 + y;
				ctx.drawTexture(TEXTURE, modX, modY, automoton.errored ? 18 : 0, 186, 18, 18);
			}
			int errorTex = automoton.stopOnError ? 176 : 189;
			ctx.drawTexture(TEXTURE, x + 26, y + 74, errorTex, 0, 12, 11);
			int engagedTex = automoton.engaged ? 176 : 195;
			ctx.drawTexture(TEXTURE, x + 83, y + 76, engagedTex, 12, 18, 9);
			if(automoton.facing == Direction.NORTH || automoton.facing == Direction.SOUTH){
				int facingTex = automoton.facing == Direction.NORTH ? 176 : 187;
				ctx.drawTexture(TEXTURE, x + 143, y + 74, facingTex, 22, 10, 13);
			}else{
				int facingTex = automoton.facing == Direction.EAST ? 198 : 212;
				ctx.drawTexture(TEXTURE, x + 141, y + 75, facingTex, 22, 13, 10);
			}
			String errorText = I18n.translate(automoton.stopOnError ? "gui.automoton.throws" : "gui.automoton.suppresses");
			ctx.drawText(textRenderer, errorText, (int)(x + (32 - textRenderer.getWidth(errorText) / 2f)), y + 91, 0, false);
			String engagedText = I18n.translate(automoton.engaged ? "gui.automoton.engaged" : "gui.automoton.disengaged");
			ctx.drawText(textRenderer, engagedText, (int)(x + (92 - textRenderer.getWidth(engagedText) / 2f)), y + 91, 0, false);
			String facingText = I18n.translate("gui.automoton." + automoton.facing.getName());
			ctx.drawText(textRenderer, facingText, (int)(x + (148 - textRenderer.getWidth(facingText) / 2f)), y + 91, 0, false);
		}
	}
	
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta){
		this.renderBackground(ctx);
		super.render(ctx, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(ctx, mouseX, mouseY);
	}
	
	protected void drawForeground(DrawContext ctx, int mouseX, int mouseY){
		ctx.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
	}
}