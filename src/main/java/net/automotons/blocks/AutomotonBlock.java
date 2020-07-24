package net.automotons.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class AutomotonBlock extends Block implements BlockEntityProvider{
	
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
	
	public BlockEntity createBlockEntity(BlockView world){
		return new AutomotonBlockEntity();
	}
	
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
		if(world.isClient)
			return ActionResult.SUCCESS;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof AutomotonBlockEntity)
			player.openHandledScreen((AutomotonBlockEntity)blockEntity);
		
		return ActionResult.CONSUME;
	}
}