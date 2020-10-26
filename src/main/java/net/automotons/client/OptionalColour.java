package net.automotons.client;

public class OptionalColour{
	
	private boolean present = false;
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	
	public void setColour(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
		present = true;
	}
	
	public void empty(){
		present = false;
		red = green = blue = 0;
	}
	
	public void ifPresent(ColourConsumer consumer){
		if(present)
			consumer.accept(red, green, blue);
	}
	
	public boolean isPresent(){
		return present;
	}
	
	@FunctionalInterface
	public interface ColourConsumer{
		void accept(int red, int green, int blue);
	}
}