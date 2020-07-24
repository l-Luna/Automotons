package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class BladeHeadItem extends HeadItem{
	
	public static float BLADE_DAMAGE = 4.5f;
	
	public BladeHeadItem(Settings settings){
		super(settings);
	}
	
	public void moveInto(AutomotonBlockEntity automoton, BlockPos to){
		// get all entities and  h u r t
		if(automoton.getWorld() != null){
			List<Entity> entities = automoton.getWorld().getEntities(null, new Box(to));
			for(Entity entity : entities)
				if(entity instanceof LivingEntity){
					LivingEntity living = (LivingEntity)entity;
					living.damage(DamageSource.GENERIC, BLADE_DAMAGE);
				}
		}
	}
}