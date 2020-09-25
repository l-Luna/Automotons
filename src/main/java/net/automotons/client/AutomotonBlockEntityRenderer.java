package net.automotons.client;

import net.automotons.Automotons;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.Head;
import net.automotons.skins.AutomotonSkin;
import net.automotons.skins.AutomotonSkins;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static java.lang.Math.min;

public class AutomotonBlockEntityRenderer extends BlockEntityRenderer<AutomotonBlockEntity>{
	
	private final ItemRenderer itemRenderer;
	
	public AutomotonBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher, ItemRenderer renderer){
		super(dispatcher);
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
		BakedModel base = modelManager.getModel(new ModelIdentifier(skin.getBase(), ""));
		BakedModel body = modelManager.getModel(new ModelIdentifier(skin.getBody(), ""));
		
		BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
		BlockState state = entity.getWorld().getBlockState(entity.getPos());
		VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
		manager.getModelRenderer().render(entity.getWorld(), base, state, entity.getPos(), matrices, buffer, false, new Random(), state.getRenderingSeed(entity.getPos()), overlay);
		
		float rotationOffset = 0f;
		if(entity.lastFacing != null && entity.lastFacing != entity.facing){
			if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing))
				rotationOffset = min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1) - 1;
			else
				rotationOffset = 1 - min((entity.moduleTime + tickDelta) / (float)entity.moduleSpeed(), 1);
		}
		
		matrices.push();
		matrices.translate(.5, 0, .5);
		matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
		matrices.translate(-.5, 0, -.5);
		// render main body with rotation
		manager.getModelRenderer().render(entity.getWorld(), body, state, entity.getPos(), matrices, buffer, false, new Random(), state.getRenderingSeed(entity.getPos()), overlay);
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
			if(renderer != null)
				renderer.render(entity, matrices, vertexConsumers, entity.data, light, overlay, tickDelta);
		}
		matrices.pop();
		BlockModelRenderer.disableBrightnessCache();
	}
}