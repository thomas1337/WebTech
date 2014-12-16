package de.rwth.webtech.gwt.demo.client;

import java.util.HashMap;

public class TranslatorLogic {

	HashMap<String, String> map;
	
	/**
	 * Constructor that initializes the translation mapping
	 */
	public TranslatorLogic() {
		map = new HashMap<String, String>();
		map.put("a", "4"); 
		map.put("e", "3");
		map.put("i", "1");
		map.put("o", "0");
		map.put("s", "5");
		map.put("t", "7");
	}
	
	
	/**
	 * Translation Method
	 */
	protected String translate(String input) {
		
		String output = "";
		for (char c : input.toCharArray()) {
			if (map.containsKey(String.valueOf(Character.toLowerCase(c))) 
					|| map.containsKey(String.valueOf(Character.toUpperCase(c)))) {
				output += map.get(String.valueOf(Character.toLowerCase(c)));
			}
			else {
				output += String.valueOf(c);
			}
		}
		return output;

	}
}
