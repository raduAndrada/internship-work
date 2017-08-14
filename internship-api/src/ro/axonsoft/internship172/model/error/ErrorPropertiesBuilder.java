package ro.axonsoft.internship172.model.error;

import java.util.Set;

import com.google.common.collect.Sets;

public class ErrorPropertiesBuilder<V extends Enum<V> & ErrorSpec.Var, E extends Enum<E> & ErrorSpec<V>> {

    public static <V extends Enum<V> & ErrorSpec.Var, E extends Enum<E> & ErrorSpec<V>> ErrorPropertiesBuilder<V, E> of(final E spec) {
        return new ErrorPropertiesBuilder<>(spec);
    }

    private final E spec;

    private final Set<InternalVarValue> values = Sets.newTreeSet();

    private ErrorPropertiesBuilder(final E spec) {
        this.spec = spec;
    }

    public ErrorPropertiesBuilder<V, E> var(final V var, final Object value) {
        values.add(new InternalVarValue(var, value));
        return this;
    }

    public ErrorProperties build() {
        return ImtErrorProperties.builder()
                .key(spec.getKey())
                .vars(values.stream().map(x -> ImtErrorProperties.VarValue.of(x.var.getName(), x.value))
                        .map(ErrorProperties.VarValue.class::cast)::iterator)
                .build();
    }

    private class InternalVarValue implements Comparable<InternalVarValue> {
        private final V var;
        private final Object value;

        public InternalVarValue(final V var, final Object value) {
            this.var = var;
            this.value = value;
        }

        @Override
        public int compareTo(final ErrorPropertiesBuilder<V, E>.InternalVarValue o) {
            return var.getIndex().compareTo(o.var.getIndex());
        }

    }

}
