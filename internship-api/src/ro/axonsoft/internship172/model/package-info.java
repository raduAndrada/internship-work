/**
 * @author andrei.morar
 *
 */
@Value.Style(typeImmutable = "Imt*", typeModifiable = "Mdf*", depluralize = true, additionalJsonAnnotations = {
		JsonIgnore.class }, visibility = ImplementationVisibility.PUBLIC, validationMethod = org.immutables.value.Value.Style.ValidationMethod.NONE, deepImmutablesDetection = true)
// @formatter:on
package ro.axonsoft.internship172.model;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonIgnore;
