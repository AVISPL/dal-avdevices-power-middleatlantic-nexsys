/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This enum represents the source of the output in a power system.
 *
 * @author Harru / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public enum OutputSourceEnum {
	NORMAL("Normal", "0"),
	BATTERY("Battery", "1"),
	BYPASS("Bypass-3phase Reserve Pwr Path", "2"),
	REDUCING("Reducing", "3"),
	BOOSTING("Boosting", "4"),
	MANUAL_BYPASS("Manual Bypass", "5"),
	OTHER("Other", "6"),
	NO_OUTPUT("No output", "7"),
	ON_ECO("On ECO", "8"),
	LOAD_TRANSFER_BREAK("Load Transfer Break", "9"),
	;
	private final String name;
	private final String value;

	/**
	 * Creates a new OutputSourceEnum with the specified name and value.
	 *
	 * @param name  The name of the output source.
	 * @param value The corresponding value of the output source.
	 */
	OutputSourceEnum(String name, String value) {
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
