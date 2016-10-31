package ak.goobi.repec;

import java.util.List;

public class KeyValues {

	private String key;
	private List<String> values;
	
	public KeyValues(String key, List<String> values) {
		this.setKey(key);
		this.setValues(values);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
	
}
