package net.automotons.items;

import net.automotons.blocks.AutomotonBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class SkinItem extends Item{
	
	Identifier skin;
	
	public SkinItem(Settings settings, Identifier skin){
		super(settings);
		this.skin = skin;
	}
	
	public ActionResult useOnBlock(ItemUsageContext context){
		World world = context.getWorld();
		BlockEntity entity = world.getBlockEntity(context.getBlockPos());
		if(entity instanceof AutomotonBlockEntity automoton){
			automoton.setSkin(skin, context.getPlayer());
			if(!world.isClient())
				automoton.sync();
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(context);
	}
	
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context){
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("skin." + skin.getNamespace() + "." + skin.getPath()).styled(style -> style.withColor(Formatting.AQUA)));
	}
}