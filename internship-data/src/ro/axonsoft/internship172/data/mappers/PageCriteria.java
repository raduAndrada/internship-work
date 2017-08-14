package ro.axonsoft.internship172.data.mappers;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Criteriu pentru paginare
 * 
 * @author Andrada
 *
 */
@Value.Immutable
@Value.Modifiable
public interface PageCriteria {

	@Parameter
	Integer getStartIndex();

	@Parameter
	Integer getPageSize();

	@Parameter
	Long getBatchId();

}
