package net.automotons.blocks;

import net.automotons.AutomotonsRegistry;
import net.automotons.items.Head;
import net.automotons.items.Module;
import net.automotons.screens.AutomotonScreenHandler;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AutomotonBlockEntity extends LockableContainerBlockEntity implements Tickable, BlockEntityClientSerializable, ExtendedScreenHandlerFactory, SidedInventory{
	
	// Facing a specific direction
	public Direction facing = Direction.NORTH;
	// Whether the head is forward
	public boolean engaged = false;
	// The module currently being processed
	public int module = 0;
	// The number of ticks already spent processing the module
	public int moduleTime = 0;
	// Modules (0-11), head (12), and store slot (13) inventory
	private DefaultedList<ItemStack> inventory;
	// Used for animations and errors
	public Direction lastFacing = Direction.NORTH;
	public boolean lastEngaged = false;
	public BlockPos lastPos = null;
	// Extra data for head
	public Object data;
	// Whether the current module threw an error
	// Updated after the next module executes, allowing them to act on it
	public boolean errored = false;
	// Whether execution should be paused when an error is thrown
	public boolean stopOnError = false;
	// Which direction to move at the end of this tick (if any)
	public Direction scheduledMove = null;
	// When moving, screens start following the wrong automoton. All screen handlers in this list are corrected.
	// Not serialized or saved (doesn't need to be).
	public List<AutomotonScreenHandler> notifying = new ArrayList<>();
	// Only slot that's exposed to hoppers.
	private int[] STORE_SLOT = new int[]{13};
	
	public AutomotonBlockEntity(){
		super(AutomotonsRegistry.AUTOMOTON_BE);
		inventory = DefaultedList.ofSize(14, ItemStack.EMPTY);
	}
	
	@SuppressWarnings("ConstantConditions")
	public void tick(){
		Module toExecute = atIndex(module);
		if(moduleTime == 0){
			// finish last instruction
			if(lastEngaged != engaged){
				// engage (happens after)
				if(engaged && getHead() != null)
					getHead().engageInto(this, pos.offset(facing), data);
				if(!engaged && getHead() != null)
					getHead().retractFrom(this, pos.offset(facing), data);
				lastEngaged = engaged;
			}
			if(lastFacing != null && lastFacing != facing){
				// rotate into (happens after)
				if(getHead() != null)
					getHead().endRotationInto(this, pos.offset(facing), pos.offset(lastFacing), data);
				lastFacing = facing;
			}
			if(lastPos != null && !lastPos.equals(pos)){
				// move into (happens after)
				if(getHead() != null)
					getHead().endAutomotonMoveInto(this, pos, lastPos, lastPos.offset(facing), pos.offset(facing), data);
				lastPos = pos;
			}
			// run instruction
			if(toExecute != null)
				errored = !toExecute.execute(this);
			getWorld().updateNeighbors(pos, getWorld().getBlockState(pos).getBlock());
		}
		if(!stopOnError || !errored)
			moduleTime++;
		if(moduleTime >= 10 && !(getWorld().isReceivingRedstonePower(pos) && !getWorld().isEmittingRedstonePower(pos, null))){
			moduleTime = 0;
			// move to next instruction
			module++;
		}
		toExecute = atIndex(module);
		if(toExecute == null){
			moduleTime = 0;
			// move to next instruction
			// look for next module
			if(hasNoModules())
				module = 0;
			else
				while(atIndex(module) == null){
					module++;
					if(module >= 12)
						module = 0;
				}
		}
		if(module >= 12)
			module = 0;
		if(getHead() != null)
			getHead().tick(this, pos.offset(facing), data);
		if(scheduledMove != null){
			// we should move now
			BlockPos to = pos.offset(scheduledMove);
			// remove this automoton block and add a new one in the new position
			if(world.setBlockState(pos, Blocks.AIR.getDefaultState())){
				BlockState state = AutomotonsRegistry.AUTOMOTON.getDefaultState();
				world.breakBlock(to, true);
				world.setBlockState(to, state);
				// we've already been removed, and a new one already exists
				// set all our data (w/ serialization methods), but set scheduledMove to null and set lastPos
				AutomotonBlockEntity moved = (AutomotonBlockEntity)world.getBlockEntity(to);
				moved.fromTag(state, addSharedData(moved.toTag(new CompoundTag())));
				moved.lastPos = pos;
				for(AutomotonScreenHandler handler : notifying){
					handler.inventory = moved;
					handler.automoton = moved;
				}
				moved.notifying = notifying;
				if(!world.isClient())
					moved.sync();
			}
		}
	}
	
	public boolean hasNoModules(){
		return inventory.subList(0, 12).stream().allMatch(ItemStack::isEmpty);
	}
	
	public ItemStack getHeadStack(){
		return getStack(12);
	}
	
	public Head getHead(){
		Item item = getHeadStack().getItem();
		return item instanceof Head ? (Head)item : null;
	}
	
	public Module atIndex(int index){
		if(index < 6){
			if(!getStack(index).isEmpty() && getStack(index).getItem() instanceof Module)
				return (Module)getStack(index).getItem();
		}else if(index < 12){
			int revIndex = 11 - (index - 6);
			if(!getStack(revIndex).isEmpty() && getStack(revIndex).getItem() instanceof Module)
				return (Module)getStack(revIndex).getItem();
		}
		return null;
	}
	
	public boolean turnCw(){
		if(getHead() == null || getHead().canRotateInto(this, pos.offset(facing.rotateYClockwise()), pos.offset(facing), data)){
			lastFacing = facing;
			facing = facing.rotateYClockwise();
			// start rotate into (happens before)
			if(getHead() != null)
				getHead().startRotationInto(this, pos.offset(facing), pos.offset(lastFacing), data);
			return true;
		}else
			return false;
	}
	
	public boolean turnCcw(){
		if(getHead() == null || getHead().canRotateInto(this, pos.offset(facing.rotateYCounterclockwise()), pos.offset(facing), data)){
			lastFacing = facing;
			facing = facing.rotateYCounterclockwise();
			// start rotate into (happens before)
			if(getHead() != null)
				getHead().startRotationInto(this, pos.offset(facing), pos.offset(lastFacing), data);
			return true;
		}else
			return false;
	}
	
	public boolean moveForward(){
		return move(facing);
	}
	
	public boolean moveLeft(){
		return move(facing.rotateYCounterclockwise());
	}
	
	public boolean moveRight(){
		return move(facing.rotateYClockwise());
	}
	
	public boolean moveBack(){
		return move(facing.getOpposite());
	}
	
	public boolean move(Direction direction){
		BlockPos to = pos.offset(direction);
		if(getHead() == null || getHead().canAutomotonMoveInto(this, to, getPos(), data)){
			BlockState state = world.getBlockState(to);
			if(state.isAir() || state.getPistonBehavior() == PistonBehavior.DESTROY){
				scheduledMove = direction;
				if(getHead() != null)
					getHead().startAutomotonMoveInto(this, to, getPos(), getPos().offset(facing), to.offset(facing), data);
				return true;
			}
		}
		return false;
	}
	
	public void setEngaged(boolean engaged){
		lastEngaged = this.engaged;
		this.engaged = engaged;
	}
	
	public CompoundTag toTag(CompoundTag tag){
		CompoundTag nbt = super.toTag(tag);
		return addSharedData(nbt);
	}
	
	public CompoundTag addSharedData(CompoundTag nbt){
		nbt.putInt("facing", facing.getId());
		nbt.putInt("lastFacing", lastFacing.getId());
		nbt.putBoolean("engaged", engaged);
		nbt.putBoolean("lastEngaged", lastEngaged);
		nbt.putInt("instruction", module);
		nbt.putInt("instructionTime", moduleTime);
		nbt.putBoolean("errored", errored);
		nbt.putBoolean("stopOnError", stopOnError);
		Inventories.toTag(nbt, inventory);
		if(getHead() != null)
			nbt.put("headData", getHead().getExtraData(data));
		boolean hasLastPos = lastPos != null;
		nbt.putBoolean("hasLastPos", hasLastPos);
		if(hasLastPos){
			nbt.putInt("lastX", lastPos.getX());
			nbt.putInt("lastY", lastPos.getY());
			nbt.putInt("lastZ", lastPos.getZ());
		}
		return nbt;
	}
	
	public void fromTag(BlockState state, CompoundTag tag){
		super.fromTag(state, tag);
		facing = Direction.byId(tag.getInt("facing"));
		lastFacing = Direction.byId(tag.getInt("lastFacing"));
		engaged = tag.getBoolean("engaged");
		lastEngaged = tag.getBoolean("lastEngaged");
		module = tag.getInt("instruction");
		moduleTime = tag.getInt("instructionTime");
		errored = tag.getBoolean("errored");
		stopOnError = tag.getBoolean("stopOnError");
		if(tag.getBoolean("hasLastPos"))
			lastPos = new BlockPos(tag.getInt("lastX"), tag.getInt("lastY"), tag.getInt("lastZ"));
		
		inventory.clear();
		Inventories.fromTag(tag, inventory);
		
		data = null;
		if(getHead() != null)
			data = getHead().readExtraData(tag.getCompound("headData"));
	}
	
	public void setData(Object data){
		this.data = data;
	}
	
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory){
		return new AutomotonScreenHandler(syncId, this, playerInventory);
	}
	
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf){
		// Write our location to buffer
		buf.writeBlockPos(getPos());
	}
	
	protected Text getContainerName(){
		return new TranslatableText("container.automoton");
	}
	
	public int size(){
		return 14;
	}
	
	public boolean isEmpty(){
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}
	
	public ItemStack getStack(int slot){
		return inventory.get(slot);
	}
	
	public ItemStack removeStack(int slot, int amount){
		ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
		if(!itemStack.isEmpty()){
			markDirty();
			sync();
			if(slot == 12)
				data = null;
		}
		return itemStack;
	}
	
	public ItemStack removeStack(int slot){
		ItemStack stack = Inventories.removeStack(inventory, slot);
		sync();
		if(slot == 12)
			data = null;
		return stack;
	}
	
	public void setStack(int slot, ItemStack stack){
		inventory.set(slot, stack);
		if(stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());
		if(slot == 12)
			data = null;
		
		markDirty();
		sync();
	}
	
	public void setStopOnError(boolean stopOnError){
		this.stopOnError = stopOnError;
	}
	
	public boolean canPlayerUse(PlayerEntity player){
		if(world == null)
			return false;
		if(world.getBlockEntity(pos) != this)
			return false;
		return player.squaredDistanceTo(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64;
	}
	
	public void clear(){
		inventory.clear();
		sync();
	}
	
	public void fromClientTag(CompoundTag tag){
		fromTag(world != null ? world.getBlockState(pos) : null, tag);
	}
	
	public CompoundTag toClientTag(CompoundTag tag){
		return toTag(tag);
	}
	
	public int[] getAvailableSlots(Direction side){
		return STORE_SLOT;
	}
	
	public boolean canInsert(int slot, ItemStack stack, Direction dir){
		return true;
	}
	
	public boolean canExtract(int slot, ItemStack stack, Direction dir){
		return true;
	}
}