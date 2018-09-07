package io.topiacoin.node.exceptions;

public class NoSuchDataItemExceptionTest extends AbstractThrowableTest {
    /**
     * Abstract method that returns the Class object representing the Throwable Class that is under test.
     *
     * @return The Class object representing the Throwable Class under test.
     */
    @Override
    public Class getExceptionClass() {
        return NoSuchDataItemException.class;
    }
}
