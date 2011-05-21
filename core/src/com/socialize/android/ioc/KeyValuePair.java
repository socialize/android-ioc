package com.socialize.android.ioc;

public final class KeyValuePair {

	public static enum RefType {
			BEAN, 
			CONTEXT,
			SHORT, 
			INTEGER, 
			LONG, 
			STRING, 
			CHAR, 
			BYTE,
			BOOLEAN}
	
	private String key;
	private String value;
	private RefType type;
	
	public KeyValuePair() {
		super();
	}
	
	public KeyValuePair(String key,String value,RefType type) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public RefType getType() {
		return type;
	}
	public void setType(RefType type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValuePair other = (KeyValuePair) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		}
		else if (!key.equals(other.key))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
	
	
}
