/**
 * @author andrei.morar
 *
 */
// @formatter:off
@Value.Style(typeImmutable = "Imt*", typeModifiable = "Mdf*", depluralize = true,
        additionalJsonAnnotations = {
                JsonIgnore.class
        },
        visibility = ImplementationVisibility.PUBLIC,
        validationMethod = org.immutables.value.Value.Style.ValidationMethod.NONE
)
// @formatter:on
package ro.axonsoft.internship172.web;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonIgnore;
