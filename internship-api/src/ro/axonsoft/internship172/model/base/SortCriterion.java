package ro.axonsoft.internship172.model.base;

import org.immutables.value.Value;

public interface SortCriterion<E extends Enum<E>> {

	@Value.Parameter
	E getCriterion();

	@Value.Parameter
	@Value.Default
	default SortDirection getDirection() {
		return SortDirection.ASC;
	}

}