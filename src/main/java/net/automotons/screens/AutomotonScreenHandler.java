package net.automotons.screens;

import net.automotons.AutomotonsRegistry;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.automotons.items.ModuleItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class AutomotonScreenHandler extends ScreenHandler{
	
	public AutomotonBlockEntity automoton;
	public Inventory inventory;
	private final PlayerInventory playerInventory;
	
	public AutomotonScreenHandler(int syncId, AutomotonBlockEntity automoton, PlayerInventory playerInventory){
		super(AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER, syncId);
		this.automoton = automoton;
		automoton.notifying.add(this);
		inventory = automoton;
		this.playerInventory = playerInventory;
		
		addSlots();
	}
	
	public AutomotonScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf){
		super(AutomotonsRegistry.AUTOMOTON_SCREEN_HANDLER, syncId);
		BlockPos autoPosition = buf.readBlockPos();
		BlockEntity entity = playerInventory.player.world.getBlockEntity(autoPosition);
		if(entity instanceof AutomotonBlockEntity){
			automoton = (AutomotonBlockEntity)entity;
			automoton.notifying.add(this);
		}else
			automoton = null;
		inventory = new SimpleInventory(14);
		this.playerInventory = playerInventory;
		
		addSlots();
	}
	
	protected void addSlots(){
		int y, x;
		// module slots
		for(y = 0; y < 2; ++y)
			for(x = 0; x < 6; ++x)
				addSlot(new Slot(inventory, x + y * 6, 53 + x * 18, 25 + y * 20){
					public boolean canInsert(ItemStack stack){
						return super.canInsert(stack) && stack.getItem() instanceof ModuleItem;
					}
					
					public int getMaxStackAmount() {
						return 1;
					}
				});
		// head slot
		addSlot(new Slot(inventory, 12, 16, 25){
			public boolean canInsert(ItemStack stack){
				return super.canInsert(stack) && stack.getItem() instanceof HeadItem;
			}
		});
		// storage slot
		addSlot(new Slot(inventory, 13, 16, 50));
		
		// player slots
		for(y = 0; y < 3; ++y)
			for(x = 0; x < 9; ++x)
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
		
		for(y = 0; y < 9; ++y)
			addSlot(new Slot(playerInventory, y, 8 + y * 18, 142));
	}
	
	public ItemStack transferSlot(PlayerEntity player, int index){
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot != null && slot.hasStack()){
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if(index < 14){
				if(!this.insertItem(itemStack2, 9, 45, true))
					return ItemStack.EMPTY;
			}else if(!this.insertItem(itemStack2, 0, 9, false))
				return ItemStack.EMPTY;
			if(itemStack2.isEmpty())
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
			if(itemStack2.getCount() == itemStack.getCount())
				return ItemStack.EMPTY;
			
			slot.onTakeItem(player, itemStack2);
		}
		
		if(automoton != null && !automoton.getWorld().isClient)
			automoton.sync();
		
		return itemStack;
	}
	
	public void onContentChanged(Inventory inventory){
		super.onContentChanged(inventory);
		if(automoton != null)
			automoton.sync();
	}
	
	public boolean canUse(PlayerEntity player){
		return inventory.canPlayerUse(player);
	}
}