package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class BladeHeadItem extends HeadItem<Object>{
	
	public static float BLADE_DAMAGE = 4.5f;
	
	public BladeHeadItem(Settings settings){
		super(settings);
	}
	
	public void engageInto(AutomotonBlockEntity automoton, BlockPos to, Object noop){
		hurtAt(to, automoton.getWorld());
	}
	
	public void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object o){
		hurtAt(from, automoton.getWorld());
		hurtAt(to, automoton.getWorld());
	}
	
	private void hurtAt(BlockPos pos, World world){
		// get all entities and  h u r t
		if(world != null){
			List<Entity> entities = world.getEntities(null, new Box(pos));
			for(Entity entity : entities)
				if(entity instanceof LivingEntity){
					LivingEntity living = (LivingEntity)entity;
					living.damage(DamageSource.GENERIC, BLADE_DAMAGE);
				}
		}
	}
}