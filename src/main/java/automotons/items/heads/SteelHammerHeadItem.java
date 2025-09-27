package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static automotons.Automotons.autoId;

public class SteelHammerHeadItem extends HeadItem<Object>{
	
	public SteelHammerHeadItem(Settings settings){
		super(settings);
	}
	
	protected static final TagKey<Item> TEXT_HOLDERS = TagKey.of(RegistryKeys.ITEM, autoId("text_holder"));
	
	// the head acts when the automoton rotates while engaged
	
	// renaming:
	// if the automoton holds a renamed piece of paper or a name tag (automotons:text_holder), it will rename the items in front of it, free of charge.
	// if the item's begins with "++", the name is instead appended.
	// if the item's name is "~~", followed by an integer N, N letters are removed from the end of the name instead.
	
	// combining:
	// if there are exactly two items in front of the automoton, the items are combined at the cost of XP.
	// combining acts the same way it would in an anvil or smithing table.
	// XP orbs are pulled towards the automoton when it begins to rotate, and orbs in the block in front of the automoton may be consumed.
	
	
	public void startRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object unused){
		// if there are exactly two items in front of the automoton that can be combined, pull nearby XP orbs.
		World world = automoton.getWorld();
		if(world != null){
			List<ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, new Box(to), __ -> true);
			if(itemEntities.size() == 2)
				if(getCombinationOf(itemEntities.get(0).getStack(), itemEntities.get(1).getStack(), world).isPresent())
					for(ExperienceOrbEntity entity : world.getEntitiesByType(EntityType.EXPERIENCE_ORB, new Box(to).expand(4), __ -> true)){
						// add momentum towards the block in front of the automoton
						Vec3d dist = Vec3d.ofCenter(to).subtract(entity.getPos());
						// it should end up there after 10? ticks
						entity.getVelocity().add(dist.multiply(1d / automoton.moduleSpeed()));
					}
		}
	}
	
	public void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object unused){
		World world = automoton.getWorld();
		
		if(world != null){
			List<ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, new Box(to), __ -> true);
			
			if(!itemEntities.isEmpty()){
				// if holding any `automotons:text_holder`, rename items in front
				ItemStack stack = automoton.getStoreStack();
				if(stack.isIn(TEXT_HOLDERS) && stack.hasCustomName()){
					String text = stack.getName().getString();
					if(text.startsWith("++")){
						for(ItemEntity entity : itemEntities){
							ItemStack item = entity.getStack();
							String name = item.getName().getString() + text.substring(2);
							if(name.length() > 40)
								name = name.substring(0, 40);
							item.setCustomName(Text.literal(name));
						}
					}else if(text.startsWith("~~") && text.substring(2).matches("[0-9]+")){
						for(ItemEntity entity : itemEntities){
							ItemStack item = entity.getStack();
							String s = item.getName().getString();
							int endIndex = Math.max(0, s.length() - Integer.decode(text.substring(2)));
							if(endIndex >= 1)
								item.setCustomName(Text.literal(s.substring(1, endIndex)));
						}
					}else
						for(ItemEntity entity : itemEntities)
							entity.getStack().setCustomName(stack.getName());
					world.syncWorldEvent(1030, automoton.getPos(), 0);
				}
				// 1030 = anvil sound
				// 1044 = smithing sound
				
				// if there are exactly two items, attempt to combine them.
				if(itemEntities.size() == 2){
					Optional<Pair<ItemStack, Integer>> combo = getCombinationOf(itemEntities.get(0).getStack(), itemEntities.get(1).getStack(), world);
					if(combo.isPresent()){
						// get XP orbs
						List<ExperienceOrbEntity> xpEntities = world.getEntitiesByType(EntityType.EXPERIENCE_ORB, new Box(to), __ -> true);
						int totalXP = 0;
						for(ExperienceOrbEntity xpEntity : xpEntities)
							totalXP += xpEntity.getExperienceAmount();
						if(combo.get().getRight() <= totalXP){
							itemEntities.forEach(Entity::kill);
							ItemEntity entity = new ItemEntity(world, to.getX() + .5, to.getY() + .5, to.getZ() + .5, combo.get().getLeft());
							entity.setVelocity(0, 0, 0);
							world.spawnEntity(entity);
							world.syncWorldEvent(1044, automoton.getPos(), 0);
						}
					}
				}
			}
		}
	}
	
	protected Optional<Pair<ItemStack, Integer>> getCombinationOf(ItemStack left, ItemStack right, World world){
		return getCombinationOfBiased(left, right, world).or(() -> getCombinationOfBiased(right, left, world));
	}
	
	protected Optional<Pair<ItemStack, Integer>> getCombinationOfBiased(ItemStack left, ItemStack right, World world){
		return getSmithingCombo(left, right, world)
				.or(() -> getEnchantingCombo(left, right))
				.or(() -> getCombineRepairCombo(left, right))
				.or(() -> getMaterialRepairCombo(left, right));
	}
	
	protected Optional<Pair<ItemStack, Integer>> getSmithingCombo(ItemStack left, ItemStack right, World world){
		Inventory smithingInventory = new SimpleInventory(2);
		smithingInventory.setStack(0, left);
		smithingInventory.setStack(1, right);
		Optional<SmithingRecipe> match = world.getRecipeManager().getFirstMatch(RecipeType.SMITHING, smithingInventory, world);
		return match.map(recipe -> new Pair<>(recipe.getOutput(world.getRegistryManager()), 0));
	}
	
	protected Optional<Pair<ItemStack, Integer>> getEnchantingCombo(ItemStack left, ItemStack right){
		// if right is an enchanted book
		if(right.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentNbt(right).isEmpty()){
			int i = 0;
			Map<Enchantment, Integer> original = EnchantmentHelper.get(left);
			Map<Enchantment, Integer> added = EnchantmentHelper.get(right);
			for(Enchantment enchantment : added.keySet()){
				int origLevel = original.getOrDefault(enchantment, 0);
				int newLevel = added.get(enchantment);
				newLevel = origLevel == newLevel ? newLevel + 1 : Math.max(newLevel, origLevel);
				boolean acceptable = enchantment.isAcceptableItem(left);
				if(left.getItem() == Items.ENCHANTED_BOOK)
					acceptable = true;
				for(Enchantment enchantment1 : original.keySet())
					if(enchantment1 != enchantment && !enchantment.canCombine(enchantment1)){
						acceptable = false;
						++i;
					}
				if(acceptable){
					original.put(enchantment, newLevel);
					int v = switch(enchantment.getRarity()){
						case COMMON -> 1;
						case UNCOMMON -> 2;
						case RARE -> 4;
						case VERY_RARE -> 8;
					};
					
					v = Math.max(1, v / 2);
					i += v * newLevel;
				}
			}
			ItemStack out = left.copy();
			EnchantmentHelper.set(original, out);
			return Optional.of(new Pair<>(out, i));
		}
		return Optional.empty();
	}
	
	protected Optional<Pair<ItemStack, Integer>> getCombineRepairCombo(ItemStack left, ItemStack right){
		if(left.isDamageable() && left.getItem() == right.getItem()){
			int cost = 0;
			int leftRemDamage = left.getMaxDamage() - left.getDamage();
			int rightRemDamage = right.getMaxDamage() - right.getDamage();
			int q = rightRemDamage + left.getMaxDamage() * 12 / 100;
			int r = leftRemDamage + q;
			int s = left.getMaxDamage() - r;
			if(s < 0)
				s = 0;
			
			ItemStack out = left.copy();
			if(s < out.getDamage()){
				out.setDamage(s);
				cost += 2;
			}
			return Optional.of(new Pair<>(out, cost));
		}
		return Optional.empty();
	}
	
	protected Optional<Pair<ItemStack, Integer>> getMaterialRepairCombo(ItemStack left, ItemStack right){
		if(left.isDamageable() && left.getItem().canRepair(left, right)){
			int cost = 0;
			ItemStack out = left.copy();
			int o = Math.min(left.getDamage(), left.getMaxDamage() / 4);
			for(int p = 0; o > 0 && p < right.getCount(); ++p){
				int q = out.getDamage() - o;
				out.setDamage(q);
				++cost;
				o = Math.min(out.getDamage(), out.getMaxDamage() / 4);
			}
			return Optional.of(new Pair<>(out, cost));
		}
		return Optional.empty();
	}
	
	public float getEngageOffset(AutomotonBlockEntity automoton, Object o){
		return 4;
	}
}