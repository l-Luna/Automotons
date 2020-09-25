package net.automotons.client;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShapes;

import static java.lang.Math.min;

public class BlocklayerHeadRenderer implements HeadRenderer<Object>{
	
	public void render(AutomotonBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Object o, int light, int overlay, float tickDelta){
		float rotationOffset = 0;
		matrices.push();
		if(entity.lastFacing != null && entity.lastFacing != entity.facing){
			// FIXME: state rotation
			if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing))
				rotationOffset = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1) - 1;
			else
				rotationOffset = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
		}
		// only display if rotating
		// point of rotation
		matrices.translate(.5, 0, .5);
		matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset + 1)));
		// offset
		matrices.translate(.5, 0, -.5);
		VertexConsumer outline = vertexConsumers.getBuffer(RenderLayer.getLines());
		Matrix4f matrix4f = matrices.peek().getModel();
		VoxelShapes.fullCube().forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
			outline.vertex(matrix4f, (float)(minX), (float)(minY), (float)(minZ)).color(0, 0, 0, 0.4f).next();
			outline.vertex(matrix4f, (float)(maxX), (float)(maxY), (float)(maxZ)).color(0, 0, 0, 0.4f).next();
		});
		matrices.pop();
	}
}