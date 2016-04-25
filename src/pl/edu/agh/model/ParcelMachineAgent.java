package pl.edu.agh.model;

public class ParcelMachineAgent {
	private String name;
	private int id;
	private int parcelCounter = 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
