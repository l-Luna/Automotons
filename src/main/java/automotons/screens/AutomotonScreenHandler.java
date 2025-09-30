package automotons.screens;

import automotons.AutomotonsRegistry;
import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import automotons.items.ModuleItem;
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
		BlockEntity entity = playerInventory.player.getWorld().getBlockEntity(autoPosition);
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
					
					public int getMaxItemCount(){
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
		// first 14 are automoton slots
		
		// player slots
		for(y = 0; y < 3; ++y)
			for(x = 0; x < 9; ++x)
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 104 + y * 18));
		
		for(y = 0; y < 9; ++y)
			addSlot(new Slot(playerInventory, y, 8 + y * 18, 162));
	}
	
	public ItemStack quickMove(PlayerEntity player, int index){
		ItemStack remaining = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot.hasStack()){
			ItemStack inserting = slot.getStack();
			remaining = inserting.copy();
			int num = automoton.moduleNum() + 2;
			if(index < num){
				if(!insertItem(inserting, num, num + 36, true))
					return ItemStack.EMPTY;
			}else if(!insertItem(inserting, 0, num, false))
				return ItemStack.EMPTY;
			if(inserting.isEmpty())
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
			if(inserting.getCount() == remaining.getCount())
				return ItemStack.EMPTY;
			
			slot.onTakeItem(player, inserting);
		}
		
		return remaining;
	}
	
	public void onContentChanged(Inventory inventory){
		super.onContentChanged(inventory);
		if(automoton != null)
			automoton.sync();
	}
	
	public boolean canUse(PlayerEntity player){
		return inventory.canPlayerUse(player);
	}
	
	public void switchAutomoton(AutomotonBlockEntity automoton){
		inventory = automoton;
		this.automoton = automoton;
		for(int i = 0; i < 14; i++)
			getSlot(i).inventory = automoton;
	}
	
	// variant that respects max slot counts
	protected boolean insertItem(ItemStack inserting, int startIdx, int endIdx, boolean fromLast){
		boolean done = false;
		int i = startIdx;
		if(fromLast)
			i = endIdx - 1;
		
		if(inserting.isStackable()){
			while(!inserting.isEmpty() && (fromLast ? i >= startIdx : i < endIdx)){
				Slot slot = slots.get(i);
				int thisMaxCount = Math.min(slot.getMaxItemCount(), inserting.getMaxCount());
				ItemStack existing = slot.getStack();
				if(!existing.isEmpty() && ItemStack.canCombine(inserting, existing)){
					int newCount = existing.getCount() + inserting.getCount();
					if(newCount <= thisMaxCount){
						inserting.setCount(0);
						existing.setCount(newCount);
						slot.markDirty();
						done = true;
					}else if(existing.getCount() < thisMaxCount){
						inserting.decrement(thisMaxCount - existing.getCount());
						existing.setCount(thisMaxCount);
						slot.markDirty();
						done = true;
					}
				}
				
				if(fromLast)
					i--;
				else
					i++;
			}
		}
		
		if(!inserting.isEmpty()){
			if(fromLast)
				i = endIdx - 1;
			else
				i = startIdx;
			
			while(fromLast ? i >= startIdx : i < endIdx){
				Slot slot = slots.get(i);
				ItemStack existing = slot.getStack();
				if(existing.isEmpty() && slot.canInsert(inserting)){
					if(inserting.getCount() > slot.getMaxItemCount())
						slot.setStack(inserting.split(slot.getMaxItemCount()));
					else
						slot.setStack(inserting.split(inserting.getCount()));
					
					slot.markDirty();
					done = true;
					break;
				}
				
				if(fromLast)
					i--;
				else
					i++;
			}
		}
		
		return done;
	}
}