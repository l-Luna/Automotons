package automotons.client;

import automotons.Automotons;
import automotons.blocks.AutomotonBlockEntity;
import automotons.items.Head;
import automotons.skins.AutomotonSkin;
import automotons.skins.AutomotonSkins;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;

import java.util.BitSet;
import java.util.List;

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
			double progress = 1 - min((entity.moduleTime + tickDelta) / (double)entity.moduleSpeed(), 1);
			double xProgress = (entity.getPos().getX() - entity.lastPos.getX()) * progress * -1;
			double yProgress = (entity.getPos().getY() - entity.lastPos.getY()) * progress * -1;
			double zProgress = (entity.getPos().getZ() - entity.lastPos.getZ()) * progress * -1;
			matrices.translate(xProgress, yProgress, zProgress);
		}
		
		// render base
		BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
		AutomotonSkin skin = AutomotonSkins.getSkin(entity.getSkin());
		BakedModel base = BakedModelManagerHelper.getModel(modelManager, skin.base());
		BakedModel body = BakedModelManagerHelper.getModel(modelManager, skin.body());
		
		BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
		BlockState state = entity.getWorld().getBlockState(entity.getPos());
		// TODO: translucency doesn't work with the coloured indicator. find a better indicator?
		VertexConsumer buffer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntitySolid());
		manager.getModelRenderer().render(entity.getWorld(), base, state, entity.getPos(), matrices, buffer, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
		// if the automoton has a colour indicator, add a coloured outline
		ColourVertexConsumer ovc = new ColourVertexConsumer(vertexConsumers.getBuffer(getColourOverlay()), matrices.peek().getPositionMatrix(), matrices.peek().getNormalMatrix());
		float progress = (entity.moduleTime + tickDelta) / (float)entity.moduleSpeed();
		entity.getOutlineColour().ifPresent((red, green, blue) -> {
			matrices.push();
			ovc.fixedColor(red, green, blue, MathHelper.abs((int)(MathHelper.sin((float)(Math.PI * min(progress, 1))) * 200)));
			matrices.translate(-0.02, -0.02, -0.02);
			matrices.scale(1.04f, 1.04f, 1.04f);
			manager.getModelRenderer().render(entity.getWorld(), base, state, entity.getPos(), matrices, ovc, false, Random.create(), state.getRenderingSeed(entity.getPos()), overlay);
			matrices.pop();
		});
		
		float rotationOffset = 0f;
		if(entity.lastFacing != null && entity.lastFacing != entity.facing)
			if(Automotons.isClockwiseRotation(entity.lastFacing, entity.facing))
				rotationOffset = min(progress, 1) - 1;
			else
				rotationOffset = 1 - min(progress, 1);
		
		matrices.push();
		matrices.translate(.5, 0, .5);
		matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
		matrices.translate(-.5, 0, -.5);
		// render main body with rotation
		renderFlat(manager.getModelRenderer(), entity.getWorld(), body, state, entity.lastPos, entity.getPos(), matrices, buffer, Random.create(), state.getRenderingSeed(entity.getPos()), overlay, entity.lastFacing, entity.facing, progress);
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
		if(!headStack.isEmpty() && headStack.getItem() instanceof Head head){
			HeadRenderer renderer = HeadRenderer.RENDERERS.get(head);
			if(renderer == null || renderer.doNormalRender(entity, entity.data)){
				matrices.push();
				// transition between engaged/disengaged
				float engageProgress = entity.engaged ? 1 : 0;
				if(entity.lastEngaged && !entity.engaged)
					engageProgress = 1 - min(progress, 1);
				else if(!entity.lastEngaged && entity.engaged)
					engageProgress = min(progress, 1);
				float offset = (head.getEngageOffset(entity, entity.data) / 16f) * engageProgress;
				// move to proper position (on automoton)
				matrices.translate(.5, 14 / 16f, .5);
				// rotate to facing
				matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90 * (entity.facing.getHorizontal() + rotationOffset - 1)));
				// make the item flat
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
				// apply engaged/disengaged transformation
				matrices.translate(-offset * 2, 0, 0);
				// more facing
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
				// render item
				itemRenderer.renderItem(headStack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);
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
			COLOUR_OVERLAY = new RenderLayer.MultiPhase("automotons:colour_overlay",
					VertexFormats.POSITION_COLOR_LIGHT, DrawMode.QUADS, 256, false, true,
					RenderLayer.MultiPhaseParameters.builder()
							.writeMaskState(RenderPhase.COLOR_MASK)
							.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
							.texture(RenderPhase.NO_TEXTURE)
							.cull(RenderPhase.DISABLE_CULLING)
							.lightmap(RenderPhase.ENABLE_LIGHTMAP)
							.program(RenderPhase.COLOR_PROGRAM)
							.build(false));
		return COLOUR_OVERLAY;
	}
	
	private static final Direction[] DIRECTIONS = Direction.values();
	
	private static void renderFlat(
			BlockModelRenderer renderer,
			BlockRenderView world,
			BakedModel model,
			BlockState state,
			BlockPos posA,
			BlockPos posB,
			MatrixStack matrices,
			VertexConsumer vc,
			Random random,
			long seed,
			int overlay,
			Direction fromDir,
			Direction toDir,
			float progress
	){
		BitSet bits = new BitSet(3);
		BlockPos.Mutable cursorA = posA.mutableCopy(), cursorB = posB.mutableCopy();
		
		// render edge quads
		for(Direction direction : DIRECTIONS){
			random.setSeed(seed);
			List<BakedQuad> directional = model.getQuads(state, direction, random);
			if(!directional.isEmpty()){
				cursorA.set(posA, rotateHBy(direction, fromDir));
				cursorB.set(posB, rotateHBy(direction, toDir));
				int light = interpLight(world, state, progress, cursorA, cursorB);
				renderer.renderQuadsFlat(world, state, posA, light, overlay, false, matrices, vc, directional, bits);
			}
		}
		
		// render non-edge quads
		random.setSeed(seed);
		List<BakedQuad> inner = model.getQuads(state, null, random);
		for(BakedQuad quad : inner){
			Direction direction = quad.getFace();
			cursorA.set(posA, rotateHBy(direction, fromDir));
			cursorB.set(posB, rotateHBy(direction, toDir));
			int light = interpLight(world, state, progress, cursorA, cursorB);
			renderer.renderQuadsFlat(world, state, posA, light, overlay, false, matrices, vc, List.of(quad), bits);
		}
	}
	
	private static int interpLight(BlockRenderView world, BlockState state, float progress, BlockPos.Mutable cursorA, BlockPos.Mutable cursorB){
		int light;
		if(!cursorA.equals(cursorB))
			light = getInterpLightmapCoordinates(world, state, cursorA, cursorB, progress);
		else
			light = WorldRenderer.getLightmapCoordinates(world, state, cursorA);
		return light;
	}
	
	private static int getInterpLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos a, BlockPos b, float progress){
		if(state.hasEmissiveLighting(world, a))
			return 15728880;
		else{
			int aSky = world.getLightLevel(LightType.SKY, a);
			int aBlock = world.getLightLevel(LightType.BLOCK, a);
			int bSky = world.getLightLevel(LightType.SKY, b);
			int bBlock = world.getLightLevel(LightType.BLOCK, b);
			int luminance = state.getLuminance();
			if(aBlock < luminance)
				aBlock = luminance;
			if(bBlock < luminance)
				bBlock = luminance;
			
			return ((int)MathHelper.lerp(progress, aSky, bSky)) << 20 | ((int)MathHelper.lerp(progress, aBlock, bBlock)) << 4;
		}
	}
	
	private static Direction rotateHBy(Direction a, Direction b){
		if(a == Direction.UP || a == Direction.DOWN)
			return a;
		// NORTH is the default rotation/unrotated, but it has a value of 2, so correct here
		return Direction.fromHorizontal(a.getHorizontal() + b.getHorizontal() + 3);
	}
}