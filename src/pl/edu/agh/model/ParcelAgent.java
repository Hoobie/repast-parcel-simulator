package pl.edu.agh.model;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.Network;

public class ParcelAgent {
	private String name;
	private int id;
	private Geography<Object> geography;
	
	public ParcelAgent(Geography<Object> geography, int id) {
		this.geography = geography;
		this.id = id;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void step() { 	
		this.geography.moveByDisplacement(this, RandomHelper.nextDoubleFromTo(-0.0005, 0.0005), 
				RandomHelper.nextDoubleFromTo(-0.0005, 0.0005));
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
