package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.automotons.mixin.ExperienceOrbEntityAccessor;
import net.fabricmc.fabric.api.tag.TagRegistry;
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
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static net.automotons.Automotons.autoId;

public class SteelHammerHeadItem extends HeadItem<Object>{
	
	public SteelHammerHeadItem(Settings settings){
		super(settings);
	}
	
	protected static final Tag<Item> TEXT_HOLDERS = TagRegistry.item(autoId("text_holder"));
	
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
		List<ItemEntity> itemEntities = automoton.getWorld().getEntitiesByType(EntityType.ITEM, new Box(to), __ -> true);
		if(itemEntities.size() == 2)
			if(getCombinationOf(itemEntities.get(0).getStack(), itemEntities.get(1).getStack(), automoton.getWorld()).isPresent())
				for(ExperienceOrbEntity entity : automoton.getWorld().getEntitiesByType(EntityType.EXPERIENCE_ORB, new Box(to).expand(4), __ -> true)){
					// add momentum towards the block in front of the automoton
					Vec3d dist = Vec3d.ofCenter(to).subtract(entity.getPos());
					// it should end up there after 10? ticks
					entity.getVelocity().add(dist.multiply(1d / automoton.moduleSpeed()));
				}
	}
	
	public void endRotationInto(AutomotonBlockEntity automoton, BlockPos to, BlockPos from, Object unused){
		World world = automoton.getWorld();
		List<ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, new Box(to), __ -> true);
		
		if(itemEntities.size() > 0){
			// if holding any `automotons:text_holder`, rename items in front
			ItemStack stack = automoton.getStoreStack();
			if(stack.getItem().isIn(TEXT_HOLDERS) && stack.hasCustomName()){
				// RIP formatting
				String text = stack.getName().asString();
				if(text.startsWith("++")){
					for(ItemEntity entity : itemEntities){
						ItemStack item = entity.getStack();
						String name = item.getName().asString() + text.substring(2);
						if(name.length() > 40)
							name = name.substring(0, 40);
						item.setCustomName(new LiteralText(name));
					}
				}else if(text.startsWith("~~") && text.substring(2).matches("[0-9]+")){
					for(ItemEntity entity : itemEntities){
						ItemStack item = entity.getStack();
						String s = item.getName().asString();
						int endIndex = Math.max(0, s.length() - Integer.decode(text.substring(2)));
						if(endIndex >= 1)
							item.setCustomName(new LiteralText(s.substring(1, endIndex)));
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
				Optional<Supplier<Pair<ItemStack, Integer>>> comboGetter = getCombinationOf(itemEntities.get(0).getStack(), itemEntities.get(1).getStack(), world);
				if(comboGetter.isPresent()){
					// get XP orbs
					List<ExperienceOrbEntity> xpEntities = world.getEntitiesByType(EntityType.EXPERIENCE_ORB, new Box(to), __ -> true);
					int totalXP = 0;
					for(ExperienceOrbEntity xpEntity : xpEntities)
						totalXP += ((ExperienceOrbEntityAccessor)xpEntity).getAmount();
					if(comboGetter.get().get().getRight() <= totalXP){
						itemEntities.forEach(Entity::kill);
						ItemEntity entity = new ItemEntity(world, to.getX() + .5, to.getY() + .5, to.getZ() + .5, comboGetter.get().get().getLeft());
						entity.setVelocity(0, 0, 0);
						world.spawnEntity(entity);
						world.syncWorldEvent(1044, automoton.getPos(), 0);
					}
				}
			}
		}
	}
	
	protected Optional<Supplier<Pair<ItemStack, Integer>>> getCombinationOf(ItemStack left, ItemStack right, World world){
		// is this what clojure is like?
		return Optional.ofNullable(
				getSmithingCombo(left, right, world)
						.orElse(getSmithingCombo(right, left, world)
								.orElse(getEnchantingCombo(left, right)
										.orElse(getEnchantingCombo(right, left)
												.orElse(getCombineRepairCombo(left, right)
														.orElse(getCombineRepairCombo(right, left)
																.orElse(getMaterialRepairCombo(left, right)
																		.orElse(getMaterialRepairCombo(right, left)
																				.orElse(null)))))))));
	}
	
	protected Optional<Supplier<Pair<ItemStack, Integer>>> getSmithingCombo(ItemStack left, ItemStack right, World world){
		Inventory smithingInventory = new SimpleInventory(2);
		smithingInventory.setStack(0, left);
		smithingInventory.setStack(1, right);
		Optional<SmithingRecipe> match = world.getRecipeManager().getFirstMatch(RecipeType.SMITHING, smithingInventory, world);
		return match.map(recipe -> () -> new Pair<>(recipe.getOutput(), 0));
	}
	
	protected Optional<Supplier<Pair<ItemStack, Integer>>> getEnchantingCombo(ItemStack left, ItemStack right){
		// if right is an enchanted book
		if(right.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentTag(right).isEmpty()){
			return Optional.of(() -> {
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
						int v = 0;
						switch(enchantment.getRarity()){
							case COMMON:
								v = 1;
								break;
							case UNCOMMON:
								v = 2;
								break;
							case RARE:
								v = 4;
								break;
							case VERY_RARE:
								v = 8;
						}
						
						v = Math.max(1, v / 2);
						i += v * newLevel;
					}
				}
				ItemStack out = left.copy();
				EnchantmentHelper.set(original, out);
				return new Pair<>(out, i);
			});
		}
		return Optional.empty();
	}
	
	protected Optional<Supplier<Pair<ItemStack, Integer>>> getCombineRepairCombo(ItemStack left, ItemStack right){
		if(left.isDamageable() && left.getItem() == right.getItem()){
			return Optional.of(() -> {
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
				return new Pair<>(out, cost);
			});
		}
		return Optional.empty();
	}
	
	protected Optional<Supplier<Pair<ItemStack, Integer>>> getMaterialRepairCombo(ItemStack left, ItemStack right){
		if(left.isDamageable() && left.getItem().canRepair(left, right)){
			return Optional.of(() -> {
				int cost = 0;
				ItemStack out = left.copy();
				int o = Math.min(left.getDamage(), left.getMaxDamage() / 4);
				for(int p = 0; o > 0 && p < right.getCount(); ++p) {
					int q = out.getDamage() - o;
					out.setDamage(q);
					++cost;
					o = Math.min(out.getDamage(), out.getMaxDamage() / 4);
				}
				return new Pair<>(out, cost);
			});
		}
		return Optional.empty();
	}
	
	public float getEngageOffset(AutomotonBlockEntity automoton, Object o){
		return 4;
	}
}