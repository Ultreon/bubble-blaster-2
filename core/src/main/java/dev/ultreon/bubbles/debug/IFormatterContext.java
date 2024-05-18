package dev.ultreon.bubbles.debug;

import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public interface IFormatterContext {
    /**
     * Format a keyword, and append it to the formatter.
     *
     * @param text the keyword to format
     * @return this formatter context
     */
    IFormatterContext keyword(String text);

    /**
     * Format a number, and append it to the formatter.
     *
     * @param text the number to format
     * @return this formatter context
     */
    IFormatterContext number(String text);

    /**
     * Format a number, and append it to the formatter.
     *
     * @param number the number to format
     * @return this formatter context
     */
    IFormatterContext number(Number number);

    /**
     * Format a string, and append it to the formatter.
     *
     * @param text the string to format
     * @return this formatter context
     */
    IFormatterContext string(String text);

    /**
     * Format a string escape, and append it to the formatter.
     *
     * @param text the string escape to format
     * @return this formatter context
     */
    IFormatterContext stringEscape(String text);

    /**
     * Format an operator, and append it to the formatter.
     *
     * @param text the operator to format
     * @return this formatter context
     */
    IFormatterContext operator(String text);

    /**
     * Format an identifier, and append it to the formatter.
     *
     * @param text the identifier to format
     * @return this formatter context
     */
    IFormatterContext identifier(String text);

    /**
     * Format a parameter, and append it to the formatter.
     *
     * @param text the parameter to format
     * @return this formatter context
     */
    IFormatterContext parameter(String text);

    /**
     * Format a parameter with a value, and append it to the formatter.
     *
     * @param text  the parameter to format
     * @param value the value to format
     * @return this formatter context
     */
    IFormatterContext parameter(String text, Object value);

    /**
     * Format a comment, and append it to the formatter.
     *
     * @param text the comment to format
     * @return this formatter context
     */
    IFormatterContext comment(String text);

    /**
     * Format an error, and append it to the formatter.
     *
     * @param text the error to format
     * @return this formatter context
     */
    IFormatterContext error(String text);

    /**
     * Format a class name, and append it to the formatter.
     *
     * @param text the class name to format
     * @return this formatter context
     */
    IFormatterContext className(String text);

    /**
     * Format an enum constant, and append it to the formatter.
     *
     * @param enumValue the enum constant to format
     * @return this formatter context
     */
    IFormatterContext enumConstant(Enum<?> enumValue);

    /**
     * Format an enum constant, and append it to the formatter.
     *
     * @param text the enum constant to format
     * @return this formatter context
     */
    IFormatterContext enumConstant(String text);

    /**
     * Format a package name, and append it to the formatter.
     *
     * @param text the package name to format
     * @return this formatter context
     */
    IFormatterContext packageName(String text);

    /**
     * Format a method name, and append it to the formatter.
     *
     * @param text the method name to format
     * @return this formatter context
     */
    IFormatterContext methodName(String text);

    /**
     * Format a function name, and append it to the formatter.
     *
     * @param text the function name to format
     * @return this formatter context
     */
    IFormatterContext functionName(String text);

    /**
     * Format a call to a method, and append it to the formatter.
     *
     * @param text the call to a method to format
     * @return this formatter context
     */
    IFormatterContext callName(String text);

    /**
     * Format a field name, and append it to the formatter.
     *
     * @param text the field name to format
     * @return this formatter context
     */
    IFormatterContext field(String text);

    /**
     * Format an annotation, and append it to the formatter.
     *
     * @param text the annotation to format
     * @return this formatter context
     */
    IFormatterContext annotation(String text);

    /**
     * Format normal text, and append it to the formatter.
     *
     * @param text the text to format
     * @return this formatter context
     */
    IFormatterContext normal(String text);

    /**
     * Format a class, and append it to the formatter.
     *
     * @param clazz the class to format
     * @return this formatter context
     */
    IFormatterContext classValue(Class<?> clazz);

    /**
     * Append a space to the formatter.
     *
     * @return this formatter context
     */
    IFormatterContext space();

    /**
     * Append a separator to the formatter.
     *
     * @return this formatter context
     */
    IFormatterContext separator();

    /**
     * Format a hexadecimal number, and append it to the formatter.
     *
     * @param hexString the hexadecimal number to format
     * @return this formatter context
     */
    IFormatterContext hex(String hexString);

    /**
     * Format a hexadecimal number, and append it to the formatter.
     *
     * @param number the hexadecimal number to format
     * @return this formatter context
     */
    IFormatterContext hexValue(int number);

    /**
     * Format an integer, and append it to the formatter.
     *
     * @param number the integer to format
     * @return this formatter context
     */
    IFormatterContext intValue(int number);

    /**
     * Format a long, and append it to the formatter.
     *
     * @param number the long to format
     * @return this formatter context
     */
    IFormatterContext longValue(long number);

    /**
     * Format a float, and append it to the formatter.
     *
     * @param number the float to format
     * @return this formatter context
     */
    IFormatterContext floatValue(float number);

    /**
     * Format a double, and append it to the formatter.
     *
     * @param number the double to format
     * @return this formatter context
     */
    IFormatterContext doubleValue(double number);

    /**
     * Format a string with escape characters, and append it to the formatter.
     *
     * @param text the string to format with escape characters
     * @return this formatter context
     */
    IFormatterContext stringEscaped(String text);

    /**
     * Format a character with escape characters, and append it to the formatter.
     *
     * @param text the character to format with escape characters
     * @return this formatter context
     */
    IFormatterContext charsEscaped(String text);

    /**
     * Format another object, and append it to the formatter.
     *
     * @param obj the object to format
     * @return this formatter
     */
    IFormatterContext other(Object obj);

    /**
     * Directly append the given text to the output.
     *
     * @param alreadyFormatted the already formatted string.
     * @return this formatter
     * @deprecated Don't use this method, this is for internal use only.
     */
    @Deprecated
    IFormatterContext direct(String alreadyFormatted);

    void subFormat(Consumer<IFormatterContext> o);
}
