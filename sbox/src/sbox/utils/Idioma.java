package sbox.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Idioma {
	private PropertiesConfiguration config;

	public PropertiesConfiguration setIdioma(String idioma) throws ConfigurationException {
		switch (idioma) {
		case "espanol":
			config = new PropertiesConfiguration(
					"C:\\smile\\properties\\lang\\" + idioma + ".properties");
			break;
		case "ingles":
			config = new PropertiesConfiguration(
					"C:\\smile\\properties\\lang\\" + idioma + ".properties");
			break;
		default:
			config = new PropertiesConfiguration(
					"C:\\smile\\properties\\lang\\" + idioma + ".properties");
		}

		return config;
	}
}
