/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys;

import static com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSMonitoringCommand.INPUT_STATE;
import static com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSMonitoringCommand.SELF_TEST_RESULTS;
import static com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSPropertiesList.LAST_REPLACEMENT_DATE;
import static com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSPropertiesList.NEXT_REPLACEMENT_DATE;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.login.FailedLoginException;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.BatteryChargeEnum;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.BatteryConditionEnum;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.BatteryStatusEnum;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.EnumTypeHandler;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.OutputSourceEnum;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.SelfTestResultEnum;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSConstant;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSMonitoringCommand;
import com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common.UPSPropertiesList;
import com.avispl.symphony.dal.communicator.SshCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * MiddleAtlanticUPSCommunicator
 * Supported features are:
 * Device Information:
 *  <ul>
 *  <li> - FirmwareVersion</li>
 *  <li> - NumberOfOutlets</li>
 *  <li> - SerialNumber</li>
 *  <ul>
 *
 * Battery Status Group:
 * <ul>
 * <li> - Capacity(%)</li>
 * <li> - Charge</li>
 * <li> - Condition</li>
 * <li> - ExternalBatteryPackCount</li>
 * <li> - LastReplacementDate(MM/DD/YYYY)</li>
 * <li> - LastSelfTestResults</li>
 * <li> - LowLimit(%)</li>
 * <li> - NextReplacementDate(MM/DD/YYYY)</li>
 * <li> - OnBatteryTime(second)</li>
 * <li> - RemainingTime</li>
 * <li> - Status</li>
 * <li> - Temperature(C)</li>
 * <li> - Voltage(V)</li>
 * </ul>
 *
 * Input Status Group:
 * <ul>
 * <li> - Current(A)</li>
 * <li> - Frequency(Hz)</li>
 * <li> - PhaseCount</li>
 * <li> - Voltage(V)</li>
 * </ul>
 *
 * Outlet Status Group:
 * <ul>
 * <li> - Current(A)</li>
 * <li> - Frequency(Hz)</li>
 * <li> - Load(%)</li>
 * <li> - PhaseCount</li>
 * <li> - Power(W)</li>
 * <li> - Source</li>
 * <li> - Voltage(V)</li>
 * </ul>
 *
 * Outlet Control Group:
 * <ul>
 * <li> - CycleOutlet1</li>
 * <li> - CycleOutlet2</li>
 * <li> - CycleOutlet3</li>
 * <li> - CycleOutlet4</li>
 * <li> - CycleOutlet5</li>
 * <li> - CycleOutlet6</li>
 * <li> - CycleOutlet7</li>
 * <li> - CycleOutlet8</li>
 * <li> - Outlet1</li>
 * <li> - Outlet2</li>
 * <li> - Outlet3</li>
 * <li> - Outlet4</li>
 * <li> - Outlet5</li>
 * <li> - Outlet6</li>
 * <li> - Outlet7</li>
 * <li> - Outlet8</li>
 * </ul>
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 24/10/2023
 * @since 1.0.0
 */
public class MiddleAtlanticUPSCommunicator extends SshCommunicator implements Monitorable, Controller {
	/**
	 * reentrantLock is a reentrant lock used for synchronization.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * localExtendedStatistics represents the extended statistics object.
	 */
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * Local cache stores data after a period of time
	 */
	private final Map<String, String> localCacheMapOfPropertyNameAndValue = new HashMap<>();

	/**
	 * Configurable property for historical properties, comma separated values kept as set locally
	 */
	private Set<String> historicalProperties = new HashSet<>();

	/**
	 * count the failed command
	 */
	private final Map<String, String> failedMonitor = new HashMap<>();

	/**
	 * isEmergencyDelivery indicates whether it is an emergency delivery.
	 */
	private boolean isEmergencyDelivery;

	/**
	 * configManagement imported from the user interface
	 */
	private String configManagement;

	/**
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(",", this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
			this.historicalProperties.add(propertyName.trim());
		});
	}

	/**
	 * Retrieves {@link #configManagement}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@link #configManagement} value
	 *
	 * @param configManagement new value of {@link #configManagement}
	 */
	public void setConfigManagement(String configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * MiddleAtlanticPowerUnitCommunicator constructor
	 */
	public MiddleAtlanticUPSCommunicator() {
		this.setCommandErrorList(Collections.singletonList("Error: input error"));
		this.setCommandSuccessList(Arrays.asList("\r\n\u0000", "\r\n\n\u0000"));
		this.setLoginSuccessList(Collections.singletonList("\u0001\u0000"));
		this.setLoginErrorList(Collections.singletonList("Permission denied, please try again."));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		reentrantLock.lock();
		try {
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> stats = new HashMap<>();
			Map<String, String> dynamic = new HashMap<>();
			Map<String, String> controlStats = new HashMap<>();
			List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
			if (!isEmergencyDelivery) {
				convertConfigManagement();
				failedMonitor.clear();
				retrieveMonitoringData();
				if (failedMonitor.size() == UPSMonitoringCommand.values().length) {
					throw new ResourceNotReachableException("Get monitoring data failed, " + failedMonitor.get(INPUT_STATE.getCommand()));
				}
				populateMonitoringAndControllingData(stats, controlStats, dynamic, advancedControllableProperties);
				if (isConfigManagement) {
					extendedStatistics.setControllableProperties(advancedControllableProperties);
					stats.putAll(controlStats);
				}
				extendedStatistics.setDynamicStatistics(dynamic);
				extendedStatistics.setStatistics(stats);
				localExtendedStatistics = extendedStatistics;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * Check for available devices before retrieving the value
	 * ping latency information to Symphony
	 */
	@Override
	public int ping() throws Exception {
		if (isInitialized()) {
			long pingResultTotal = 0L;

			for (int i = 0; i < this.getPingAttempts(); i++) {
				long startTime = System.currentTimeMillis();

				try (Socket puSocketConnection = new Socket(this.host, this.getPort())) {
					puSocketConnection.setSoTimeout(this.getPingTimeout());
					if (puSocketConnection.isConnected()) {
						long pingResult = System.currentTimeMillis() - startTime;
						pingResultTotal += pingResult;
						if (this.logger.isTraceEnabled()) {
							this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, host, this.getPort(), pingResult));
						}
					} else {
						if (this.logger.isDebugEnabled()) {
							logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
						}
						return this.getPingTimeout();
					}
				} catch (SocketTimeoutException | ConnectException tex) {
					throw new SocketTimeoutException("Socket connection timed out");
				} catch (UnknownHostException tex) {
					throw new SocketTimeoutException("Socket connection timed out" + tex.getMessage());
				} catch (Exception e) {
					if (this.logger.isWarnEnabled()) {
						this.logger.warn(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
					}
					return this.getPingTimeout();
				}
			}
			return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
		} else {
			throw new IllegalStateException("Cannot use device class without calling init() first");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doneReading(String command, String response) throws CommandFailureException {
		if (response.replaceAll("\u0000", "").equals(command)) {
			return true;
		}
		return super.doneReading(command, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
		this.createChannel();
		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
			localExtendedStatistics.getStatistics().clear();
			localExtendedStatistics.getControllableProperties().clear();
		}
		if (!localCacheMapOfPropertyNameAndValue.isEmpty()) {
			localCacheMapOfPropertyNameAndValue.clear();
		}
		isEmergencyDelivery = false;
		isConfigManagement = false;
		failedMonitor.clear();
		this.destroyChannel();
		super.internalDestroy();
	}

	/**
	 * Populates monitoring and controlling data for a UPS (Uninterruptible Power Supply) system.
	 *
	 * @param stats A map to store monitoring statistics.
	 * @param controlStats A map to store controlling statistics.
	 * @param dynamic A map to store dynamic data.
	 * @param advancedControllableProperties A list to store advanced controllable properties.
	 */
	private void populateMonitoringAndControllingData(Map<String, String> stats, Map<String, String> controlStats, Map<String, String> dynamic,
			List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String propertyName;
		for (UPSPropertiesList property : UPSPropertiesList.values()) {
			propertyName = property.getGroup().concat(property.getName());
			value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
			switch (property) {
				case TEMPERATURE:
				case OUTPUT_LOAD:
				case OUTPUT_POWER:
				case CAPACITY:
					mapDynamicStatistic(propertyName, value, stats, dynamic);
					break;
				case INPUT_CURRENT:
				case OUTPUT_CURRENT:
					value = String.valueOf(UPSConstant.ZERO.equals(value) ? value : Double.parseDouble(value) / 10);
					mapDynamicStatistic(propertyName, value, stats, dynamic);
					break;
				case INPUT_FREQUENCY:
				case INPUT_VOLTAGE:
				case OUTPUT_FREQUENCY:
				case OUTPUT_VOLTAGE:
				case BATTERY_VOLTAGE:
					value = String.valueOf(UPSConstant.ZERO.equals(value) ? value : Double.parseDouble(value) / 10);
					stats.put(propertyName, value);
					break;
				case SOURCE:
					stats.put(propertyName, EnumTypeHandler.getNameByValue(OutputSourceEnum.class, value));
					break;
				case CONDITION:
					stats.put(propertyName, EnumTypeHandler.getNameByValue(BatteryConditionEnum.class, value));
					break;
				case STATUS:
					stats.put(propertyName, EnumTypeHandler.getNameByValue(BatteryStatusEnum.class, value));
					break;
				case CHARGE:
					stats.put(propertyName, EnumTypeHandler.getNameByValue(BatteryChargeEnum.class, value));
					break;
				case LAST_SELF_TEST_RESULTS:
					stats.put(propertyName, EnumTypeHandler.getNameByValue(SelfTestResultEnum.class, value));
					break;
				case REMAINING_TIME:
					stats.put(propertyName, convertTime(value));
					break;
				case LAST_REPLACEMENT_DATE:
				case NEXT_REPLACEMENT_DATE:
					value = convertDateFormat(value, UPSConstant.COMMAND_FORMAT_DATE, UPSConstant.UI_FORMAT_DATE);
					addAdvanceControlProperties(advancedControllableProperties, controlStats, createText(propertyName, value), value);
					break;
				case OUTLET_STATUS_1:
				case OUTLET_STATUS_2:
				case OUTLET_STATUS_3:
				case OUTLET_STATUS_4:
				case OUTLET_STATUS_5:
				case OUTLET_STATUS_6:
				case OUTLET_STATUS_7:
				case OUTLET_STATUS_8:
					if (UPSConstant.NONE.equals(value)) {
						stats.put(propertyName, value);
					} else {
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, Integer.parseInt(value), UPSConstant.OFF, UPSConstant.ON),
								UPSConstant.NUMBER_ONE.equals(value) ? UPSConstant.ON : UPSConstant.OFF);
					}
					break;
				case OUTLET_CYCLE_1:
				case OUTLET_CYCLE_2:
				case OUTLET_CYCLE_3:
				case OUTLET_CYCLE_4:
				case OUTLET_CYCLE_5:
				case OUTLET_CYCLE_6:
				case OUTLET_CYCLE_7:
				case OUTLET_CYCLE_8:
					addAdvanceControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, UPSConstant.CYCLE, UPSConstant.CYCLING, UPSConstant.GRACE_PERIOD), value);
					break;
				default:
					stats.put(propertyName, value);
					break;
			}
		}
	}

	/**
	 * Retrieves monitoring data from the UPS (Uninterruptible Power Supply) system by sending a series of commands and
	 * updates the local cache with the received data.
	 */
	private void retrieveMonitoringData() {
		String response;
		localCacheMapOfPropertyNameAndValue.clear();
		for (UPSMonitoringCommand command : UPSMonitoringCommand.values()) {
			response = sendCommand(command.getCommand());
			if (StringUtils.isNotNullOrEmpty(response) && response.length() > 7) {
				response = response.substring(7);
				switch (command) {
					case INPUT_STATE:
						updateLocalCachedValueWithGroupValue(response, UPSConstant.INPUT_STATUS_GROUP);
						break;
					case OUTPUT_STATE:
						updateLocalCachedValueWithGroupValue(response, UPSConstant.OUTPUT_STATUS_GROUP);
						break;
					case BATTERY_STATE:
						updateLocalCachedValueWithGroupValue(response, UPSConstant.BATTERY_STATUS_GROUP);
						break;
					case ALL_OUTLETS:
						updateOutletStatusLocalCached(response);
						break;
					case REPLACEMENT_DATE:
						String[] values = response.split(";");
						if (values.length >= 2) {
							localCacheMapOfPropertyNameAndValue.put(UPSConstant.BATTERY_STATUS_GROUP + LAST_REPLACEMENT_DATE.getName(), getDefaultValueForNullData(values[0]));
							localCacheMapOfPropertyNameAndValue.put(UPSConstant.BATTERY_STATUS_GROUP + NEXT_REPLACEMENT_DATE.getName(), getDefaultValueForNullData(values[1]));
						}
						break;
					case SELF_TEST_RESULTS:
						localCacheMapOfPropertyNameAndValue.put(UPSConstant.BATTERY_STATUS_GROUP + SELF_TEST_RESULTS.getName(), response);
						break;
					default:
						localCacheMapOfPropertyNameAndValue.put(command.getName(), response);
						break;
				}
			}
		}
	}

	/**
	 * Maps a dynamic statistic to either the "stats" or "dynamics" map based on certain conditions.
	 *
	 * @param propertyName The name of the property.
	 * @param value The value of the property.
	 * @param stats The map for storing monitoring statistics.
	 * @param dynamics The map for storing dynamic data.
	 */
	private void mapDynamicStatistic(String propertyName, String value, Map<String, String> stats, Map<String, String> dynamics) {
		boolean propertyListed = false;
		if (!historicalProperties.isEmpty()) {
			if (propertyName.contains(UPSConstant.HASH)) {
				propertyListed = historicalProperties.contains(propertyName.split(UPSConstant.HASH)[1]);
			} else {
				propertyListed = historicalProperties.contains(propertyName);
			}
		}
		if (propertyListed && StringUtils.isNotNullOrEmpty(value) && !UPSConstant.NONE.equals(value)) {
			dynamics.put(propertyName, value);
		} else {
			stats.put(propertyName, getDefaultValueForNullData(value));
		}
	}

	/**
	 * Updates the local cache with values from a response string based on a specified group.
	 *
	 * @param response The response string containing property values.
	 * @param group The group associated with the properties to update in the local cache.
	 */
	private void updateLocalCachedValueWithGroupValue(String response, String group) {
		List<UPSPropertiesList> monitoringProperties = Arrays.stream(UPSPropertiesList.values())
				.filter(property -> property.getGroup().equals(group)).collect(Collectors.toList());
		String[] values = splitStringAndReplaceEmpty(response);
		if (values.length >= monitoringProperties.get(monitoringProperties.size() - 1).getBitIndex()) {
			for (UPSPropertiesList property : monitoringProperties) {
				if (property.getBitIndex() != -1) {
					localCacheMapOfPropertyNameAndValue.put(group.concat(property.getName()), getDefaultValueForNullData(values[property.getBitIndex()]));
				}
			}
		}
	}

	/**
	 * Updates the local cache with outlet status values from a reversed response string.
	 *
	 * @param response The reversed response string containing outlet status values (1 or 0).
	 */
	private void updateOutletStatusLocalCached(String response) {
		if (response.length() >= 8) {
			String value;
			response = new StringBuilder(response).reverse().toString();
			for (int i = 0; i < response.length(); i++) {
				char character = response.charAt(i);
				if (character == '1' || character == '0') {
					value = String.valueOf(character);
				} else {
					value = UPSConstant.NONE;
				}
				localCacheMapOfPropertyNameAndValue.put(UPSConstant.OUTLET_CONTROL_GROUP + UPSConstant.OUTLET + (i + 1), value);
			}
		}
	}

	/**
	 * Sends a command to the UPS (Uninterruptible Power Supply) device and retrieves the response.
	 *
	 * @param command The command to be sent to the device.
	 * @return The response received from the device.
	 * @throws IllegalArgumentException If the response is empty or null.
	 * @throws FailedLoginException If another connection has accessed the device.
	 */
	private String sendCommand(String command) {
		try {
			String response = this.send(command + "\r");
			if (StringUtils.isNullOrEmpty(response)) {
				throw new IllegalArgumentException("The response is empty or null");
			}
			System.out.println("You received response: " + getResponse(response));
			return getResponse(response);
		} catch (FailedLoginException e) {
			throw new IllegalArgumentException("Another connection has accessed the device.  " + e.getMessage());
		} catch (Exception e) {
			failedMonitor.put(command, e.getMessage());
			logger.error("Error when retrieve command " + e.getMessage(), e);
			return UPSConstant.EMPTY;
		}
	}

	/**
	 * Extracts and returns the response portion from an input string containing a command response.
	 *
	 * @param inputString The input string containing a command response.
	 * @return The extracted response portion as a trimmed string.
	 */
	private String getResponse(String inputString) {
		int firstTildeIndex = inputString.indexOf("~");
		int secondTildeIndex = inputString.indexOf("~", firstTildeIndex + 1);
		int endIndex = inputString.indexOf("\r", secondTildeIndex);
		if (endIndex == -1) {
			return "";
		}
		return inputString.substring(secondTildeIndex, endIndex).trim();
	}

	/**
	 * Splits the input string using a specified regex pattern and replaces empty parts with "None".
	 *
	 * @param input The input string to be split.
	 * @return An array of strings obtained after splitting, with empty parts replaced by "None".
	 */
	private String[] splitStringAndReplaceEmpty(String input) {
		String regex = "(?<=;|^)(?=(?:[^;]*;[^;]*)*$)";
		Pattern pattern = Pattern.compile(regex);
		String[] parts = pattern.split(input);

		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].replace(";", UPSConstant.EMPTY);
			if (parts[i].isEmpty()) {
				parts[i] = UPSConstant.NONE;
			}
		}
		return parts;
	}

	/**
	 * Converts a time value (in minutes) to a human-readable format (hours and minutes).
	 *
	 * @param value The time value in minutes to be converted.
	 * @return A human-readable representation of the time in the format "X hour(s) Y minute(s)" or "0 minute(s)" if the value is zero.
	 * @throws IllegalArgumentException If the input value cannot be converted to a valid time format.
	 */
	private String convertTime(String value) {
		try {
			long inputValue = Long.parseLong(value);
			long hours = inputValue / 60;
			long minutes = inputValue % 60;

			StringBuilder result = new StringBuilder();

			if (hours > 0) {
				result.append(hours).append(" hour(s) ");
			}
			if (minutes > 0) {
				result.append(minutes).append(" minute(s) ");
			}

			return result.length() == 0 ? "0 minute(s)" : result.toString();
		} catch (Exception e) {
			logger.error("Error while formatting date: " + e.getMessage(), e);
			return UPSConstant.NONE;
		}
	}

	/**
	 * Converts a date from one date format to another date format.
	 *
	 * @param input The input date string to be converted.
	 * @param inputFormat The format of the input date string.
	 * @param outputFormat The desired output date format.
	 * @return The date string converted to the specified output format, or "None" if the input is "None".
	 * @throws IllegalArgumentException If the input date cannot be converted from the input format to the output format.
	 */
	private String convertDateFormat(String input, String inputFormat, String outputFormat) {
		if (UPSConstant.NONE.equals(input)) {
			return input;
		}
		try {
			SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
			Date date = inputDateFormat.parse(input);

			SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
			return outputDateFormat.format(date);
		} catch (Exception e) {
			logger.error("Error while convert date format: " + e.getMessage(), e);
			return UPSConstant.NONE;
		}
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(String value) {
		return StringUtils.isNotNullOrEmpty(value) ? value : UPSConstant.NONE;
	}

	/**
	 * This method is used to validate input config management from user
	 */
	private void convertConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase(UPSConstant.TRUE);
	}

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			if (StringUtils.isNotNullOrEmpty(value)) {
				stats.put(property.getName(), value);
			} else {
				stats.put(property.getName(), UPSConstant.EMPTY);
			}
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}
}
