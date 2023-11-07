/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

/**
 * This enum represents the results of self-tests performed in a system.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public enum SelfTestResultEnum {
	NO_TEST_PERFORMED("No test performed", "0"),
	TEST_PASSED("Test passed", "1"),
	TEST_IN_PROGRESS("Test in progress", "2"),
	GENERAL_TEST_FAILED("General test failed (Obsolete)", "3"),
	BATTERY_TEST_FAILED("Battery test failed", "4"),
	DEEP_BATTERY("Deep battery test failed (Obsolete)", "5"),
	TEST_ABORTED("Test Aborted", "6"),
	;
	private final String name;
	private final String value;

	/**
	 * Creates a new SelfTestResultEnum with the specified name and value.
	 *
	 * @param name  The name of the self-test result.
	 * @param value The corresponding value of the self-test result.
	 */
	SelfTestResultEnum(String name, String value) {
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
