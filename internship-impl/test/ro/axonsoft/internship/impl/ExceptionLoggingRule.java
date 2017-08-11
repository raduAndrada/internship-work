package ro.axonsoft.internship.impl;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ExceptionLoggingRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (final Exception e) {
                    System.out.printf("%s: caught exception %s with message: %s\n", description.getMethodName(),
                            e.getClass().getSimpleName(), e.getMessage());
                    throw e;
                }
            }
        };
    }
}