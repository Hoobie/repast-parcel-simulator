package pl.edu.agh;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import pl.edu.agh.model.BoxMachine;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

public class SimulationContextBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("simulation");
		
		GeographyParameters<Object> params = new GeographyParameters<Object>();
		//params.setAdder(new RandomGisAdder<Object>());
		GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);
		Geography<Object> geography = factory.createGeography("geography", context, params);
		
		for (int i = 0; i < 10; i++) {
			BoxMachine bm = new BoxMachine(geography);
			context.add(bm);
			geography.move(bm, new GeometryFactory().createPoint(new Coordinate(i, i + 1)));
		}
		
		return context;
	}

}
