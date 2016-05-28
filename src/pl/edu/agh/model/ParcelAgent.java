package pl.edu.agh.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.algorithm.Angle;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;

public class ParcelAgent {
	private Geography<Object> geography;
	private ParcelMachineAgent senderParcelMachine;
	private ParcelMachineAgent receiverParcelMachine;
	private Coordinate currentPosition;
	private Object target;
	private ParcelStatus status = ParcelStatus.CREATED;
	private List<AgencyAgent> agencies;
	private List<SortingCenterAgent> sortingCenters;
	private int id;
	
	/** if target was reached:
	 * 1 - closest agentAgency to sender
	 * 2 - sorting center
	 * 3 - closest agentAgency to receiver
	 * 4 - in receiver parcel machine
	 * 5 - in addressee hands
	 */
	private int reached = 0;		
	private double tickCount = 0;
	private boolean isMoving = true;		// if parcel should move or not
	private AgencyAgent closestAgency;
	
	private static final int PARCEL_MACHINE_CAPACITY = 1;
	private static final int TIME_TO_WAIT_IN_TICKS = 50000;
	
	public ParcelAgent(Geography<Object> geography, ParcelMachineAgent senderMachine, ParcelMachineAgent receiverMachine,
			List<SortingCenterAgent> sortingCenters,
			List<AgencyAgent> agencies, int id) {
		this.geography = geography;
		this.senderParcelMachine = senderMachine;
		this.receiverParcelMachine = receiverMachine;
		this.currentPosition = geography.getGeometry(senderMachine)
				.getCoordinate();
		this.agencies = agencies;
		this.sortingCenters = sortingCenters;
		if (senderMachine != receiverMachine) {
			this.target = senderMachine;
		}
		this.id = id;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void move() {
		if (reached > 0) {
			tickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			switch(reached) {
			case 1:
				System.out.println("Parcel nr " + this.id + " is in sender agent agency");
				break;
			case 2:
				System.out.println("Parcel nr " + this.id + " is in sorting center");
				break;
			case 3:
				System.out.println("Parcel nr " + this.id + " is in receiver agent agency");
				break;
			case 4:
				System.out.println("Parcel nr " + this.id + " delivered to " + receiverParcelMachine.getName());
				break;
			case 5:
				System.out.println("Parcel nr " + this.id + " is picked up by addressee");
				break;
			}
			reached = 0;
		} 
		if (target == null) {
			return;
		}
		
		double bearing = Angle.normalizePositive(bearing(currentPosition,
				geography.getGeometry(target).getCoordinate()));
		
		if (RunEnvironment.getInstance().getCurrentSchedule().getTickCount()
				< tickCount + TIME_TO_WAIT_IN_TICKS && tickCount != 0) {
			isMoving = false;
		} else {
			isMoving = true;
		}
		
		if(isMoving) {
			currentPosition = geography.moveByVector(this, 3, bearing)
				.getCoordinate();	
		}
			
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void checkStatus() {
		if (target == null
				|| !isCloseEnoughTo(geography.getGeometry(target)
						.getCoordinate())) {
			return;
		}
		switch (status) {
			case CREATED:
				// where parcel supposed to go
				target = senderParcelMachine;
				//senderParcelMachine.setParcelCounter(senderParcelMachine.getParcelCounter() + 1);
				System.out.println("Parcel nr " + this.id + " created in " + senderParcelMachine.getName()
						+ ". It goes to " + receiverParcelMachine.getName());
				break;
			case TO_SENDER_AGENCY:
				// firsty send to closest from current position agency
				target = getClosestAgency(agencies);
				break;
			case TO_SORTING_CENTER:
				// then send to closest sorting center
				target = getClosestSortingCenter(sortingCenters);
				reached = 1;
				break;
			case TO_RECEIVER_AGENCY:
				// then send to closest agency from target parcel machine
				target = getClosestAgencyToParcelMachine(agencies);
				reached = 2;
				break;
			case TO_RECEIVER_MACHINE:
				// then send to target parcel machine
				target = receiverParcelMachine;
				reached = 3;
				break;
			case DELIVERED:
				//target = null;
				
				receiverParcelMachine.setParcelCounter(receiverParcelMachine.getParcelCounter() + 1);
				if(receiverParcelMachine.getParcelCounter() > PARCEL_MACHINE_CAPACITY) {
					target = closestAgency;
					status = ParcelStatus.TO_RECEIVER_MACHINE;
				} else {
					reached = 4;
					
				}
				break;
				//return;
			case RECEIVED:
				// tutaj dodac jakas pauze zeby paczka troche pobyla w paczkomacie przed jej odbiorem
				// wtedy bedzie widac przepelnianie paczkomatow
				target = null;
				reached = 5;
				receiverParcelMachine.setParcelCounter(receiverParcelMachine.getParcelCounter() - 1);
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
	
	private Object getClosestAgency(List<AgencyAgent> targets) {
		Map<AgencyAgent, Coordinate> positionsMap = new HashMap<AgencyAgent, Coordinate>();
		for(AgencyAgent agent : targets) {
			positionsMap.put(agent, geography.getGeometry(agent).getCoordinate());
		}
		
		AgencyAgent currentTarget = null;
		double tempDist = 0.0;
		for(Map.Entry<AgencyAgent, Coordinate> entry : positionsMap.entrySet()) {
			double dist = entry.getValue().distance(currentPosition);
			
			if(currentTarget == null || dist < tempDist) {
				currentTarget = entry.getKey();
				tempDist = dist;
			}
		}
		return currentTarget;
	}
	
	private Object getClosestSortingCenter(List<SortingCenterAgent> targets) {
		Map<SortingCenterAgent, Coordinate> positionsMap = new HashMap<SortingCenterAgent, Coordinate>();
		for(SortingCenterAgent agent : targets) {
			positionsMap.put(agent, geography.getGeometry(agent).getCoordinate());
		}
		
		SortingCenterAgent currentTarget = null;
		double tempDist = 0.0;
		for(Map.Entry<SortingCenterAgent, Coordinate> entry : positionsMap.entrySet()) {
			double dist = entry.getValue().distance(geography.getGeometry(target).getCoordinate()); //targetem jest agencja wczesniej wybrana
			if(currentTarget == null || dist < tempDist) {
				currentTarget = entry.getKey();
				tempDist = dist;
			}
		}
		return currentTarget;
	}
	
	private Object getClosestAgencyToParcelMachine(List<AgencyAgent> targets) {
		Map<AgencyAgent, Coordinate> positionsMap = new HashMap<AgencyAgent, Coordinate>();
		for(AgencyAgent agent : targets) {
			positionsMap.put(agent, geography.getGeometry(agent).getCoordinate());
		}
		AgencyAgent currentTarget = null;
		double tempDist = 0.0;
		for(Map.Entry<AgencyAgent, Coordinate> entry : positionsMap.entrySet()) {
			double dist = entry.getValue().distance(geography.getGeometry(receiverParcelMachine).getCoordinate());
			
			if(currentTarget == null || dist < tempDist) {
				currentTarget = entry.getKey();
				tempDist = dist;
			}
		}
		closestAgency = currentTarget;
		return currentTarget;
	}
}