/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This class contains constants representing control commands for a UPS system.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/24/2023
 * @since 1.0.0
 */
public class UPSControlCommand {
	public static final String TURN_ON_COMMAND = "~00S006RON$;2";
	public static final String TURN_OFF_COMMAND = "~00S006ROF$;2";
	public static final String OUTLET_CYCLE_COMMAND ="~00S009RSC$;5;10";
	public static final String REPLACEMENT_DATE_COMMAND = "~00S020BRD$1;$2";
}
