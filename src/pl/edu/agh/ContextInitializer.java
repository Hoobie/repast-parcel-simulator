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
import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.ShapefileLoader;
import repast.simphony.space.graph.Network;

public class ContextInitializer implements ContextBuilder<Object> {

	private static final double KRAKOW_LATITUDE = 50.03;
	private static final double KRAKOW_LONGITUDE = 19.90;

	private List<ParcelMachineAgent> parcelMachines = new ArrayList<ParcelMachineAgent>();

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
				"network", context, true);

		Network<Object> net = netBuilder.buildNetwork();

		Parameters parm = RunEnvironment.getInstance().getParameters();
		int parcelNumber = (Integer) parm.getValue("parcelNumber");
		int sortingCenterNumber = (Integer) parm
				.getValue("sortingCenterNumber");
		int parcelMachinesNumber = (Integer) parm
				.getValue("parcelMachineNumber");
		int agencyNumber = (Integer) parm.getValue("agencyNumber");

		// Generate sorting center agents
		List<SortingCenterAgent> sortingCenters = new ArrayList<SortingCenterAgent>();

		for (int i = 0; i < sortingCenterNumber; i++) {
			SortingCenterAgent sortingCenter = new SortingCenterAgent();
			context.add(sortingCenter);

			Coordinate coord = new Coordinate(KRAKOW_LONGITUDE + 0.01
					* Math.random(), KRAKOW_LATITUDE + 0.01 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(sortingCenter, geom);

			sortingCenters.add(sortingCenter);
		}

		// Generate agencies
		List<AgencyAgent> agencies = new ArrayList<AgencyAgent>();

		for (int i = 0; i < agencyNumber; i++) {
			AgencyAgent agency = new AgencyAgent();
			context.add(agency);

			Coordinate coord = new Coordinate(KRAKOW_LONGITUDE + 0.02
					* Math.random(), KRAKOW_LATITUDE + 0.02 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(agency, geom);

			for (SortingCenterAgent sortingCenter : sortingCenters) {
				net.addEdge(sortingCenter, agency,
						geography.getGeometry(sortingCenter).distance(geom));
			}
			agencies.add(agency);
		}

		// Generate parcel machines
		for (int i = 0; i < parcelMachinesNumber; i++) {
			ParcelMachineAgent parcelMachine = new ParcelMachineAgent();
			context.add(parcelMachine);

			Coordinate coord = new Coordinate(KRAKOW_LONGITUDE + 0.03
					* Math.random(), KRAKOW_LATITUDE + 0.03 * Math.random());
			Point geom = fac.createPoint(coord);
			geography.move(parcelMachine, geom);

			for (AgencyAgent agency : agencies) {
				net.addEdge(agency, parcelMachine, geography
						.getGeometry(agency).distance(geom));
			}
			parcelMachines.add(parcelMachine);
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
			for (AgencyAgent agency : agencies) {
				// TODO: refactor?
				net.addEdge(
						agency,
						parcelMachine,
						geography.getGeometry(agency).distance(
								geography.getGeometry(parcelMachine)));
			}
			parcelMachines.add(parcelMachine);
		}

		// Generate parcel agents
		for (int i = 0; i < parcelNumber; i++) {
			ParcelMachineAgent senderMachine = getRandomParcelMachine();
			ParcelMachineAgent receiverMachine = getRandomParcelMachine();

			ParcelAgent agent = new ParcelAgent(geography, net, senderMachine,
					receiverMachine);
			context.add(agent);

			geography.move(agent, geography.getGeometry(senderMachine));
		}

		System.out.println("Number of edges: " + net.numEdges());
		System.out.println("Number of nodes: " + net.size());

		return context;
	}

	private ParcelMachineAgent getRandomParcelMachine() {
		int parcelMachineIndex = RandomHelper.nextIntFromTo(0,
				parcelMachines.size() - 1);
		return parcelMachines.get(parcelMachineIndex);
	}
}
