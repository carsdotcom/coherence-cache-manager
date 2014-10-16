package com.cars.cache.coherence;

import java.util.Collection;
import java.util.Map;

/**
 * Coherence configurator.
 * @author dmistry
 * 
 */
public interface CoherenceConfig {

	/**
	 * Returns a collection of cache names to be pre-loaded by
	 * {@link CoherenceCacheManager}.
	 * @return collection of cache names
	 */
	public Collection<String> getCacheNames();

	/**
	 * Returns a mapping of cache name and its key property name. The actual key
	 * value will be resolved by reflection. <br>
	 * 
	 * @return a map of cache name to key property name
	 */
	public Map<String, String> getKeyMappings();

	/**
	 * Returns the number of elements to be used in a putAll() operation
	 * @return
	 */
	public int getBufferSize();

}
