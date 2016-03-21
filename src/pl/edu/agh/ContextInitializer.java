package pl.edu.agh;

import java.io.File;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import pl.edu.agh.model.BoxMachine;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.ShapefileLoader;

public class ContextInitializer implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("parcel-simulator");

		GeographyParameters<Object> params = new GeographyParameters<Object>();
		GeographyFactory factory = GeographyFactoryFinder
				.createGeographyFactory(null);
		Geography<Object> geography = factory.createGeography("geography",
				context, params);

		File shapefile = null;
		ShapefileLoader<BoxMachine> loader = null;
		try {
			shapefile = new File("misc/shp/boxmachines/boxmachines.shp");
			loader = new ShapefileLoader<BoxMachine>(BoxMachine.class,
					shapefile.toURL(), geography, context);
		} catch (java.net.MalformedURLException e) {
			e.printStackTrace();
		}
		while (loader.hasNext()) {
			loader.next();
		}

		return context;
	}

}
