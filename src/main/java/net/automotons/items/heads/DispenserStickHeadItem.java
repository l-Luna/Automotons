package net.automotons.items.heads;

import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.automotons.mixin.DispenserBlockAccessor;
import net.automotons.mixin.StateAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenserStickHeadItem extends HeadItem<Object>{
	
	public DispenserStickHeadItem(Settings settings){
		super(settings);
	}
	
	public void engageInto(AutomotonBlockEntity automoton, BlockPos to, Object unused){
		// get the dispenser behaviour
		ItemStack stack = automoton.getStack(13);
		DispenserBehavior behavior = ((DispenserBlockAccessor)Blocks.DISPENSER).callGetBehaviorForItem(stack);
		World world = automoton.getWorld();
		if(world instanceof ServerWorld && !world.isClient())
			behavior.dispense(new DirectionReplacingBlockPointer((ServerWorld)world, automoton.getPos()), stack);
	}
	
	protected static class DirectionReplacingBlockPointer extends BlockPointerImpl{
		
		public DirectionReplacingBlockPointer(ServerWorld serverWorld, BlockPos pos){
			super(serverWorld, pos);
		}
		
		public BlockState getBlockState(){
			return new DirectionReplacingBlockState(super.getBlockState(), getBlockEntity() instanceof AutomotonBlockEntity ? getBlockEntity() : null);
		}
	}
	
	protected static class DirectionReplacingBlockState extends BlockState{
		
		final BlockState deferred;
		final AutomotonBlockEntity entity;
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		public DirectionReplacingBlockState(BlockState deferred, AutomotonBlockEntity entity){
			super(deferred.getBlock(), deferred.getEntries(), ((StateAccessor)deferred).getCodec());
			this.deferred = deferred;
			this.entity = entity;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Comparable<T>> T get(Property<T> property){
			return property != DispenserBlock.FACING || entity == null ? super.get(property) : (T)entity.facing;
		}
	}
}