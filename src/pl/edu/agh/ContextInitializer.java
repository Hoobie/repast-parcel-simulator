package pl.edu.agh;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import pl.edu.agh.model.AgencyAgent;
import pl.edu.agh.model.ParcelAgent;
import pl.edu.agh.model.ParcelMachineAgent;
import pl.edu.agh.model.SortingCenterAgent;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.ShapefileLoader;
import repast.simphony.space.graph.Network;

public class ContextInitializer implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("parcel-simulator");

		GeographyParameters<Object> params = new GeographyParameters<Object>();
		GeographyFactory factory = GeographyFactoryFinder
				.createGeographyFactory(null);
		Geography<Object> geography = factory.createGeography("geography",
				context, params);
		GeometryFactory fac = new GeometryFactory();

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"network", context, false);

		Network<Object> net = netBuilder.buildNetwork();

		Parameters parm = RunEnvironment.getInstance().getParameters();
		int parcelNumber = (Integer) parm.getValue("parcelNumber");
		int sortingCenterNumber = (Integer) parm.getValue("sortingCenterNumber");
		int parcelMachinesNumber = (Integer) parm.getValue("parcelMachineNumber");
		int agencyNumber = (Integer) parm.getValue("agencyNumber");

		// Generate parcel agents
		for (int i = 0; i < parcelNumber; i++) {
			ParcelAgent agent = new ParcelAgent(geography, i);
			context.add(agent);

			Coordinate coord = new Coordinate(19.90 + 0.05 * Math.random(),
					50.03 + 0.05 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(agent, geom);
		}

		List<Object> networkObjects = new ArrayList<Object>();

		// Generate sorting center agents
		for (int i = 0; i < sortingCenterNumber; i++) {
			SortingCenterAgent agent = new SortingCenterAgent();
			context.add(agent);

			Coordinate coord = new Coordinate(19.90 + 0.2 * Math.random(),
					50.03 + 0.2 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(agent, geom);

			for (int j = 0; j < networkObjects.size(); j++) {
				net.addEdge(networkObjects.get(j), agent);
			}
			networkObjects.add(agent);
		}

		// Generate parcel machines
		for (int i = 0; i < parcelMachinesNumber; i++) {
			ParcelMachineAgent agent = new ParcelMachineAgent();
			context.add(agent);

			Coordinate coord = new Coordinate(19.90 + 0.01 * Math.random(),
					50.03 + 0.01 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(agent, geom);
			for (int j = 0; j < networkObjects.size(); j++) {
				net.addEdge(networkObjects.get(j), agent);
			}
			networkObjects.add(agent);
		}

		// Add parcel machines from shapefiles
		File shapefile = null;
		ShapefileLoader<ParcelMachineAgent> loader = null;
		try {
			shapefile = new File("misc/shp/parcel_machines/parcel_machines.shp");
			loader = new ShapefileLoader<ParcelMachineAgent>(
					ParcelMachineAgent.class, shapefile.toURL(), geography,
					context);
		} catch (java.net.MalformedURLException e) {
			e.printStackTrace();
		}
		while (loader.hasNext()) {
			ParcelMachineAgent parcelMachine = loader.next();
			networkObjects.add(parcelMachine);
		}

		// Generate agencies
		for (int i = 0; i < agencyNumber; i++) {
			AgencyAgent agent = new AgencyAgent();
			context.add(agent);

			Coordinate coord = new Coordinate(19.90 + 0.03 * Math.random(),
					50.03 + 0.03 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(agent, geom);
			for (int j = 0; j < networkObjects.size(); j++) {
				net.addEdge(networkObjects.get(j), agent);
			}
			networkObjects.add(agent);
		}

		System.out.println("Number of edges: " + net.numEdges());
		System.out.println("Number of nodes: " + net.size());

		return context;
	}
}
