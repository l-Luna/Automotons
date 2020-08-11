package net.automotons.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.automotons.AutomotonsRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunction;

import java.util.function.Consumer;

public class BlockEntityInventoryEntry extends LeafEntry{
	
	protected BlockEntityInventoryEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions){
		super(weight, quality, conditions, functions);
	}
	
	protected void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context){
		BlockEntity entity = context.get(LootContextParameters.BLOCK_ENTITY);
		if(entity instanceof Inventory){
			Inventory inventory = (Inventory)entity;
			for(int i = 0; i < inventory.size(); i++)
				lootConsumer.accept(inventory.getStack(i));
		}
	}
	
	public LootPoolEntryType getType(){
		return AutomotonsRegistry.BLOCK_ENTITY_INVENTORY;
	}
	
	public static class Serializer extends LeafEntry.Serializer<BlockEntityInventoryEntry>{
		
		public BlockEntityInventoryEntry fromJson(JsonObject object, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions){
			return new BlockEntityInventoryEntry(weight, quality, conditions, functions);
		}
	}
}