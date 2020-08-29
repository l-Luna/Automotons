package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ElectromagnetHeadItem extends HeadItem<Object>{
	
	public static final DustParticleEffect IRON = new DustParticleEffect(.7f, .7f, .7f, .5f);
	public static final DustParticleEffect REDSTONE = new DustParticleEffect(1, 0, 0, .5f);
	
	public ElectromagnetHeadItem(Settings settings){
		super(settings);
	}
	
	public void tick(AutomotonBlockEntity automoton, BlockPos facing, Object o){
		if(automoton.engaged && !automoton.getWorld().isReceivingRedstonePower(automoton.getPos())){
			ItemStack stack = automoton.getStoreStack();
			int dist = Math.min(stack.getCount(), 16);
			boolean push;
			if((push = (stack.getItem() == Items.REDSTONE)) || stack.getItem() == Items.IRON_INGOT){
				Direction direction = automoton.facing;
				BlockPos pos = automoton.getPos();
				for(ItemEntity entity : automoton.getWorld().getEntities(EntityType.ITEM, new Box(pos.offset(direction)).expand(dist * direction.getOffsetX(), dist * direction.getOffsetY(), dist * direction.getOffsetZ()), __ -> true)){
					Vec3d movement;
					if(push)
						movement = Vec3d.of(direction.getVector()).multiply(.2);
					else
						movement = Vec3d.of(direction.getVector()).multiply(-.2);
					entity.move(MovementType.SHULKER, movement);
					entity.setVelocity(entity.getVelocity().multiply(.6));
					for(int i = 0; i < 4; i++)
						entity.world.addParticle(push ? REDSTONE : IRON, entity.getX() + RANDOM.nextGaussian() / 7, entity.getY() + RANDOM.nextGaussian() / 7 + .25, entity.getZ() + RANDOM.nextGaussian() / 7, movement.getX() * -3, movement.getY() * -3, movement.getZ() * -3);
				}
			}
		}
	}
}