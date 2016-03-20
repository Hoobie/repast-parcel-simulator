package pl.edu.agh;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.GISAdder;
import repast.simphony.space.gis.Geography;
import simphony.util.messages.MessageCenter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class RandomGisAdder<T> implements GISAdder<T> {

	private WKTReader reader;

	public RandomGisAdder() {
		reader = new WKTReader();
	}

	public void add(Geography<T> destination, T object) {
		double x = RandomHelper.getUniform().nextDoubleFromTo(0, 10);
		double y = RandomHelper.getUniform().nextDoubleFromTo(0, 10);
		Geometry geom;
		try {
			geom = reader.read("POINT(" + x + " " + y + ")");
			destination.move(object, geom);
		} catch (ParseException e) {
			MessageCenter.getMessageCenter(getClass()).warn(
					"Unable to create Geometry from wkt", e);
		}
	}
}
