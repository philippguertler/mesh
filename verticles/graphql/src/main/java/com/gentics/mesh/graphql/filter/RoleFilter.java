package com.gentics.mesh.graphql.filter;

import com.gentics.graphqlfilter.filter.FilterField;
import com.gentics.graphqlfilter.filter.StartMainFilter;
import com.gentics.mesh.core.data.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleFilter extends StartMainFilter<Role> {

	private static final String NAME = "RoleFilter";

	private static RoleFilter instance;
	public static synchronized RoleFilter filter() {
		if (instance == null) {
			instance = new RoleFilter();
		}
		return instance;
	}

	private RoleFilter() {
		super(NAME, "Filters roles");
	}

	@Override
	protected List<FilterField<Role, ?>> getFilters() {
		List<FilterField<Role, ?>> filters = new ArrayList<>();
		filters.addAll(CommonFields.nameFilter());
		filters.addAll(CommonFields.userTrackingFilter());
		return filters;
	}
}
