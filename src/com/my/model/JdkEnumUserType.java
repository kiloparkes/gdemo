/*
 * $Id: JdkEnumUserType.java 28991 2009-02-27 11:10:25Z datta-av $
 *
 */
package com.my.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * @author dumer-fl
 */
@SuppressWarnings("unchecked")
public class JdkEnumUserType implements UserType, ParameterizedType {

	private int[] SQL_TYPES;
	private Class targetClass;
	private boolean intBased;
	private String property;

	public void setParameterValues(Properties parameters) {
		final String targetClassName = parameters.getProperty("targetClass");
		intBased = !parameters.containsKey("stringProperty");
		if (intBased) {
			SQL_TYPES = new int[] { Types.INTEGER };
			property = parameters.getProperty("intProperty");
		}
		else {
			SQL_TYPES = new int[] { Types.VARCHAR };
			property = parameters.getProperty("stringProperty");
		}
		if (targetClassName == null) {
			throw new MappingException("targetClass not specified.");
		}
		try {
			targetClass = Class.forName(targetClassName);
			if (!Enum.class.isAssignableFrom(targetClass)) {
				throw new MappingException("Class " + targetClassName + " is not an enum");
			}
		}
		catch (final ClassNotFoundException e) {
			throw new MappingException("Class " + targetClassName + " not found ", e);
		}
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Class returnedClass() {
		return targetClass;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (null == x || null == y) {
			return false;
		}
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		Object e = null;
		if (intBased) {
			final int value = rs.getInt(names[0]);
			if (!rs.wasNull()) {
				if (property == null) {
					try {
						e = targetClass.getEnumConstants()[value];
					}catch (final Exception ex) {
						throw new HibernateException("Failed to resolve enum '" + targetClass.getName() + "' with value '" + value + "'.", ex);
					}
				} else {
					try {
						e = targetClass.getMethod("valueOfParam", Integer.class).invoke(null, value);
					}
					catch (final Exception ex) {
						throw new HibernateException("Failed to resolve enum '" + targetClass.getName() + "' with value '" + value + "'.", ex);
					}
				}
			}
		}
		else {
			final String name = rs.getString(names[0]);
			if (!rs.wasNull()) {
				if (property == null) {
					e = Enum.valueOf(targetClass, name);
				} else {
					try {
						e = targetClass.getMethod("valueOfParam", String.class).invoke(null, name);
					}
					catch (final Exception ex) {
						throw new HibernateException("Failed to resolve enum '" + targetClass.getName() + "' with name '" + name + "'.", ex);
					}
				}
			}
		}
		return e;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		if (value == null) {
			if (intBased) {
				st.setNull(index, Types.INTEGER);
			} else {
				st.setNull(index, Types.VARCHAR);
			}
		}
		else if (intBased) {
			int val;
			if (property == null) {
				val = ((Enum)value).ordinal();
			} else {
				try {
					val = (Integer)targetClass.getMethod(createGetter(property)).invoke(value);
				}
				catch (final Exception ex) {
					throw new HibernateException("Failed to read value from enum '" + targetClass.getName() + "'", ex);
				}
			}
			st.setInt(index, val);
		}
		else {
			String name = null;
			if (property == null) {
				name = ((Enum)value).name();
			} else {
				try {
					name = (String)targetClass.getMethod(createGetter(property)).invoke(value);
				}
				catch (final Exception ex) {
					throw new HibernateException("Failed to read name from enum '" + targetClass.getName() + "'", ex);
				}
				st.setString(index, name);
			}
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	private String createGetter(String property) {
		return "get" + this.property.substring(0, 1).toUpperCase() + this.property.substring(1);
	}
}