package pl.edu.agh.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.algorithm.Angle;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.Network;

public class ParcelAgent {
	private Geography<Object> geography;
	private Network<Object> network;
	private ParcelMachineAgent senderParcelMachine;
	private ParcelMachineAgent receiverParcelMachine;
	private Coordinate currentPosition;
	private Object target;
	private ParcelStatus status = ParcelStatus.CREATED;

	public ParcelAgent(Geography<Object> geography, Network<Object> network,
			ParcelMachineAgent senderMachine, ParcelMachineAgent receiverMachine) {
		this.geography = geography;
		this.network = network;
		this.senderParcelMachine = senderMachine;
		this.receiverParcelMachine = receiverMachine;
		this.currentPosition = geography.getGeometry(senderMachine)
				.getCoordinate();
		if (senderMachine != receiverMachine) {
			this.target = senderMachine;
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void move() {
		if (target == null) {
			return;
		}
		double bearing = Angle.normalizePositive(bearing(currentPosition,
				geography.getGeometry(target).getCoordinate()));
		currentPosition = geography.moveByVector(this, 0.05, bearing)
				.getCoordinate();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void checkStatus() {
		if (target == null
				|| !isCloseEnoughTo(geography.getGeometry(target)
						.getCoordinate())) {
			return;
		}
		switch (status) {
		// TODO: use closest instead of random???
		case CREATED:
			target = network.getRandomPredecessor(senderParcelMachine);
			break;
		case TO_SENDER_AGENCY:
			target = network.getRandomPredecessor(target);
			break;
		case TO_SORTING_CENTER:
			target = network.getRandomPredecessor(receiverParcelMachine);
			break;
		case TO_RECEIVER_AGENCY:
			target = receiverParcelMachine;
			break;
		case TO_RECEIVER_MACHINE:
			break;
		case DELIVERED:
			target = null;
			return;
		}
		status = status.getNextStatus();
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

	private boolean isCloseEnoughTo(Coordinate coordinate) {
		return coordinate.distance(currentPosition) < 0.0005;
	}
}
