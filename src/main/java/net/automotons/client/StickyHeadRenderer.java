package net.automotons.client;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.BlockRotation;

import java.util.Random;

import static java.lang.Math.min;

public class StickyHeadRenderer implements HeadRenderer<BlockState>{
	
	public void render(AutomotonBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockState state, int light, int overlay, float tickDelta){
		// if we're rotating, display block
		if(state != null && !state.isAir()){
			float rotationOffset = 0;
			if(((entity.lastFacing != null && entity.lastFacing != entity.facing) || (entity.lastPos != null && !entity.lastPos.equals(entity.getPos())))){
				BlockModelRenderer.enableBrightnessCache();
				matrices.push();
				BlockState rotated = state;
				if(entity.lastFacing != null && entity.lastFacing != entity.facing){
					BlockRotation rotation;
					// FIXME: state rotation
					if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing)){
						rotationOffset = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1) - 1;
						rotation = BlockRotation.CLOCKWISE_90;
					}else{
						rotation = BlockRotation.COUNTERCLOCKWISE_90;
						rotationOffset = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
					}
					rotated = rotated.rotate(rotation);
				}
				// only display if rotating
				// point of rotation
				matrices.translate(.5, 0, .5);
				matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset + 1)));
				// offset
				matrices.translate(.5, 0, -.5);
				BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
				BakedModel model = manager.getModel(rotated);
				VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
				manager.getModelRenderer().render(entity.getWorld(), model, rotated, entity.getPos(), matrices, buffer, false, new Random(), rotated.getRenderingSeed(entity.getPos()), overlay);
				matrices.pop();
				BlockModelRenderer.disableBrightnessCache();
			}
		}
	}
}