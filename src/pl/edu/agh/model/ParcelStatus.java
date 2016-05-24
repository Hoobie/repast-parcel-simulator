package pl.edu.agh.model;

import java.util.Arrays;

public enum ParcelStatus {
	CREATED,
	TO_SENDER_AGENCY,
	TO_SORTING_CENTER,
	TO_RECEIVER_AGENCY,
	TO_RECEIVER_MACHINE,
	DELIVERED,
	RECEIVED;
	
	
	public ParcelStatus getNextStatus() {
		ParcelStatus[] values = ParcelStatus.values();
		int index = Arrays.asList(values).indexOf(this);
		if (index > values.length - 2) {
			throw new RuntimeException("There is no further status.");
		}
		return values[index + 1];
	}
}
