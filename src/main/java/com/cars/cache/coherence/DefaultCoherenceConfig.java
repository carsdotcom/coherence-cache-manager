package com.cars.cache.coherence;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A coherence configuration with defaults
 * @author dmistry
 * 
 */
public class DefaultCoherenceConfig implements CoherenceConfig {

	/**
	 * Returns an empty collection
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<String> getCacheNames() {
		return Collections.EMPTY_LIST;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getKeyMappings() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public int getBufferSize() {
		return 1000;
	}

}
