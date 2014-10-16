package com.cars.cache.coherence;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

/**
 * {@link CacheManager backed by Coherence {@link CacheFactory}.
 * @see Cacheable
 * @author dmistry
 * 
 */
public class CoherenceCacheManager implements CacheManager, DisposableBean,
		InitializingBean {

	private Logger logger = LoggerFactory
			.getLogger(CoherenceCacheManager.class);

	private final ConcurrentMap<String, CoherenceCache> cacheMap = new ConcurrentHashMap<String, CoherenceCache>(
			16);

	private Set<String> cacheNames = new LinkedHashSet<String>(16);

	private CoherenceConfig coherenceConfig;

	public CoherenceCacheManager() {
		this(new DefaultCoherenceConfig());
	}

	public CoherenceCacheManager(CoherenceConfig coherenceConfig) {
		Assert.notNull(coherenceConfig, "CoherenceConfig cannot be null");
		Assert.notNull(coherenceConfig.getCacheNames(),
				"Cache names cannot be null");
		Assert.notNull(coherenceConfig.getKeyMappings(),
				"Cache key mappings cannot be null");
		this.coherenceConfig = coherenceConfig;
		CacheFactory.ensureCluster();
	}

	@Override
	public void afterPropertiesSet() {
		this.cacheMap.clear();
		this.cacheNames.clear();
		loadCaches();
	}

	protected final void addCache(CoherenceCache cache) {
		this.cacheMap.put(cache.getName(), cache);
		this.cacheNames.add(cache.getName());
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheNames);
	}

	protected void loadCaches() {
		Collection<String> cacheNames = this.coherenceConfig.getCacheNames();
		if (!CollectionUtils.isEmpty(cacheNames)) {
			for (String name : cacheNames) {
				if (logger.isDebugEnabled()) {
					logger.debug("Initializing cache :  " + name);
				}
				CoherenceCache cache = buildCache(name);
				addCache(cache);
			}
		}
	}

	public Cache getCache(String name) {
		CoherenceCache cache = this.cacheMap.get(name);
		if (cache == null) {
			cache = buildCache(name);
			addCache(cache);
		}
		return cache;
	}

	private CoherenceCache buildCache(String name) {
		Map<String, String> cacheKeyMappings = this.coherenceConfig
				.getKeyMappings();
		NamedCache namedCache = CacheFactory.getCache(name);
		String keyName = cacheKeyMappings.get(name);
		if (StringUtils.isEmpty(keyName)) {
			// No key named found for this cache
			return new CoherenceCache(namedCache,
					this.coherenceConfig.getBufferSize());
		}
		else {
			return new CoherenceCache(namedCache,
					this.coherenceConfig.getBufferSize(), keyName, true);
		}
	}

	public CoherenceConfig getConfig() {
		return coherenceConfig;
	}

	@Override
	public void destroy() throws Exception {
		// Release all local resources
		for (CoherenceCache cache : cacheMap.values()) {
			CacheFactory.releaseCache(cache.getNativeCache());
		}
	}

}
