package com.gentics.mesh.parameter;

import com.gentics.mesh.handler.ActionContext;

import io.vertx.core.MultiMap;

/**
 * Abstract class for parameter provider implementations.
 */
public abstract class AbstractParameters implements ParameterProvider {

	protected MultiMap parameters;

	public AbstractParameters(ActionContext ac) {
		this(ac.getParameters());
		validate();
	}

	public AbstractParameters(MultiMap parameters) {
		this.parameters = parameters;
	}

	public AbstractParameters() {
		this(MultiMap.caseInsensitiveMultiMap());
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public MultiMap getParameters() {
		return parameters;
	}

	@Override
	public void setParameter(String name, String value) {
		parameters.set(name, value);
	}

	@Override
	public String toString() {
		return getQueryParameters();
	}
}