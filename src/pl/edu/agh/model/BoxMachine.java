package pl.edu.agh.model;

import repast.simphony.space.gis.Geography;

public class BoxMachine {

	private Geography<Object> geography;
	
	public BoxMachine() {
	}

	public Geography<Object> getGeography() {
		return geography;
	}

	public void setGeography(Geography<Object> geography) {
		this.geography = geography;
	}
}
