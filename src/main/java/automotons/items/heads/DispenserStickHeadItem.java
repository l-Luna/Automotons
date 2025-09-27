package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;
import net.minecraft.block.BlockState;
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
	
	public void endEngageInto(AutomotonBlockEntity automoton, BlockPos to, Object __){
		ItemStack stack = automoton.getStack(13);
		DispenserBehavior behavior = DispenserBlock.BEHAVIORS.get(stack.getItem());
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
		
		final AutomotonBlockEntity entity;
		
		public DirectionReplacingBlockState(BlockState deferred, AutomotonBlockEntity entity){
			super(deferred.getBlock(), deferred.getEntries(), deferred.codec);
			this.entity = entity;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Comparable<T>> T get(Property<T> property){
			return property != DispenserBlock.FACING || entity == null ? super.get(property) : (T)entity.facing;
		}
	}
}