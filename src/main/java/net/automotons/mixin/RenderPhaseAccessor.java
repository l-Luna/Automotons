package net.automotons.mixin;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor{
	@Accessor
	static RenderPhase.Transparency getTRANSLUCENT_TRANSPARENCY(){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static RenderPhase.Texture getNO_TEXTURE(){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static RenderPhase.Lightmap getENABLE_LIGHTMAP(){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static RenderPhase.Cull getDISABLE_CULLING(){
		throw new UnsupportedOperationException();
	}
	
	@Accessor
	static RenderPhase.WriteMaskState getCOLOR_MASK(){
		throw new UnsupportedOperationException();
	}
}
