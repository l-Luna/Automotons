package net.automotons;

import net.automotons.blocks.AutomotonBlock;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.HeadItem;
import net.automotons.items.ModuleItem;
import net.automotons.items.RoboticsBookItem;
import net.automotons.items.heads.*;
import net.automotons.loot.BlockEntityInventoryEntry;
import net.automotons.screens.AutomotonScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static net.automotons.Automotons.autoId;
import static net.automotons.items.ModuleItem.fromConsumer;
import static net.minecraft.util.registry.Registry.register;

public class AutomotonsRegistry{
	
	// For BlockItems
	private static final List<Pair<Identifier, Block>> WITH_ITEMS = new ArrayList<>();
	
	// Blocks
	public static Block AUTOMOTON = new AutomotonBlock(FabricBlockSettings.of(Material.METAL).breakByHand(true).strength(6f).nonOpaque().solidBlock((state, world, pos) -> false));
	
	// Item Settings
	private static final Item.Settings TABBED = new Item.Settings().group(Automotons.ITEMS);
	private static final Item.Settings SINGLE_TABBED = new Item.Settings().group(Automotons.ITEMS).maxCount(1);
	
	// Items
	public static Item ROBOTICS_BOOK = new RoboticsBookItem(TABBED);
	
	// Heads
	public static HeadItem<?> STICKY_HEAD = new StickyHeadItem(SINGLE_TABBED);
	public static HeadItem<?> BLADE_HEAD = new BladeHeadItem(SINGLE_TABBED);
	public static HeadItem<?> DRILL_HEAD = new DrillHeadItem(SINGLE_TABBED);
	public static HeadItem<?> REDSTONE_HEAD = new RedstoneHeadItem(SINGLE_TABBED);
	public static HeadItem<?> DISPENSER_ON_A_STICK = new DispenserStickHeadItem(SINGLE_TABBED);
	public static HeadItem<?> NOTE_BLOCK_ON_A_STICK = new NoteBlockStickHeadItem(SINGLE_TABBED);
	
	// Materials
	public static Item BLANK_MODULE = new Item(TABBED);
	public static Item IRON_GEAR = new Item(TABBED);
	
	// Turn & engage
	public static Item NOOP_MODULE = new ModuleItem(TABBED, entity -> true);
	public static Item CW_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::turnCw);
	public static Item CCW_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::turnCcw);
	public static Item ENGAGE_MODULE = fromConsumer(TABBED, entity -> entity.setEngaged(true));
	public static Item DISENGAGE_MODULE = fromConsumer(TABBED, entity -> entity.setEngaged(false));
	public static Item RAND_TURN_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.getWorld() != null && entity.getWorld().random.nextBoolean())
			return entity.turnCw();
		else
			return entity.turnCcw();
	});
	
	// Error handling
	public static Item THROW_ERRORS_MODULE = fromConsumer(TABBED, entity -> entity.setStopOnError(true));
	public static Item SUPPRESS_ERRORS_MODULE = fromConsumer(TABBED, entity -> entity.setStopOnError(false));
	
	// Repetition
	public static Item REPEAT_ON_SUCCESS_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.errored)
			return false;
		entity.module = 0;
		entity.moduleTime = 0;
		return true;
	});
	public static Item REPEAT_SECOND_ROW_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.errored)
			return false;
		entity.module = 6;
		entity.moduleTime = 0;
		return true;
	});
	
	// Movement
	public static Item MOVE_FORWARD_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveForward);
	public static Item MOVE_LEFT_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveLeft);
	public static Item MOVE_RIGHT_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveRight);
	public static Item MOVE_BACK_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveBack);
	
	// Block Entity Types
	public static BlockEntityType<AutomotonBlockEntity> AUTOMOTON_BE = BlockEntityType.Builder
			.create(AutomotonBlockEntity::new, AUTOMOTON)
			.build(null);
	
	// Screens and Screen Handler Types
	public static final ScreenHandlerType<AutomotonScreenHandler> AUTOMOTON_SCREEN_HANDLER =ScreenHandlerRegistry
			.registerExtended(autoId("automoton"), AutomotonScreenHandler::new);
	
	// Loot Pool Entry Types
	public static final LootPoolEntryType BLOCK_ENTITY_INVENTORY = new LootPoolEntryType(new BlockEntityInventoryEntry.Serializer());
	
	public static void registerObjects(){
		// Blocks
		WITH_ITEMS.add(new Pair<>(autoId("automoton"), AUTOMOTON));
		
		for(Pair<Identifier, Block> item : WITH_ITEMS){
			register(Registry.BLOCK, item.getLeft(), item.getRight());
			register(Registry.ITEM, item.getLeft(), new BlockItem(item.getRight(), new Item.Settings().group(Automotons.ITEMS)));
		}
		
		// Items
		register(Registry.ITEM, autoId("robotics_book"), ROBOTICS_BOOK);
		
		register(Registry.ITEM, autoId("sticky_head"), STICKY_HEAD);
		register(Registry.ITEM, autoId("blade_head"), BLADE_HEAD);
		register(Registry.ITEM, autoId("drill_head"), DRILL_HEAD);
		register(Registry.ITEM, autoId("redstone_head"), REDSTONE_HEAD);
		register(Registry.ITEM, autoId("dispenser_on_a_stick"), DISPENSER_ON_A_STICK);
		register(Registry.ITEM, autoId("note_block_on_a_stick"), NOTE_BLOCK_ON_A_STICK);
		
		register(Registry.ITEM, autoId("blank_module"), BLANK_MODULE);
		register(Registry.ITEM, autoId("iron_gear"), IRON_GEAR);
		register(Registry.ITEM, autoId("noop_module"), NOOP_MODULE);
		register(Registry.ITEM, autoId("cw_module"), CW_MODULE);
		register(Registry.ITEM, autoId("ccw_module"), CCW_MODULE);
		register(Registry.ITEM, autoId("engage_module"), ENGAGE_MODULE);
		register(Registry.ITEM, autoId("disengage_module"), DISENGAGE_MODULE);
		register(Registry.ITEM, autoId("rand_turn_module"), RAND_TURN_MODULE);
		register(Registry.ITEM, autoId("throw_errors_module"), THROW_ERRORS_MODULE);
		register(Registry.ITEM, autoId("suppress_errors_module"), SUPPRESS_ERRORS_MODULE);
		register(Registry.ITEM, autoId("repeat_on_success_module"), REPEAT_ON_SUCCESS_MODULE);
		register(Registry.ITEM, autoId("repeat_second_row_module"), REPEAT_SECOND_ROW_MODULE);
		register(Registry.ITEM, autoId("move_forward_module"), MOVE_FORWARD_MODULE);
		register(Registry.ITEM, autoId("move_left_module"), MOVE_LEFT_MODULE);
		register(Registry.ITEM, autoId("move_right_module"), MOVE_RIGHT_MODULE);
		register(Registry.ITEM, autoId("move_back_module"), MOVE_BACK_MODULE);
		
		// Block Entity Types
		register(Registry.BLOCK_ENTITY_TYPE, autoId("automoton"), AUTOMOTON_BE);
		
		// Loot Pool Entry Types
		register(Registry.LOOT_POOL_ENTRY_TYPE, autoId("block_entity_inventory"), BLOCK_ENTITY_INVENTORY);
	}
}