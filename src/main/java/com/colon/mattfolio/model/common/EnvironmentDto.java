package com.colon.mattfolio.model.common;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 웹 어플리케이션에서 사용할 설정 정보를 가져오는 클래스
 */
@Component
public class EnvironmentDto {

	private final Environment env;

	public EnvironmentDto(Environment env) {
		this.env = env;
	}

	/**
	 * KEY에 해당하는 설정값을 문자열로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 문자열로 반환, 존재하지 않는 key인 경우 null을 반환
	 */
	public String getString(String key) {
		return this.env.getProperty(key);
	}

	/**
	 * KEY에 해당하는 설정값을 문자열로 반환하는 메소드<br>
	 * KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 문자열로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public String getString(String key, String defaultValue) {
		return this.env.getProperty(key, defaultValue);
	}

	/**
	 * KEY에 해당하는 설정값을 정수로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 0을 반환
	 */
	public int getInt(String key) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Integer.parseInt(value);
		} else {
			return 0;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 정수로 반환하는 메소드<br>
	 * KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public int getInt(String key, int defaultValue) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Integer.parseInt(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 정수(long)로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 0을 반환
	 */
	public long getLong(String key) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Long.parseLong(value);
		} else {
			return 0;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 정수(long)로 반환하는 메소드<br>
	 * KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public long getLong(String key, long defaultValue) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Long.parseLong(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 실수(float)로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 0을 반환
	 */
	public float getFloat(String key) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Float.parseFloat(value);
		} else {
			return 0;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 실수(float)로 반환하는 메소드<br>
	 * KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public float getFloat(String key, float defaultValue) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Float.parseFloat(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 실수(double)로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 0을 반환
	 */
	public double getDouble(String key) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Double.parseDouble(value);
		} else {
			return 0;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 실수(double)로 반환하는 메소드<br>
	 * KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public double getDouble(String key, double defaultValue) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Double.parseDouble(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 boolean 자료형으로 반환하는 메소드
	 * 
	 * @param key 설정파일에 정의된 키
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 false를 반환
	 */
	public boolean getBoolean(String key) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Boolean.parseBoolean(value);
		} else {
			return false;
		}
	}

	/**
	 * KEY에 해당하는 설정값을 boolean 자료형으로 반환하는 메소드 KEY가 존재하지 않는 경우 기본값을 반환
	 * 
	 * @param key 설정파일에 정의된 키
	 * @param defaultValue 값이 존재하지 않을 경우 반환할 기본 값
	 * @return 해당 key의 값을 정수로 반환, 존재하지 않는 key인 경우 defaultValue을 반환
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = this.getString(key);

		if (value != null && value.isEmpty() == false) {
			return Boolean.parseBoolean(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 현재 설정파일에 정의된 모든 키 목록 가져오는 메소드
	 * 
	 * @return KEY List를 반환, KEY가 없는 경우 원소가 0개인 List를 반환
	 * 
	 * public List<String> getKeyList() { List<String> keyList = new ArrayList<String>(); Enumeration<Object> keys = this.env.keys();
	 * 
	 * if (keys != null) { while (keys.hasMoreElements()) { keyList.add(keys.nextElement().toString()); } }
	 * 
	 * return keyList; }
	 */
}