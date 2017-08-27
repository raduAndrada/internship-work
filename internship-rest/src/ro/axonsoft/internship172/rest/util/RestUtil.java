package ro.axonsoft.internship172.rest.util;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.base.SortCriterion;
import ro.axonsoft.internship172.model.base.SortDirection;

public class RestUtil {

	public static <T extends Enum<T>, C extends SortCriterion<T>> List<C> parseSort(final List<String> sortStr,
			final Class<T> criterionTypeClass, final SortCriterionCreator<T, C> creator) {
		return ImmutableList.copyOf(sortStr.stream().map(str -> parseSort(str, criterionTypeClass, creator))::iterator);
	}

	public static <T extends Enum<T>, C extends SortCriterion<T>> C parseSort(final String sortStr,
			final Class<T> criterionTypeClass, final SortCriterionCreator<T, C> creator) {
		final String[] parts = sortStr.split(":");
		if (parts.length == 1) {
			return creator.create(Enum.valueOf(criterionTypeClass, parts[0].toUpperCase()), SortDirection.ASC);
		} else if (parts.length == 2) {
			return creator.create(Enum.valueOf(criterionTypeClass, parts[0].toUpperCase()),
					SortDirection.valueOf(parts[1].toUpperCase()));
		} else {
			throw new IllegalArgumentException(String.format("Unexpected sort %s", sortStr));
		}
	}

	@FunctionalInterface
	public static interface SortCriterionCreator<T extends Enum<T>, C extends SortCriterion<T>> {

		C create(T type, SortDirection dir);

	}
}
