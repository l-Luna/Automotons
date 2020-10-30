package net.automotons.client;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;

import java.util.Optional;

import static java.lang.Math.min;

public class BlocklayerHeadRenderer implements HeadRenderer<Object>{
	
	@SuppressWarnings("deprecation")
	public void render(AutomotonBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Object o, int light, int overlay, float tickDelta){
		float rotationOffset = 0;
		matrices.push();
		if(entity.lastFacing != null && entity.lastFacing != entity.facing)
			if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing))
				rotationOffset = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1) - 1;
			else
				rotationOffset = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
		// point of rotation
		matrices.translate(.5, 0, .5);
		matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset + 1)));
		// offset
		matrices.translate(1, 0, 0);
		// un-rotate
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset + 1)));
		// offset again
		matrices.translate(-.5, 0, -.5);
		VertexConsumer outline = vertexConsumers.getBuffer(RenderLayer.getLines());
		// get shape
		Optional<VoxelShape> shape;
		if(entity.getStoreStack().getItem() instanceof BlockItem){
			BlockItem bi = (BlockItem)entity.getStoreStack().getItem();
			Block block = bi.getBlock();
			shape = Optional.of(block.getOutlineShape(block.getDefaultState(), entity.getWorld(), entity.getPos().offset(entity.facing), ShapeContext.absent()));
		}else
			shape = Optional.empty();
		Matrix4f matrix4f = matrices.peek().getModel();
		shape.ifPresent(sh -> sh.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
			outline.vertex(matrix4f, (float)(minX), (float)(minY), (float)(minZ)).color(.4f, 0, .4f, 0.5f).next();
			outline.vertex(matrix4f, (float)(maxX), (float)(maxY), (float)(maxZ)).color(.4f, 0, .4f, 0.5f).next();
		}));
		matrices.pop();
	}
}