
package com.meshyog.emptycan.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * The Class GsonExclusionStrategy.
 */
public class GsonExclusionStrategy implements ExclusionStrategy {

	/** The type to exclude. */
	private final Class<?> typeToExclude;

	/**
	 * Instantiates a new gson exclusion strategy.
	 *
	 * @param clazz the clazz
	 */
	public GsonExclusionStrategy(Class<?> clazz) {
		this.typeToExclude = clazz;
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
	 */
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return (this.typeToExclude != null && this.typeToExclude == clazz)
				|| clazz.getAnnotation(GsonExclude.class) != null;
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
	 */
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(GsonExclude.class) != null;
	}

}