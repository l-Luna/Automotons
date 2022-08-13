package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class BladeHeadItem extends HeadItem<Object>{
	
	public static float BLADE_DAMAGE = 4.5f;
	
	public BladeHeadItem(Settings settings){
		super(settings);
	}
	
	public void moveInto(AutomotonBlockEntity automoton, BlockPos to, Object o){
		if(automoton.engaged)
			hurtAt(to, automoton.getWorld());
	}
	
	private void hurtAt(BlockPos pos, World world){
		// get all entities and deal damage
		if(world != null){
			List<Entity> entities = world.getOtherEntities(null, new Box(pos));
			boolean didHurt = false;
			for(Entity entity : entities)
				if(entity instanceof LivingEntity living){
					didHurt = true;
					living.damage(DamageSource.GENERIC, BLADE_DAMAGE);
				}
			if(didHurt)
				world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.BLOCKS, .5f, 1);
		}
	}
}