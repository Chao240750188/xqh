package com.essence.jdbc;

/**
 * 不仅一个值异常
 *
 * @author Gavin
 * @company Essence
 */
public class NotOneException extends IllegalArgumentException {

    /**
     *
     */
    private static final long serialVersionUID = -2848938801111118894L;

    public NotOneException() {
        super();
    }

    /**
     * Constructs a <code>NotOneException</code> with the specified detail
     * message.
     *
     * @param s the detail message.
     */
    public NotOneException(String s) {
        super(s);
    }

    /**
     * Factory method for making a <code>NotOneException</code> given the
     * specified input which caused the error.
     *
     * @param s the input causing the error
     */
    static NotOneException forInputString(String s) {
        return new NotOneException("For input string: \"" + s + "\"");
    }
}
