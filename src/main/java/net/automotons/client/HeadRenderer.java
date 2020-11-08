package net.automotons.client;

import net.automotons.AutomotonsRegistry;
import net.automotons.blocks.AutomotonBlockEntity;
import net.automotons.items.Head;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public interface HeadRenderer<Data>{
	
	/**
	 * When rendering an automoton, this map is checked for a head renderer for the given head.
	 * Register head renderers here, e.g.
	 * <pre>
	 * RENDERERS.put(AutomotonsRegistry.STICKY_HEAD, new StickyHeadRenderer());
	 * </pre>
	 */
	Map<Head<?>, HeadRenderer<?>> RENDERERS = new HashMap<>();
	
	static void init(){
		RENDERERS.put(AutomotonsRegistry.STICKY_HEAD, new StickyHeadRenderer());
		RENDERERS.put(AutomotonsRegistry.DRILL_HEAD, new DrillHeadRenderer());
		RENDERERS.put(AutomotonsRegistry.BLOCKLAYER, new BlocklayerHeadRenderer());
	}
	
	/**
	 * Allows a head to render any additional effects. This is called in addition to and after the head is rendered
	 * in the regular way, assuming <code>doNormalRender</code> returns true - otherwise, only this is called.
	 * <p>
	 * The matrix stack has already been translated based on the automoton's position when moving, but <em>not</em> rotated.
	 *
	 * @param automoton
	 * 		The automoton being rendered.
	 * @param matrices
	 * 		The <pre>MatrixStack</pre> used for rendering.
	 * @param vertexConsumers
	 * 		The <pre>VertexConsumerProvider</pre> used for rendering.
	 * @param data
	 * 		The extra data stored for the head.
	 * @param light
	 * 		The light at the automoton.
	 * @param overlay
	 * 		The overlay colour at the automoton.
	 * @param tickDelta
	 * 		The time since the previous tick, used for interpolation.
	 */
	default void render(AutomotonBlockEntity automoton, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Data data, int light, int overlay, float tickDelta){
	}
	
	/**
	 * Whether the automoton should render the head in the regular way i.e. rendering the item model with rotation and offset.
	 * This does not affect whether <pre>render</pre> is called.
	 *
	 * @param automoton
	 * 		The block entity being rendered.
	 * @param data
	 * 		The extra data stored for the head.
	 * @return Whether the automoton should render the head in the regular way.
	 */
	default boolean doNormalRender(AutomotonBlockEntity automoton, Data data){
		return true;
	}
}