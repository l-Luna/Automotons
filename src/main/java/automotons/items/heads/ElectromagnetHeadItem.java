package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ElectromagnetHeadItem extends HeadItem<Object>{
	
	public static final DustParticleEffect IRON = new DustParticleEffect(new Vector3f(.7f, .7f, .7f), .5f);
	public static final DustParticleEffect REDSTONE = new DustParticleEffect(new Vector3f(1, 0, 0), .5f);
	
	public ElectromagnetHeadItem(Settings settings){
		super(settings);
	}
	
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Object o){
		World world = automoton.getWorld();
		if(automoton.engaged && world != null && !world.isReceivingRedstonePower(automoton.getPos())){
			ItemStack stack = automoton.getStoreStack();
			int dist = Math.min(stack.getCount(), 16);
			boolean push;
			if((push = (stack.getItem() == Items.REDSTONE)) || stack.getItem() == Items.IRON_INGOT){
				Direction direction = automoton.facing;
				BlockPos pos = automoton.getPos();
				Box affects = new Box(pos.offset(direction)).expand(0, .5, 0).union(new Box(pos.offset(direction, dist)));
				List<Entity> affected = new ArrayList<>();
				affected.addAll(world.getEntitiesByType(EntityType.ITEM, affects, __ -> true));
				affected.addAll(world.getEntitiesByType(EntityType.EXPERIENCE_ORB, affects, __ -> true));
				for(Entity entity : affected){
					Vec3d movement;
					if(push)
						movement = Vec3d.of(direction.getVector()).multiply(.2);
					else
						movement = Vec3d.of(direction.getVector()).multiply(-.2);
					entity.move(MovementType.SHULKER, movement);
					if(!entity.verticalCollision)
						entity.setVelocity(entity.getVelocity().multiply(1, .6, 1));
					entity.setVelocity(entity.getVelocity().multiply(.6, 1, .6));
					Random rng = world.random;
					for(int i = 0; i < 4; i++)
						world.addParticle(push ? REDSTONE : IRON, entity.getX() + rng.nextGaussian() / 7, entity.getY() + rng.nextGaussian() / 7 + .25, entity.getZ() + rng.nextGaussian() / 7, movement.getX() * -3, movement.getY() * -3, movement.getZ() * -3);
				}
			}
		}
	}
}