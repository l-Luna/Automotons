package automotons.items;

import automotons.blocks.AutomotonBlockEntity;
import automotons.broadcast.Broadcast;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModuleItem extends Item implements Module{
	
	private Predicate<AutomotonBlockEntity> execution;
	private BiPredicate<AutomotonBlockEntity, Broadcast> executionFromBroadcast;
	private boolean clientExec = false;
	
	public ModuleItem(Settings settings, Predicate<AutomotonBlockEntity> execution){
		super(settings);
		this.execution = execution;
	}
	
	public static ModuleItem fromConsumer(Settings settings, Consumer<AutomotonBlockEntity> execution){
		return new ModuleItem(settings, entity -> {
			execution.accept(entity);
			return true;
		});
	}
	
	public ActionResult useOnBlock(ItemUsageContext ctx){
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		BlockEntity be = ctx.getWorld().getBlockEntity(pos);
		if(be instanceof AutomotonBlockEntity automoton && automoton.hasNoModules()){
			if(!world.isClient || clientExec)
				execution.test(automoton);
			if(!world.isClient && !clientExec)
				automoton.sync();
			return ActionResult.CONSUME;
		}
		return super.useOnBlock(ctx);
	}
	
	public boolean execute(AutomotonBlockEntity block){
		return execution.test(block);
	}
	
	public ModuleItem withBroadcastExecution(BiPredicate<AutomotonBlockEntity, Broadcast> execution){
		executionFromBroadcast = execution;
		return this;
	}
	
	public boolean executeFromBroadcast(AutomotonBlockEntity block, Broadcast broadcast){
		return executionFromBroadcast != null ? executionFromBroadcast.test(block, broadcast) : execution.test(block);
	}
	
	public ModuleItem withClientExec(){
		clientExec = true;
		return this;
	}
	
	public boolean shouldExecuteOnClient(){
		return clientExec;
	}
}