package pl.edu.agh.util;

import java.util.List;

import repast.simphony.random.RandomHelper;

public class CollectionUtil {

	public static <T> T getRandomElement(List<T> agents) {
		int parcelMachineIndex = RandomHelper.nextIntFromTo(0,
				agents.size() - 1);
		return agents.get(parcelMachineIndex);
	}
}
