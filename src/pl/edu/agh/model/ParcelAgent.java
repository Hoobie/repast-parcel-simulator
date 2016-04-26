package pl.edu.agh.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.algorithm.Angle;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;

public class ParcelAgent {
	private Geography<Object> geography;
	private ParcelMachineAgent senderParcelMachine;
	private ParcelMachineAgent receiverParcelMachine;
	private Coordinate currentPosition;

	public ParcelAgent(Geography<Object> geography,
			ParcelMachineAgent senderMachine, ParcelMachineAgent receiverMachine) {
		this.geography = geography;
		this.senderParcelMachine = senderMachine;
		this.receiverParcelMachine = receiverMachine;
		this.currentPosition = geography.getGeometry(senderMachine).getCoordinate();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void step() {
		Coordinate targetPosition = geography.getGeometry(receiverParcelMachine)
				.getCoordinate();
		if (senderParcelMachine == receiverParcelMachine
				|| isCloseEnoughTo(targetPosition)) {
			return;
		}
		double bearing = Angle.normalizePositive(bearing(currentPosition,
				targetPosition));
		currentPosition = geography.moveByVector(this, 0.1, bearing)
				.getCoordinate();
	}

	private boolean isCloseEnoughTo(Coordinate coordinate) {
		return coordinate.distance(currentPosition) < 0.001;
	}

	private double bearing(Coordinate c1, Coordinate c2) {
		double longitude1 = c1.x;
		double longitude2 = c2.x;
		double latitude1 = Math.toRadians(c1.y);
		double latitude2 = Math.toRadians(c2.y);
		double longDiff = Math.toRadians(longitude2 - longitude1);
		double y = Math.sin(longDiff) * Math.cos(latitude2);
		double x = Math.cos(latitude1) * Math.sin(latitude2)
				- Math.sin(latitude1) * Math.cos(latitude2)
				* Math.cos(longDiff);
		double angle = Math.atan2(y, x);
		// magic transform
		return -angle + (Math.PI / 2);
	}
}
