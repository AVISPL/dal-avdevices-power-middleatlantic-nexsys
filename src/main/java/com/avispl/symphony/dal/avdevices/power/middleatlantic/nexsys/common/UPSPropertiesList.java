/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * This enum represents a list of properties and metrics related to a UPS system.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/24/2023
 * @since 1.0.0
 */
public enum UPSPropertiesList {
	INPUT_PHASE_COUNT("PhaseCount", UPSConstant.INPUT_STATUS_GROUP, 0),
	INPUT_FREQUENCY("Frequency(Hz)", UPSConstant.INPUT_STATUS_GROUP, 1),
	INPUT_VOLTAGE("Voltage(V)", UPSConstant.INPUT_STATUS_GROUP, 2),
	INPUT_CURRENT("Current(A)", UPSConstant.INPUT_STATUS_GROUP, 3),

	SOURCE("Source", UPSConstant.OUTPUT_STATUS_GROUP, 0),
	OUTPUT_FREQUENCY("Frequency(Hz)", UPSConstant.OUTPUT_STATUS_GROUP, 1),
	OUTPUT_PHASE_COUNT("PhaseCount", UPSConstant.OUTPUT_STATUS_GROUP, 2),
	OUTPUT_VOLTAGE("Voltage(V)", UPSConstant.OUTPUT_STATUS_GROUP, 3),
	OUTPUT_CURRENT("Current(A)", UPSConstant.OUTPUT_STATUS_GROUP, 4),
	OUTPUT_POWER("Power(W)", UPSConstant.OUTPUT_STATUS_GROUP, 5),
	OUTPUT_LOAD("Load(%)", UPSConstant.OUTPUT_STATUS_GROUP, 6),

	LAST_SELF_TEST_RESULTS("LastSelfTestResults", UPSConstant.BATTERY_STATUS_GROUP, -1),
	LAST_REPLACEMENT_DATE("LastReplacementDate(MM/DD/YYYY)", UPSConstant.BATTERY_STATUS_GROUP, -1),
	NEXT_REPLACEMENT_DATE("NextReplacementDate(MM/DD/YYYY)", UPSConstant.BATTERY_STATUS_GROUP, -1),
	CONDITION("Condition", UPSConstant.BATTERY_STATUS_GROUP, 0),
	STATUS("Status", UPSConstant.BATTERY_STATUS_GROUP, 1),
	CHARGE("Charge", UPSConstant.BATTERY_STATUS_GROUP, 2),
	ON_BATTERY_TIME("OnBatteryTime(second)", UPSConstant.BATTERY_STATUS_GROUP, 3),
	REMAINING_TIME("RemainingTime", UPSConstant.BATTERY_STATUS_GROUP, 4),
	BATTERY_VOLTAGE("Voltage(V)", UPSConstant.BATTERY_STATUS_GROUP, 6),
	TEMPERATURE("Temperature(C)", UPSConstant.BATTERY_STATUS_GROUP, 8),
	CAPACITY("Capacity(%)", UPSConstant.BATTERY_STATUS_GROUP, 9),
	NUMBER_EXTERNAL_BATTERY_PACK("ExternalBatteryPackCount", UPSConstant.BATTERY_STATUS_GROUP, 10),

	NUMBER_OF_OUTLETS("NumberOfOutlets", UPSConstant.EMPTY, -1),
	FIRMWARE_VERSION("FirmwareVersion", UPSConstant.EMPTY, -1),
	SERIAL("SerialNumber", UPSConstant.EMPTY, -1),
	OUTLET_STATUS_1("Outlet1", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_2("Outlet2", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_3("Outlet3", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_4("Outlet4", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_5("Outlet5", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_6("Outlet6", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_7("Outlet7", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_STATUS_8("Outlet8", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_1("CycleOutlet1", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_2("CycleOutlet2", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_3("CycleOutlet3", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_4("CycleOutlet4", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_5("CycleOutlet5", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_6("CycleOutlet6", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_7("CycleOutlet7", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	OUTLET_CYCLE_8("CycleOutlet8", UPSConstant.OUTLET_CONTROL_GROUP, -1),
	;
	private final String name;
	private final String group;
	private final int bitIndex;

	/**
	 * Creates a new UPSPropertiesList with the specified name, group, and bit index.
	 *
	 * @param name The display name of the metric.
	 * @param group The corresponding value of the metric.
	 * @param bitIndex The bit index associated with the metric.
	 */
	UPSPropertiesList(String name, String group, int bitIndex) {
		this.name = name;
		this.group = group;
		this.bitIndex = bitIndex;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Retrieves {@link #bitIndex}
	 *
	 * @return value of {@link #bitIndex}
	 */
	public int getBitIndex() {
		return bitIndex;
	}

	/**
	 * This method is used to get properties metric group by name
	 *
	 * @param name is the name of device metric group that want to get
	 * @return UPSPropertiesList is the device metric group that want to get
	 */
	public static UPSPropertiesList getByName(String name) {
		Optional<UPSPropertiesList> property = Arrays.stream(UPSPropertiesList.values()).filter(group -> group.getName().equals(name)).findFirst();
		if (property.isPresent()) {
			return property.get();
		} else {
			throw new IllegalStateException(String.format("control group %s is not supported.", name));
		}
	}
}
