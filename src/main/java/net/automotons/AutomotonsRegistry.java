package net.automotons;

import net.automotons.blocks.AutomotonBlock;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.automotons.items.ModuleItem;
import net.automotons.items.heads.BladeHeadItem;
import net.automotons.items.heads.DrillHeadItem;
import net.automotons.items.heads.StickyHeadItem;
import net.automotons.screens.AutomotonScreen;
import net.automotons.screens.AutomotonScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static net.automotons.Automotons.autoId;
import static net.minecraft.util.registry.Registry.register;

public class AutomotonsRegistry{
	
	// For BlockItems
	private static final List<Pair<Identifier, Block>> WITH_ITEMS = new ArrayList<>();
	
	// Blocks
	public static Block AUTOMOTON = new AutomotonBlock(FabricBlockSettings.of(Material.METAL).breakByHand(true).strength(6f).nonOpaque().solidBlock((state, world, pos) -> false));
	
	// Items
	public static HeadItem<?> STICKY_HEAD = new StickyHeadItem(new Item.Settings().maxCount(1).group(Automotons.ITEMS));
	public static HeadItem<?> BLADE_HEAD = new BladeHeadItem(new Item.Settings().maxCount(1).group(Automotons.ITEMS));
	public static HeadItem<?> DRILL_HEAD = new DrillHeadItem(new Item.Settings().maxCount(1).group(Automotons.ITEMS));
	public static HeadItem<?> REDSTONE_HEAD = new HeadItem<>(new Item.Settings().maxCount(1).group(Automotons.ITEMS));
	
	public static Item BLANK_MODULE = new Item(new Item.Settings().group(Automotons.ITEMS));
	
	public static Item NOOP_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), entity -> true);
	public static Item CW_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), AutomotonBlockEntity::turnCw);
	public static Item CCW_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), AutomotonBlockEntity::turnCcw);
	public static Item ENGAGE_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), entity -> {
		entity.setEngaged(true);
		return true;
	});
	public static Item DISENGAGE_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), entity -> {
		entity.setEngaged(false);
		return true;
	});
	public static Item RAND_TURN_MODULE = new ModuleItem(new Item.Settings().group(Automotons.ITEMS), entity -> {
		if(entity.getWorld() != null && entity.getWorld().random.nextBoolean())
			return entity.turnCw();
		else
			return entity.turnCcw();
	});
	
	// Block Entity Types
	public static BlockEntityType<AutomotonBlockEntity> AUTOMOTON_BE = BlockEntityType.Builder.create(AutomotonBlockEntity::new, AUTOMOTON).build(null);
	
	// Screens and Screen Handler Types
	public static final ScreenHandlerType<AutomotonScreenHandler> AUTOMOTON_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(autoId("automoton"), AutomotonScreenHandler::new);
	
	public static void registerObjects(){
		// Blocks
		WITH_ITEMS.add(new Pair<>(autoId("automoton"), AUTOMOTON));
		
		// Items
		register(Registry.ITEM, autoId("sticky_head"), STICKY_HEAD);
		register(Registry.ITEM, autoId("blade_head"), BLADE_HEAD);
		register(Registry.ITEM, autoId("drill_head"), DRILL_HEAD);
		register(Registry.ITEM, autoId("redstone_head"), REDSTONE_HEAD);
		
		register(Registry.ITEM, autoId("blank_module"), BLANK_MODULE);
		register(Registry.ITEM, autoId("noop_module"), NOOP_MODULE);
		register(Registry.ITEM, autoId("cw_module"), CW_MODULE);
		register(Registry.ITEM, autoId("ccw_module"), CCW_MODULE);
		register(Registry.ITEM, autoId("engage_module"), ENGAGE_MODULE);
		register(Registry.ITEM, autoId("disengage_module"), DISENGAGE_MODULE);
		register(Registry.ITEM, autoId("rand_turn_module"), RAND_TURN_MODULE);
		
		for(Pair<Identifier, Block> item : WITH_ITEMS){
			register(Registry.BLOCK, item.getLeft(), item.getRight());
			register(Registry.ITEM, item.getLeft(), new BlockItem(item.getRight(), new Item.Settings().group(Automotons.ITEMS)));
		}
		
		// Block Entity Types
		register(Registry.BLOCK_ENTITY_TYPE, autoId("automoton"), AUTOMOTON_BE);
		
		// Screens and Screen Handler Types
		ScreenRegistry.register(AUTOMOTON_SCREEN_HANDLER, AutomotonScreen::new);
	}
}