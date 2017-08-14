package ro.axonsoft.internship172.model.error;

import java.util.List;

public interface ErrorSpec<V extends ErrorSpec.Var> {

    String getKey();

    List<V> getVars();

    public interface Var {

        Integer getIndex();

        String getName();
    }
}
