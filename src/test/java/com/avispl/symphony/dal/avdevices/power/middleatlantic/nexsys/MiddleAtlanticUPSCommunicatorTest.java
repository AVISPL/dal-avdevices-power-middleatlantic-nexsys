/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class MiddleAtlanticUPSCommunicatorTest {
	private MiddleAtlanticUPSCommunicator middleAtlanticUPSCommunicator;

	@BeforeEach()
	public void setUp() throws Exception {
		middleAtlanticUPSCommunicator = new MiddleAtlanticUPSCommunicator();
		//  ToDo: comment out controlling capabilities and config management
		middleAtlanticUPSCommunicator.setHost("");
		middleAtlanticUPSCommunicator.setPort(22);
		middleAtlanticUPSCommunicator.setLogin("");
		middleAtlanticUPSCommunicator.setPassword("");
		middleAtlanticUPSCommunicator.init();
		middleAtlanticUPSCommunicator.connect();
	}

	@AfterEach()
	public void destroy() throws Exception {
		middleAtlanticUPSCommunicator.disconnect();
	}
}
