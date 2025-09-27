package automotons.items.heads;

import automotons.blocks.AutomotonBlockEntity;
import automotons.items.HeadItem;

public class BroadcastAntennaeHead extends HeadItem<Object>{
	
	public BroadcastAntennaeHead(Settings settings){
		super(settings);
	}
	
	public boolean canGenerateBroadcast(AutomotonBlockEntity automoton, Object o){
		return true;
	}
}