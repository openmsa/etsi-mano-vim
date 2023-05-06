/**
 *     Copyright (C) 2019-2023 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.vim;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestBean {
	private TestBean() {
		//
	}

	public static void testClass(final Class clzz) {
		final Class<DefaultQuotas> clazz = DefaultQuotas.class;
		try {
			final Object obj = clazz.getConstructor().newInstance();
			final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			testObject(obj, props);
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testObject(final Object obj, final PropertyDescriptor[] props) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException {
		for (final PropertyDescriptor propertyDescriptor : props) {
			final Method mr = propertyDescriptor.getReadMethod();
			if (null != mr) {
				mr.invoke(obj);
			}
			final Method mw = propertyDescriptor.getWriteMethod();
			if ((null != mw) && (null != mr)) {
				final Class<?> ret = mr.getReturnType();
				if (Modifier.isAbstract(ret.getModifiers())) {
					// continue;
				}
				mw.invoke(obj, createType(ret));
			}
		}
		obj.hashCode();
		obj.toString();
		obj.equals(null);
		obj.equals(props);
		obj.equals(obj);
	}

	private static Object createType(final Class<?> ret) {
		if ("int".equals(ret.getCanonicalName()) || ret.isAssignableFrom(Boolean.class)) {
			return 123;
		}
		if ("long".equals(ret.getCanonicalName()) || ret.isAssignableFrom(Boolean.class)) {
			return 123L;
		}
		return null;
	}

}
