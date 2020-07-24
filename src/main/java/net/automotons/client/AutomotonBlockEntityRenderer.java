package net.automotons.client;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.Head;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import static java.lang.Math.min;

public class AutomotonBlockEntityRenderer extends BlockEntityRenderer<AutomotonBlockEntity>{
	
	private final ItemRenderer itemRenderer;
	
	public AutomotonBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher, ItemRenderer renderer){
		super(dispatcher);
		itemRenderer = renderer;
	}
	
	public void render(AutomotonBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
		ItemStack headStack = entity.getStack(12);
		if(!headStack.isEmpty() && headStack.getItem() instanceof Head){
			Head head = (Head)headStack.getItem();
			matrices.push();
			// transition between engaged/disengaged
			float engageProgress = entity.engaged ? 1 : 0;
			if(entity.lastEngaged && !entity.engaged && entity.moduleTime > 0)
				engageProgress = 1 - min(entity.moduleTime / 10f, 1);
			else if(!entity.lastEngaged && entity.engaged && entity.moduleTime > 0)
				engageProgress = min(entity.moduleTime / 10f, 1);
			float offset = (head.getEngageOffset(entity) / 16f) * engageProgress;
			// move to proper position (on automoton)
			matrices.translate(.5, 14 / 16f, .5);
			// rotate to facing
			float rotationOffset = 0f;
			if(entity.lastFacing != null && entity.lastFacing != entity.facing && entity.moduleTime > 0)
				if((entity.lastFacing.getHorizontal() < entity.facing.getHorizontal() || entity.lastFacing == Direction.EAST && entity.facing == Direction.SOUTH) && !(entity.lastFacing == Direction.SOUTH && entity.facing == Direction.EAST))
					rotationOffset = min(entity.moduleTime / 10f, 1) - 1;
				else
					rotationOffset = 1 - min(entity.moduleTime / 10f, 1);
			matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
			// make the item flat
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
			// apply engaged/disengaged transformation
			matrices.translate(-offset * 2, 0, 0);
			// more facing
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(45));
			// render item
			itemRenderer.renderItem(headStack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
	}
}