package net.automotons.client;

import net.automotons.*;
import net.automotons.blocks.*;
import net.automotons.items.*;
import net.automotons.skins.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.*;
import net.minecraft.client.render.block.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;

import static java.lang.Math.min;

public class AutomotonBlockEntityRenderer implements BlockEntityRenderer<AutomotonBlockEntity>{
	
	private final ItemRenderer itemRenderer;
	
	public AutomotonBlockEntityRenderer(ItemRenderer renderer){
		itemRenderer = renderer;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void render(AutomotonBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
		matrices.push();
		
		// render block
		BlockModelRenderer.enableBrightnessCache();
		if(entity.lastPos != null && !entity.lastPos.equals(entity.getPos())){
			float progress = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
			float xProgress = (entity.getPos().getX() - entity.lastPos.getX()) * progress * -1;
			float yProgress = (entity.getPos().getY() - entity.lastPos.getY()) * progress * -1;
			float zProgress = (entity.getPos().getZ() - entity.lastPos.getZ()) * progress * -1;
			matrices.translate(xProgress, yProgress, zProgress);
		}
		
		// render base
		BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
		AutomotonSkin skin = AutomotonSkins.getSkin(entity.getSkin());
		BakedModel base = modelManager.getModel(new ModelIdentifier(skin.base(), ""));
		BakedModel body = modelManager.getModel(new ModelIdentifier(skin.body(), ""));
		
		BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
		BlockState state = entity.getWorld().getBlockState(entity.getPos());
		// TODO: translucency doesn't work with the coloured indicator. find a better indicator?
		VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntitySolid());
		manager.getModelRenderer().render(entity.getWorld(), base, state, entity.getPos(), matrices, buffer, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
		// if the automoton has a colour indicator, add a coloured outline
		ColourVertexConsumer ovc = new ColourVertexConsumer(vertexConsumers.getBuffer(getColourOverlay()), matrices.peek().getPositionMatrix(), matrices.peek().getNormalMatrix());
		entity.getOutlineColour().ifPresent((red, green, blue) -> {
			matrices.push();
			ovc.fixedColor(red, green, blue, MathHelper.abs((int)(MathHelper.sin((float)(Math.PI * min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1))) * 200)));
			matrices.translate(-0.02, -0.02, -0.02);
			matrices.scale(1.04f, 1.04f, 1.04f);
			manager.getModelRenderer().render(entity.getWorld(), base, state, entity.getPos(), matrices, ovc, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
			matrices.pop();
		});
		
		float rotationOffset = 0f;
		if(entity.lastFacing != null && entity.lastFacing != entity.facing)
			if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing))
				rotationOffset = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1) - 1;
			else
				rotationOffset = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
		
		matrices.push();
		matrices.translate(.5, 0, .5);
		matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
		matrices.translate(-.5, 0, -.5);
		// render main body with rotation
		manager.getModelRenderer().render(entity.getWorld(), body, state, entity.getPos(), matrices, buffer, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
		// colour indicator again
		if(entity.getOutlineColour().isPresent() && !entity.hasNoModules()){
			matrices.push();
			matrices.translate(-0.02, -0.02, -0.02);
			matrices.scale(1.04f, 1.04f, 1.04f);
			manager.getModelRenderer().render(entity.getWorld(), body, state, entity.getPos(), matrices, ovc, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
			matrices.pop();
		}
		matrices.pop();
		
		ItemStack headStack = entity.getStack(12);
		if(!headStack.isEmpty() && headStack.getItem() instanceof Head){
			Head head = (Head)headStack.getItem();
			HeadRenderer renderer = HeadRenderer.RENDERERS.get(head);
			if(renderer == null || renderer.doNormalRender(entity, entity.data)){
				matrices.push();
				// transition between engaged/disengaged
				float engageProgress = entity.engaged ? 1 : 0;
				if(entity.lastEngaged && !entity.engaged)
					engageProgress = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
				else if(!entity.lastEngaged && entity.engaged)
					engageProgress = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
				float offset = (head.getEngageOffset(entity, entity.data) / 16f) * engageProgress;
				// move to proper position (on automoton)
				matrices.translate(.5, 14 / 16f, .5);
				// rotate to facing
				matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
				// make the item flat
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
				// apply engaged/disengaged transformation
				matrices.translate(-offset * 2, 0, 0);
				// more facing
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45));
				// render item
				itemRenderer.renderItem(headStack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
				matrices.pop();
			}
			if(renderer != null)
				renderer.render(entity, matrices, vertexConsumers, entity.data, light, overlay, tickDelta);
		}
		matrices.pop();
		BlockModelRenderer.disableBrightnessCache();
	}
	
	private static RenderLayer COLOUR_OVERLAY = null;
	
	private static RenderLayer getColourOverlay(){
		if(COLOUR_OVERLAY == null)
			COLOUR_OVERLAY = new RenderLayer.MultiPhase("automotons:colour_overlay", VertexFormats.POSITION_COLOR_LIGHT, DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().writeMaskState(RenderPhase.COLOR_MASK).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).texture(RenderPhase.NO_TEXTURE).cull(RenderPhase.DISABLE_CULLING).lightmap(RenderPhase.ENABLE_LIGHTMAP).build(false));
		return COLOUR_OVERLAY;
	}
}