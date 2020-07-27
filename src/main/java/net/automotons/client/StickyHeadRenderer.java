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
	
	public void render(AutomotonBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockState state, int light, int overlay){
		// if we're rotating, display block
		if(state != null && !state.isAir()){
			float rotationOffset;
			if(entity.lastFacing != null && entity.lastFacing != entity.facing && entity.moduleTime > 0){
				BlockModelRenderer.enableBrightnessCache();
				matrices.push();
				// FIXME: state rotation
				BlockRotation rotation = BlockRotation.COUNTERCLOCKWISE_90;
				if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing)){
					rotationOffset = min(entity.moduleTime / 10f, 1) - 1;
					rotation = BlockRotation.CLOCKWISE_90;
				}else
					rotationOffset = 1 - min(entity.moduleTime / 10f, 1);
				// only display if rotating
				// point of rotation
				matrices.translate(.5, 0, .5);
				matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset + 1)));
				// offset
				matrices.translate(.5, 0, -.5);
				BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
				BlockState rotated = state.rotate(rotation);
				BakedModel model = manager.getModel(rotated);
				VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
				manager.getModelRenderer().render(entity.getWorld(), model, rotated, entity.getPos(), matrices, buffer, false, new Random(), rotated.getRenderingSeed(entity.getPos()), overlay);
				matrices.pop();
				BlockModelRenderer.disableBrightnessCache();
			}
		}
	}
}