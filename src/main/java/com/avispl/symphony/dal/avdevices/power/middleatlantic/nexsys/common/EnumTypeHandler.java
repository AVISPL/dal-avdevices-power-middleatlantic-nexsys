/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.power.middleatlantic.nexsys.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.avispl.symphony.api.dal.error.ResourceNotReachableException;

/**
 * EnumTypeHandler class defined the enum for monitoring and controlling process
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/21/2023
 * @since 1.0.0
 */
public class EnumTypeHandler {
	/**
	 * Get an array of all enum names
	 *
	 * @param enumType the enum type is enum class
	 */
	public static <T extends Enum<T>> String[] getEnumNames(Class<T> enumType) {
		List<String> names = new ArrayList<>();
		for (T c : enumType.getEnumConstants()) {
			try {
				Method method = c.getClass().getMethod("getName");
				String name = (String) method.invoke(c); // getName executed
				names.add(name);
			} catch (Exception e) {
				throw new ResourceNotReachableException("Error to convert enum " + enumType.getSimpleName() + " to names", e);
			}
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Get value by name
	 *
	 * @param enumType the enum type is enum class
	 * @param name is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> String getValueByName(Class<T> enumType, String name) {
		try {
			for (T metric : enumType.getEnumConstants()) {
				Method methodName = metric.getClass().getMethod("getName");
				String nameMetric = (String) methodName.invoke(metric); // getName executed
				if (name.equals(nameMetric)) {
					Method methodValue = metric.getClass().getMethod("getValue");
					return methodValue.invoke(metric).toString();
				}
			}
			throw new ResourceNotReachableException("Fail to get enum " + enumType.getSimpleName() + " with name is " + name);
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}

	/**
	 * Get name by value
	 *
	 * @param enumType the enum type is enum class
	 * @param value is String
	 * @return T is metric instance
	 */
	public static <T extends Enum<T>> String getNameByValue(Class<T> enumType, String value) {
		try {
			for (T metric : enumType.getEnumConstants()) {
				Method methodValue = metric.getClass().getMethod("getValue");
				String valueMetric = methodValue.invoke(metric).toString(); // getName executed
				if (value.equals(valueMetric)) {
					Method methodName = metric.getClass().getMethod("getName");
					return methodName.invoke(metric).toString();
				}
			}
			return UPSConstant.NONE;
		} catch (Exception e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		}
	}
}
