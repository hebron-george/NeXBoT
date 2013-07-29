package client;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class stringAccessor {
	private static final String BUNDLE_NAME = "client.config"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private stringAccessor() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}