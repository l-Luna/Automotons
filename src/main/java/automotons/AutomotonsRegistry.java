package automotons;

import automotons.blocks.AutomotonBlock;
import automotons.blocks.AutomotonBlockEntity;
import automotons.blocks.PointerBlock;
import automotons.broadcast.Broadcast;
import automotons.broadcast.Broadcasts;
import automotons.items.HeadItem;
import automotons.items.ModuleItem;
import automotons.items.RoboticsBookItem;
import automotons.items.SkinItem;
import automotons.items.heads.*;
import automotons.loot.BlockEntityInventoryEntry;
import automotons.screens.AutomotonScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static automotons.Automotons.autoId;
import static automotons.items.ModuleItem.fromConsumer;

public class AutomotonsRegistry{
	
	// For creative tab
	public static final List<Item> ALL_ITEMS = new ArrayList<>();
	
	// For scanning modules
	private static final TagKey<Block> SCANNABLE = TagKey.of(RegistryKeys.BLOCK, autoId("automoton_scannable"));
	private static final TagKey<Block> SCANNABLE_REVERSE = TagKey.of(RegistryKeys.BLOCK, autoId("automoton_scannable_reverse"));
	
	// Blocks
	public static Block AUTOMOTON = new AutomotonBlock(FabricBlockSettings.of(Material.METAL)/*.breakByHand(true)*/.strength(6f).nonOpaque().solidBlock((state, world, pos) -> false));
	public static Block POINTER = new PointerBlock(FabricBlockSettings.of(Material.METAL)/*.breakByHand(true)*/.breakInstantly());
	
	// Item Settings
	private static final Item.Settings TABBED = new Item.Settings();
	private static final Item.Settings SINGLE_TABBED = new Item.Settings().maxCount(1);
	
	// Items
	public static Item ROBOTICS_BOOK = new RoboticsBookItem(TABBED);
	
	// Heads
	public static HeadItem<?> STICKY_HEAD = new StickyHeadItem(SINGLE_TABBED);
	public static HeadItem<?> BLADE_HEAD = new BladeHeadItem(SINGLE_TABBED);
	public static HeadItem<?> DRILL_HEAD = new DrillHeadItem(SINGLE_TABBED);
	public static HeadItem<?> REDSTONE_HEAD = new RedstoneHeadItem(SINGLE_TABBED);
	public static HeadItem<?> ELECTROMAGNET_HEAD = new ElectromagnetHeadItem(SINGLE_TABBED);
	public static HeadItem<?> DISPENSER_ON_A_STICK = new DispenserStickHeadItem(SINGLE_TABBED);
	public static HeadItem<?> NOTE_BLOCK_ON_A_STICK = new NoteBlockStickHeadItem(SINGLE_TABBED);
	public static HeadItem<?> STEEL_HAMMER = new SteelHammerHeadItem(SINGLE_TABBED);
	public static HeadItem<?> BROADCAST_ANTENNAE = new BroadcastAntennaeHead(SINGLE_TABBED);
	public static HeadItem<?> BLOCKLAYER = new BlocklayerHeadItem(SINGLE_TABBED);
	
	// Materials
	public static Item BLANK_MODULE = new Item(TABBED);
	public static Item IRON_GEAR = new Item(TABBED);
	
	// Turn & engage
	public static Item NOOP_MODULE = new ModuleItem(TABBED, entity -> true);
	public static Item CW_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::turnCw).withClientExec();
	public static Item CCW_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::turnCcw).withClientExec();
	public static Item ENGAGE_MODULE = fromConsumer(TABBED, entity -> entity.setEngaged(true)).withClientExec();
	public static Item DISENGAGE_MODULE = fromConsumer(TABBED, entity -> entity.setEngaged(false)).withClientExec();
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
		entity.moduleTime = -1;
		return true;
	});
	public static Item REPEAT_SECOND_ROW_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.errored)
			return false;
		entity.module = 6;
		entity.moduleTime = -1;
		return true;
	});
	
	// Movement
	public static Item MOVE_FORWARD_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveForward).withClientExec();
	public static Item MOVE_LEFT_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveLeft).withClientExec();
	public static Item MOVE_RIGHT_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveRight).withClientExec();
	public static Item MOVE_BACK_MODULE = new ModuleItem(TABBED, AutomotonBlockEntity::moveBack).withClientExec();
	public static Item SCAN_AND_MOVE_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.getWorld() != null){
			BlockState below = entity.getWorld().getBlockState(entity.getPos().down());
			if(below.isIn(SCANNABLE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return entity.move(below.get(HorizontalFacingBlock.FACING));
			if(below.isIn(SCANNABLE_REVERSE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return entity.move(below.get(HorizontalFacingBlock.FACING).getOpposite());
		}
		return false;
	}).withBroadcastExecution((blockEntity, broadcast) -> {
		BlockEntity entity = broadcast.getSource();
		if(blockEntity.getWorld() != null){
			BlockState below = blockEntity.getWorld().getBlockState(entity.getPos().down());
			if(below.isIn(SCANNABLE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return blockEntity.move(below.get(HorizontalFacingBlock.FACING));
			if(below.isIn(SCANNABLE_REVERSE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return blockEntity.move(below.get(HorizontalFacingBlock.FACING).getOpposite());
		}
		return false;
	});
	
	public static Item SCAN_AND_ROTATE_MODULE = new ModuleItem(TABBED, entity -> {
		if(entity.getWorld() != null){
			BlockState below = entity.getWorld().getBlockState(entity.getPos().down());
			if(below.isIn(SCANNABLE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return entity.turnTo(below.get(HorizontalFacingBlock.FACING));
			if(below.isIn(SCANNABLE_REVERSE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return entity.turnTo(below.get(HorizontalFacingBlock.FACING).getOpposite());
		}
		return false;
	}).withBroadcastExecution((blockEntity, broadcast) -> {
		AutomotonBlockEntity entity = broadcast.getSource();
		if(blockEntity.getWorld() != null){
			BlockState below = blockEntity.getWorld().getBlockState(entity.getPos().down());
			if(below.isIn(SCANNABLE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return blockEntity.turnTo(below.get(HorizontalFacingBlock.FACING));
			if(below.isIn(SCANNABLE_REVERSE) && below.getProperties().contains(HorizontalFacingBlock.FACING))
				return blockEntity.turnTo(below.get(HorizontalFacingBlock.FACING).getOpposite());
		}
		return false;
	});
	
	// Broadcasts
	public static Item START_BROADCAST_MODULE = ModuleItem.fromConsumer(TABBED, AutomotonBlockEntity::generateBroadcast);
	public static Item END_BROADCAST_MODULE = ModuleItem.fromConsumer(TABBED, entity -> entity.setBroadcast(null));
	public static Item KILL_BROADCAST_MODULE = ModuleItem.fromConsumer(TABBED, entity -> Broadcasts.getNearestBroadcast(entity).ifPresent(broadcast -> {
		broadcast.kill();
		entity.setOutlineColour(250, 0, 0);
	})).withClientExec();
	public static Item RECEIVE_BROADCAST_MODULE = new ModuleItem(TABBED, entity -> {
		Optional<Broadcast> nearest = Broadcasts.getNearestBroadcast(entity);
		if(nearest.isPresent()){
			var broadcast = nearest.get();
			if(broadcast.getInstruction() != null){
				entity.setOutlineColour(135, 206, 250);
				return broadcast.getInstruction().executeFromBroadcast(entity, broadcast);
			}else{
				entity.setOutlineColour(250, 250, 0);
				return false; // what triggers this?
			}
		}
		entity.setOutlineColour(250, 100, 100);
		return false;
	}).withBroadcastExecution((entity, broadcast) -> {
		// a broadcasting automoton will always receive its own broadcast
		// so a Receive Broadcast instruction will execute itself, leading to SOE
		// instead we no-op and error
		return false;
	});
	
	// Skins
	public static Item REGULAR_SKIN = new SkinItem(TABBED, autoId("regular"));
	public static Item CHARCOAL_SKIN = new SkinItem(TABBED, autoId("charcoal"));
	public static Item WOOD_SKIN = new SkinItem(TABBED, autoId("wood"));
	public static Item FACTORY_SKIN = new SkinItem(TABBED, autoId("factory"));
	
	// Block Entity Types
	public static BlockEntityType<AutomotonBlockEntity> AUTOMOTON_BE = FabricBlockEntityTypeBuilder
			.create(AutomotonBlockEntity::new, AUTOMOTON)
			.build(null);
	
	// Screens and Screen Handler Types
	public static final ScreenHandlerType<AutomotonScreenHandler> AUTOMOTON_SCREEN_HANDLER = ScreenHandlerRegistry
			.registerExtended(autoId("automoton"), AutomotonScreenHandler::new);
	
	// Loot Pool Entry Types
	public static final LootPoolEntryType BLOCK_ENTITY_INVENTORY = new LootPoolEntryType(new BlockEntityInventoryEntry.Serializer());
	
	public static void registerObjects(){
		// Blocks
		List<Pair<String, Block>> blocks = new ArrayList<>();
		blocks.add(new Pair<>("automoton", AUTOMOTON));
		blocks.add(new Pair<>("pointer", POINTER));
		
		for(var item : blocks){
			register(item.getLeft(), item.getRight());
			register(item.getLeft(), new BlockItem(item.getRight(), new Item.Settings()));
		}
		
		// Items
		register("robotics_book", ROBOTICS_BOOK);
		
		register("sticky_head", STICKY_HEAD);
		register("blade_head", BLADE_HEAD);
		register("drill_head", DRILL_HEAD);
		register("redstone_head", REDSTONE_HEAD);
		register("electromagnet_head", ELECTROMAGNET_HEAD);
		register("dispenser_on_a_stick", DISPENSER_ON_A_STICK);
		register("note_block_on_a_stick", NOTE_BLOCK_ON_A_STICK);
		register("steel_hammer", STEEL_HAMMER);
		register("broadcast_antennae", BROADCAST_ANTENNAE);
		register("blocklayer_head", BLOCKLAYER);
		
		register("blank_module", BLANK_MODULE);
		register("iron_gear", IRON_GEAR);
		
		register("noop_module", NOOP_MODULE);
		register("cw_module", CW_MODULE);
		register("ccw_module", CCW_MODULE);
		register("engage_module", ENGAGE_MODULE);
		register("disengage_module", DISENGAGE_MODULE);
		register("rand_turn_module", RAND_TURN_MODULE);
		register("throw_errors_module", THROW_ERRORS_MODULE);
		register("suppress_errors_module", SUPPRESS_ERRORS_MODULE);
		register("repeat_on_success_module", REPEAT_ON_SUCCESS_MODULE);
		register("repeat_second_row_module", REPEAT_SECOND_ROW_MODULE);
		register("move_forward_module", MOVE_FORWARD_MODULE);
		register("move_left_module", MOVE_LEFT_MODULE);
		register("move_right_module", MOVE_RIGHT_MODULE);
		register("move_back_module", MOVE_BACK_MODULE);
		register("scan_and_move_module", SCAN_AND_MOVE_MODULE);
		register("scan_and_rotate_module", SCAN_AND_ROTATE_MODULE);
		register("start_broadcast_module", START_BROADCAST_MODULE);
		register("end_broadcast_module", END_BROADCAST_MODULE);
		register("kill_broadcast_module", KILL_BROADCAST_MODULE);
		register("receive_broadcast_module", RECEIVE_BROADCAST_MODULE);
		
		register("regular_skin", REGULAR_SKIN);
		register("charcoal_skin", CHARCOAL_SKIN);
		register("wood_skin", WOOD_SKIN);
		register("factory_skin", FACTORY_SKIN);
		
		// Block Entity Types
		register("automoton", AUTOMOTON_BE);
		
		// Loot Pool Entry Types
		register("block_entity_inventory", BLOCK_ENTITY_INVENTORY);
	}
	
	@SuppressWarnings("unchecked")
	private static void register(String id, @NotNull Object value){
		Registry<?> r = null; // type switch would be useful here
		if(value instanceof Item i){
			r = Registries.ITEM;
			ALL_ITEMS.add(i);
		}else if(value instanceof Block)
			r = Registries.BLOCK;
		else if(value instanceof BlockEntityType<?>)
			r = Registries.BLOCK_ENTITY_TYPE;
		else if(value instanceof LootPoolEntryType)
			r = Registries.LOOT_POOL_ENTRY_TYPE;
		else
			throw new IllegalArgumentException("Don't know how to register " + value.getClass());
		Registry.register((Registry<Object>)r, autoId(id), value);
	}
}