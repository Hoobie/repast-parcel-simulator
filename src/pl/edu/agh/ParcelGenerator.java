package pl.edu.agh;

import java.util.List;

import pl.edu.agh.model.AgencyAgent;
import pl.edu.agh.model.ParcelAgent;
import pl.edu.agh.model.ParcelMachineAgent;
import pl.edu.agh.model.SortingCenterAgent;
import pl.edu.agh.util.CollectionUtil;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;

public class ParcelGenerator {

	private Context<Object> context;
	private Geography<Object> geography;
	private List<ParcelMachineAgent> parcelMachines;
	private List<SortingCenterAgent> sortingCenters;
	private List<AgencyAgent> agencies;
	private int interval;

	public ParcelGenerator(Context<Object> context,
			Geography<Object> geography,
			List<ParcelMachineAgent> parcelMachines,
			List<SortingCenterAgent> sortingCenters,
			List<AgencyAgent> agencies, int interval) {
		this.context = context;
		this.geography = geography;
		this.parcelMachines = parcelMachines;
		this.sortingCenters = sortingCenters;
		this.agencies = agencies;
		this.interval = interval;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void generate() {
		if (RunEnvironment.getInstance().getCurrentSchedule().getTickCount()
				% interval != 0) {
			return;
		}
		ParcelMachineAgent senderMachine = CollectionUtil
				.getRandomElement(parcelMachines);
		ParcelMachineAgent receiverMachine = CollectionUtil
				.getRandomElement(parcelMachines);
		while (senderMachine == receiverMachine)
			receiverMachine = CollectionUtil.getRandomElement(parcelMachines);

		ParcelAgent agent = new ParcelAgent(geography, senderMachine,
				receiverMachine, sortingCenters, agencies, System.nanoTime());
		context.add(agent);

		geography.move(agent, geography.getGeometry(senderMachine));
	}
}
