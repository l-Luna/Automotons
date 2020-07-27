package net.automotons.client;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DrillHeadRenderer implements HeadRenderer<Float>{
	
	public void render(AutomotonBlockEntity automoton, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Float breakingTime, int light, int overlay){
		// add block breaking particles
		World world = automoton.getWorld();
		BlockPos facing = automoton.getPos().offset(automoton.facing);
		if(automoton.engaged && world != null){
			BlockState state = world.getBlockState(facing);
			// Ignore hardness - bedrock gets particles.
			if(!state.isAir()){
				if(breakingTime == null)
					breakingTime = 0f;
				if(breakingTime < 9)
					for(Direction value : Direction.values())
						MinecraftClient.getInstance().particleManager.addBlockBreakingParticles(facing, value);
			}
		}
	}
}