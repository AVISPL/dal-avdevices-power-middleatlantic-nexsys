/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This enum represents the status of a battery.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public enum BatteryStatusEnum {
	OK("OK", "0"),
	LOW("Low", "1"),
	DEPLETED("Depleted", "2"),
	;
	private final String name;
	private final String value;

	/**
	 * Creates a new BatteryStatusEnum with the specified name and value.
	 *
	 * @param name  The name of the battery status.
	 * @param value The corresponding value of the battery status.
	 */
	BatteryStatusEnum(String name, String value) {
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
