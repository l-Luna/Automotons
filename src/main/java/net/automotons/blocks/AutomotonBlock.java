package net.automotons.blocks;

import net.automotons.AutomotonsRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class AutomotonBlock extends BlockWithEntity{
	
	public static VoxelShape SHAPE = VoxelShapes.union(
			Block.createCuboidShape(0, 2, 0, 16, 12, 16),
			Block.createCuboidShape(2, 0, 2, 14, 2, 14),
			Block.createCuboidShape(7, 12, 7, 9, 14, 9)
	);
	
	public AutomotonBlock(Settings settings){
		super(settings);
	}
	
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
		return SHAPE;
	}
	
	public BlockEntity createBlockEntity(BlockPos pos,  BlockState state){
		return new AutomotonBlockEntity(pos, state);
	}
	
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World _world, BlockState _state, BlockEntityType<T> type){
		return checkType(type, AutomotonsRegistry.AUTOMOTON_BE, (world, pos, state, entity) -> entity.tick());
	}
	
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
		if(world.isClient)
			return ActionResult.SUCCESS;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof AutomotonBlockEntity)
			player.openHandledScreen((AutomotonBlockEntity)blockEntity);
		
		return ActionResult.CONSUME;
	}
	
	public boolean emitsRedstonePower(BlockState state){
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction){
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof AutomotonBlockEntity entity)
			if(entity.getHead() != null)
				return entity.getHead().getStrongPowerTo(entity, direction, entity.data);
		return super.getStrongRedstonePower(state, world, pos, direction);
	}
	
	@SuppressWarnings("unchecked")
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction){
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof AutomotonBlockEntity entity)
			if(entity.getHead() != null)
				return entity.getHead().getWeakPowerTo(entity, direction, entity.data);
		return super.getStrongRedstonePower(state, world, pos, direction);
	}
	
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}
	
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity entity = world.getBlockEntity(pos);
		if(entity instanceof AutomotonBlockEntity te){
			ItemStack stack = te.getStack(13);
			return MathHelper.floor((float)stack.getCount() / (float)Math.min(te.getMaxCountPerStack(), stack.getMaxCount()) * 14.0F) + (stack.isEmpty() ? 1 : 0);
		}
		return 0;
	}
}