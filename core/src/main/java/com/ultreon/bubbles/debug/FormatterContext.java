package com.ultreon.bubbles.debug;

import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.libs.commons.v0.Color;
import com.ultreon.libs.text.v1.MutableText;
import com.ultreon.libs.text.v1.TextObject;

import java.util.Locale;
import java.util.function.Consumer;

public class FormatterContext implements IFormatterContext {
    public static final String KEYWORD_COLOR = "#e030ff";
    public static final String NUMBER_COLOR = "#ffa030";
    public static final String ENUM_CONST_COLOR = "#ff7030";
    public static final String STRING_COLOR = "#30ff40";
    public static final String STRING_ESCAPE_COLOR = "#30F0FF";
    public static final String METHOD_COLOR = "#30a0ff";
    public static final String FIELD_COLOR = "#FF3050";
    public static final String CLASS_COLOR = "#ff30a0";
    public static final String COMMENT_COLOR = "#777777";
    public static final String ANNOTATION_COLOR = "#FFC030";
    public static final String PACKAGE_COLOR = "#30a0a0";
    public static final String PARAMETER_COLOR = "#FF4040";
    public static final String IDENTIFIER_COLOR = "#40ffff";
    public static final String OPERATOR_COLOR = "#999999";
    public static final String ERROR_COLOR = "#FF3050";
    public static final String DEFAULT_COLOR = "#bbbbbb";

    private MutableText builder = TextObject.literal("");

    @Override
    public IFormatterContext keyword(String text) {
        colored(text, KEYWORD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(String text) {
        colored(text, NUMBER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(Number number) {
        number(String.valueOf(number));
        return this;
    }

    @Override
    public IFormatterContext string(String text) {
        colored(text, STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext stringEscape(String text) {
        colored(text, STRING_ESCAPE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext operator(String text) {
        colored(text, OPERATOR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext identifier(String text) {
        colored(text, IDENTIFIER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text) {
        colored(text, PARAMETER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text, Object value) {
        parameter(text);
        operator(": ");
        other(value);
        return this;
    }

    @Override
    public IFormatterContext comment(String text) {
        colored(text, COMMENT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext error(String text) {
        colored(text, ERROR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext className(String text) {
        colored(text, CLASS_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(String text) {
        colored(text, ENUM_CONST_COLOR);
        return this;
    }

    @Override
    public IFormatterContext packageName(String text) {
        colored(text, PACKAGE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext methodName(String text) {
        colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext functionName(String text) {
        colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext callName(String text) {
        colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext field(String text) {
        colored(text, FIELD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext annotation(String text) {
        colored(text, ANNOTATION_COLOR);
        return this;
    }

    @Override
    public IFormatterContext normal(String text) {
        colored(text, DEFAULT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(Enum<?> enumValue) {
        if (GameSettings.instance().getDebugOptions().isSpacedEnumConstants()) {
            enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", " "));
        } else {
            enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", "-"));
        }
        return this;
    }

    @Override
    public IFormatterContext classValue(Class<?> clazz) {
        packageName(clazz.getPackage().getName() + ".");
        className(clazz.getName());
        return this;
    }

    @Override
    public IFormatterContext space() {
        normal(" ");
        return this;
    }

    @Override
    public IFormatterContext separator() {
        operator(", ");
        return this;
    }

    @Override
    public IFormatterContext hex(String hexString) {
        number(hexString);
        return this;
    }

    @Override
    public IFormatterContext hexValue(int number) {
        hex(Integer.toHexString(number));
        return this;
    }

    @Override
    public IFormatterContext intValue(int number) {
        number(Integer.toString(number));
        return this;
    }

    @Override
    public IFormatterContext longValue(long number) {
        number(Long.toString(number));
        return this;
    }

    @Override
    public IFormatterContext floatValue(float number) {
        number(Float.toString(number));
        return this;
    }

    @Override
    public IFormatterContext doubleValue(double number) {
        number(Double.toString(number));
        return this;
    }

    @Override
    public IFormatterContext other(Object obj) {
        FormatterContext ctx = new FormatterContext();
        DebugRenderer.format(obj, ctx);
        builder.append(ctx.build());
        return this;
    }

    @Override
    public IFormatterContext stringEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\"' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\"");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }

        colored(current.toString(), STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext charsEscaped(String text) {
        StringBuilder current = new StringBuilder();

        for (char c : text.toCharArray()) {
            switch (c) {
                case '\b' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\b");
                    current = new StringBuilder();
                }
                case '\t' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\t");
                    current = new StringBuilder();
                }
                case '\n' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\n");
                    current = new StringBuilder();
                }
                case '\f' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\f");
                    current = new StringBuilder();
                }
                case '\r' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\r");
                    current = new StringBuilder();
                }
                case '\'' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\'");
                    current = new StringBuilder();
                }
                case '\\' -> {
                    colored(current.toString(), STRING_COLOR);
                    stringEscape("\\\\");
                    current = new StringBuilder();
                }
                default -> current.append(c);
            }
        }
        return this;
    }

    @Deprecated
    @Override
    public IFormatterContext direct(String alreadyFormatted) {
        builder.append(TextObject.nullToEmpty(alreadyFormatted));
        return this;
    }

    @Override
    public void subFormat(Consumer<IFormatterContext> o) {
        FormatterContext ctx = new FormatterContext();
        o.accept(ctx);
        builder.append(ctx.build());
    }

    private void colored(String text, String keywordColor) {
        Color hex = Color.hex(keywordColor);
        java.awt.Color color = hex.toAwt();
        MutableText o = TextObject.literal(text);
        o.setColor(color);
        builder = builder.append(o);
    }

    public TextObject build() {
        return builder;
    }
}
