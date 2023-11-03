/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys;

import static com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSPropertiesList.NEXT_REPLACEMENT_DATE;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSConstant;

/**
 * MiddleAtlanticUPSCommunicatorTest for unit test of MiddleAtlanticUPSCommunicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 30/10/2023
 * @since 1.0.0
 */
public class MiddleAtlanticUPSCommunicatorTest {
	private MiddleAtlanticUPSCommunicator middleAtlanticUPSCommunicator;
	static ExtendedStatistics extendedStatistic;

	@BeforeEach()
	public void setUp() throws Exception {
		middleAtlanticUPSCommunicator = new MiddleAtlanticUPSCommunicator();
		middleAtlanticUPSCommunicator.setHost("");
		middleAtlanticUPSCommunicator.setPort(22);
		middleAtlanticUPSCommunicator.setLogin("");
		middleAtlanticUPSCommunicator.setPassword("");
		middleAtlanticUPSCommunicator.init();
		middleAtlanticUPSCommunicator.connect();
		middleAtlanticUPSCommunicator.setConfigManagement("true");
	}

	@AfterEach()
	public void destroy() throws Exception {
		middleAtlanticUPSCommunicator.internalDestroy();
		middleAtlanticUPSCommunicator.disconnect();
	}

	/**
	 * Unit test to verify the functionality of the "getMultipleStatistics" method in the MiddleAtlanticUPSCommunicator class.
	 * This test ensures that the method correctly retrieves multiple statistics and verifies the expected size of the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatistics() throws Exception {
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(43, statistics.size());
		Assert.assertEquals(18, advancedControllablePropertyList.size());
	}

	/**
	 * This test ensures that the method correctly retrieves overall statistics and validates specific values from the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatisticsWithOverallInfo() throws Exception {
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals("S02E03", statistics.get("FirmwareVersion"));
		Assert.assertEquals("8", statistics.get("NumberOfOutlets"));
		Assert.assertEquals("F0LW2A6002U", statistics.get("SerialNumber"));
	}

	/**
	 * This test ensures that the method correctly retrieves battery status statistics and validates specific values from the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatisticsWithBatteryStatus() throws Exception {
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals("Charging", statistics.get("BatteryStatus#Charge"));
		Assert.assertEquals("Good", statistics.get("BatteryStatus#Condition"));
		Assert.assertEquals("01/01/2022", statistics.get("BatteryStatus#LastReplacementDate(MM/DD/YYYY)"));
		Assert.assertEquals("01/05/2024", statistics.get("BatteryStatus#NextReplacementDate(MM/DD/YYYY)"));
		Assert.assertEquals("Test passed", statistics.get("BatteryStatus#LastSelfTestResults"));
		Assert.assertEquals("OK", statistics.get("BatteryStatus#Status"));
	}

	/**
	 * This test ensures that the method correctly retrieves input status statistics and validates specific values from the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatisticsWithInputStatus() throws Exception {
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals("0", statistics.get("InputStatus#Current(A)"));
		Assert.assertEquals("60.0", statistics.get("InputStatus#Frequency(Hz)"));
		Assert.assertEquals("1", statistics.get("InputStatus#PhaseCount"));
		Assert.assertEquals("117.9", statistics.get("InputStatus#Voltage(V)"));
	}

	/**
	 * This test ensures that the method correctly retrieves output status statistics and validates specific values from the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatisticsWithOutputStatus() throws Exception {
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals("0", statistics.get("OutputStatus#Current(A)"));
		Assert.assertEquals("60.0", statistics.get("OutputStatus#Frequency(Hz)"));
		Assert.assertEquals("1", statistics.get("OutputStatus#PhaseCount"));
		Assert.assertEquals("117.9", statistics.get("OutputStatus#Voltage(V)"));
		Assert.assertEquals("0", statistics.get("OutputStatus#Load(%)"));
		Assert.assertEquals("0", statistics.get("OutputStatus#Power(W)"));
		Assert.assertEquals("Normal", statistics.get("OutputStatus#Source"));
	}

	/**
	 * Unit test to verify the functionality of the "getMultipleStatistics" method with historical properties specified.
	 * This test sets historical properties, retrieves statistics, and ensures that the method correctly handles
	 * dynamic statistics and validates specific values from the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testGetMultipleStatisticsWithHistorical() throws Exception {
		middleAtlanticUPSCommunicator.setHistoricalProperties("Temperature(C), Current(A), Load(%), Power(W), Capacity(%)");
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Map<String, String> dynamics = extendedStatistic.getDynamicStatistics();
		Assert.assertEquals(37, statistics.size());
		Assert.assertEquals(18, advancedControllablePropertyList.size());
		Assert.assertEquals(6, dynamics.size());
	}

	/**
	 * Unit test to verify the functionality of switching control for an outlet in the MiddleAtlanticUPSCommunicator class.
	 * This test ensures that the method correctly switches control for a specific outlet, updates its status, and verifies the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testSwitchControl() throws Exception {
		middleAtlanticUPSCommunicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();

		String property = UPSConstant.OUTLET_CONTROL_GROUP.concat("Outlet3");
		String value = "0";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		middleAtlanticUPSCommunicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Optional<AdvancedControllableProperty> advancedControllableProperty = advancedControllablePropertyList.stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value, advancedControllableProperty.get().getValue());
	}

	/**
	 * Unit test to verify the functionality of button control for an outlet in the MiddleAtlanticUPSCommunicator class.
	 * This test ensures that the method correctly sends a button control command for a specific outlet and verifies the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testButtonControl() throws Exception {
		middleAtlanticUPSCommunicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();

		String property = UPSConstant.OUTLET_CONTROL_GROUP.concat("CycleOutlet6");
		String value = "1";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		middleAtlanticUPSCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * Unit test to verify the functionality of text control for a specific property in the MiddleAtlanticUPSCommunicator class.
	 * This test ensures that the method correctly sends a text control command for a specific property and verifies the result.
	 *
	 * @throws Exception if an error occurs during the test execution.
	 */
	@Test
	void testTextControl() throws Exception {
		middleAtlanticUPSCommunicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) middleAtlanticUPSCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();

		String property = NEXT_REPLACEMENT_DATE.getName();
		String value = "01/05/2024";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		middleAtlanticUPSCommunicator.controlProperty(controllableProperty);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Optional<AdvancedControllableProperty> advancedControllableProperty = advancedControllablePropertyList.stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value, advancedControllableProperty.get().getValue());
	}
}
