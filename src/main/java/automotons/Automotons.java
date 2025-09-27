package automotons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Automotons implements ModInitializer{
	
	public static final String MODID = "automotons";
	
	public static final ItemGroup ITEMS = FabricItemGroup
			.builder(autoId("items"))
			.icon(() -> new ItemStack(AutomotonsRegistry.AUTOMOTON.asItem()))
			.entries((ctx, entries) -> AutomotonsRegistry.ALL_ITEMS.forEach(entries::add))
			.build();
	
	public void onInitialize(){
		AutomotonsRegistry.registerObjects();
	}
	
	public static Identifier autoId(String path){
		return new Identifier(MODID, path);
	}
	
	public static boolean isClockwiseRotation(Direction from, Direction to){
		return from.getHorizontal() < to.getHorizontal() && !(from == Direction.SOUTH && to == Direction.EAST) || from == Direction.EAST && to == Direction.SOUTH;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> RegistryEntryLookup<T> lookup(RegistryKey<? extends Registry<T>> key, @Nullable World world){
		if(world != null)
			return world.createCommandRegistryWrapper(key);
		else{
			Registry<?> registry = Registries.REGISTRIES.get(key.getValue());
			if(registry == null)
				throw new IllegalArgumentException("Wrong registry name: " + key);
			return (RegistryEntryLookup<T>)registry.getReadOnlyWrapper();
		}
	}
}