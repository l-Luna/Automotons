package net.automotons.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import static net.automotons.Automotons.autoId;

public class RoboticsBookItem extends Item{
	
	private static final Identifier BOOK_ID = autoId("robotics");
	
	public RoboticsBookItem(Settings settings){
		super(settings);
	}
	
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
		if(!world.isClient)
			PatchouliAPI.get().openBookGUI((ServerPlayerEntity)user, BOOK_ID);
		return TypedActionResult.success(user.getStackInHand(hand));
	}
}