package ro.axonsoft.internship172.model.error;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImtErrorProperties.class)
@JsonDeserialize(builder = ImtErrorProperties.Builder.class)
public interface ErrorProperties {

    String getKey();

    Set<VarValue> getVars();

    @Value.Immutable
    @JsonSerialize(as = ImtErrorProperties.VarValue.class)
    @JsonDeserialize(builder = ImtErrorProperties.VarValue.Builder.class)
    public interface VarValue {
        @Value.Parameter
        String getName();

        @Value.Parameter
        Object getValue();
    }
}
