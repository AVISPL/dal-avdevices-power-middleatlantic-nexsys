/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This class contains constants representing monitoring commands for a UPS system.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/24/2023
 * @since 1.0.0
 */
public enum UPSMonitoringCommand {
	INPUT_STATE("InputState", "~00P003STI"),
	OUTPUT_STATE("OutputState", "~00P003STO"),
	BATTERY_STATE("BatteryState", "~00P003STB"),
	FIRMWARE("FirmwareVersion", "~00P003VER"),
	NUM_OF_OUTLETS("NumberOfOutlets", "~00P003LET"),
	ALL_OUTLETS("AllOutlets", "~00P003OL8"),
	REPLACEMENT_DATE("ReplacementDate", "~00P012BRD"),
	SERIAL("SerialNumber", "~00P003SER"),
	SELF_TEST_RESULTS("LastSelfTestResults", "~00P003TSR"),
	;
	private final String name;
	private final String command;

	/**
	 * Creates a new UPSMonitoringCommand with the specified name and command.
	 *
	 * @param name The name of the monitoring command.
	 * @param command The actual command string to be sent to the UPS.
	 */
	UPSMonitoringCommand(String name, String command) {
		this.name = name;
		this.command = command;
	}

	/**
	 * Retrieves {@link #command}
	 *
	 * @return value of {@link #command}
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
