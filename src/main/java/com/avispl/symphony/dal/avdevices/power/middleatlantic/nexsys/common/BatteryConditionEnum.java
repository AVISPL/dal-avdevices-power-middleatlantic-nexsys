/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This enum represents the condition of a battery.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public enum BatteryConditionEnum {
	GOOD("Good", "0"),
	WEAK("Weak", "1"),
	REPLACE("Replace", "2"),
	;
	private final String name;
	private final String value;

	/**
	 * Creates a new BatteryConditionEnum with the specified name and value.
	 *
	 * @param name  The name of the battery condition.
	 * @param value The corresponding value of the battery condition.
	 */
	BatteryConditionEnum(String name, String value) {
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
