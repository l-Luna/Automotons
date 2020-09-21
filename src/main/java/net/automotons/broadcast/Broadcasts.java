package net.automotons.broadcast;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Optional;

public final class Broadcasts{
	
	public static final int BROADCAST_RECEIVE_RADIUS = 24;
	
	private Broadcasts(){}
	
	// I did consider doing it the other way around, where the broadcaster sets the broadcast of nearby automotons
	// but I couldn't figure how to make that work :shrug:
	
	public static Optional<Broadcast> getNearestBroadcast(World world, BlockPos position){
		// get all tile entities
		return world.tickingBlockEntities.parallelStream()
				// filter by distance
				.filter(entity -> entity.getPos().getManhattanDistance(position) <= BROADCAST_RECEIVE_RADIUS)
				// filter to automotons
				.filter(AutomotonBlockEntity.class::isInstance)
				.map(AutomotonBlockEntity.class::cast)
				// with a broadcast
				.filter(entity -> entity.getSendingBroadcast() != null)
				// sort by distance
				.sequential().min(Comparator.comparingInt(value -> value.getPos().getManhattanDistance(position)))
				.map(AutomotonBlockEntity::getSendingBroadcast);
	}
	
	public static Optional<Broadcast> getNearestBroadcast(BlockEntity entity){
		return getNearestBroadcast(entity.getWorld(), entity.getPos());
	}
}