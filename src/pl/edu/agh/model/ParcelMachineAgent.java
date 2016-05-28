package pl.edu.agh.model;

public class ParcelMachineAgent {
	private String name;
	private int parcelCounter;
	
	public ParcelMachineAgent() {
		this("");
	}
	
	public ParcelMachineAgent(String name) {
		this.name = name;
		this.parcelCounter = 0;
	}
	
	public int getParcelCounter() {
		return parcelCounter;
	}

	public void setParcelCounter(int parcelCounter) {
		this.parcelCounter = parcelCounter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
