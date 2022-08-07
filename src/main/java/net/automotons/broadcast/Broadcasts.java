package net.automotons.broadcast;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Optional;

public final class Broadcasts{
	
	/** The radius, in blocks, that broadcasts can be received by an automoton, by manhattan distance. */
	public static final int BROADCAST_RECEIVE_RADIUS = 24;
	
	private Broadcasts(){
	}
	
	/**
	 * Returns the nearest broadcast within range to a location.
	 * Returns <code>Optional.empty()</code> if <code>world</code> or <code>position</code> is <code>null</code>.
	 *
	 * @param world
	 * 		The world of the location.
	 * @param position
	 * 		The position of the location.
	 * @return The nearest broadcast to that location.
	 */
	public static Optional<Broadcast> getNearestBroadcast(World world, BlockPos position){
		if(world == null || position == null)
			return Optional.empty();
		// get all tile entities
		return world.blockEntityTickers.parallelStream()
				// filter by distance
				.filter(ticker -> ticker.getPos().getManhattanDistance(position) <= BROADCAST_RECEIVE_RADIUS)
                // map to the relevant block entity
                .map(ticker -> world.getBlockEntity(ticker.getPos()))
				// filter to automotons
				.filter(AutomotonBlockEntity.class::isInstance)
				.map(AutomotonBlockEntity.class::cast)
				// with a broadcast
				.filter(entity -> entity.getSendingBroadcast() != null)
				// sort by distance
				.sequential().min(Comparator.comparingInt(value -> value.getPos().getManhattanDistance(position)))
				.map(AutomotonBlockEntity::getSendingBroadcast);
	}
	
	/**
	 * Returns the nearest broadcast within range to an automoton.
	 * Returns <code>Optional.empty()</code> if <code>entity</code> is <code>null</code>.
	 *
	 * @param entity
	 * 		The automoton around which broadcasts are being fetched.
	 * @return The nearest broadcast.
	 */
	public static Optional<Broadcast> getNearestBroadcast(BlockEntity entity){
		return entity == null ? Optional.empty() : getNearestBroadcast(entity.getWorld(), entity.getPos());
	}
}