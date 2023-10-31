/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This enum represents the battery charge status.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public enum BatteryChargeEnum {
	OBSOLETE("(obsolete)", "0"),
	CHARGING("Charging", "1"),
	RESTING("Resting", "2"),
	DISCHARGING("Discharging", "3"),
	;
	private final String name;
	private final String value;

	/**
	 * Creates a new BatteryChargeEnum with the specified name and value.
	 *
	 * @param name  The name of the battery charge status.
	 * @param value The corresponding value of the battery charge status.
	 */
	BatteryChargeEnum(String name, String value) {
		this.name = name;
		this.value = value;
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
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
