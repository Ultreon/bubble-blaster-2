package dev.ultreon.bubbles.debug;

import dev.ultreon.bubbles.settings.GameSettings;
import dev.ultreon.libs.commons.v0.Color;
import dev.ultreon.libs.text.v1.MutableText;
import dev.ultreon.libs.text.v1.TextObject;

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
        this.colored(text, KEYWORD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(String text) {
        this.colored(text, NUMBER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext number(Number number) {
        this.number(String.valueOf(number));
        return this;
    }

    @Override
    public IFormatterContext string(String text) {
        this.colored(text, STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext stringEscape(String text) {
        this.colored(text, STRING_ESCAPE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext operator(String text) {
        this.colored(text, OPERATOR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext identifier(String text) {
        this.colored(text, IDENTIFIER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text) {
        this.colored(text, PARAMETER_COLOR);
        return this;
    }

    @Override
    public IFormatterContext parameter(String text, Object value) {
        this.parameter(text);
        this.operator(": ");
        this.other(value);
        return this;
    }

    @Override
    public IFormatterContext comment(String text) {
        this.colored(text, COMMENT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext error(String text) {
        this.colored(text, ERROR_COLOR);
        return this;
    }

    @Override
    public IFormatterContext className(String text) {
        this.colored(text, CLASS_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(String text) {
        this.colored(text, ENUM_CONST_COLOR);
        return this;
    }

    @Override
    public IFormatterContext packageName(String text) {
        this.colored(text, PACKAGE_COLOR);
        return this;
    }

    @Override
    public IFormatterContext methodName(String text) {
        this.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext functionName(String text) {
        this.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext callName(String text) {
        this.colored(text, METHOD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext field(String text) {
        this.colored(text, FIELD_COLOR);
        return this;
    }

    @Override
    public IFormatterContext annotation(String text) {
        this.colored(text, ANNOTATION_COLOR);
        return this;
    }

    @Override
    public IFormatterContext normal(String text) {
        this.colored(text, DEFAULT_COLOR);
        return this;
    }

    @Override
    public IFormatterContext enumConstant(Enum<?> enumValue) {
        if (GameSettings.instance().getDebugOptions().isSpacedEnumConstants()) {
            this.enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", " "));
        } else {
            this.enumConstant(enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", "-"));
        }
        return this;
    }

    @Override
    public IFormatterContext classValue(Class<?> clazz) {
        this.packageName(clazz.getPackage().getName() + ".");
        this.className(clazz.getName());
        return this;
    }

    @Override
    public IFormatterContext space() {
        this.normal(" ");
        return this;
    }

    @Override
    public IFormatterContext separator() {
        this.operator(", ");
        return this;
    }

    @Override
    public IFormatterContext hex(String hexString) {
        this.number(hexString);
        return this;
    }

    @Override
    public IFormatterContext hexValue(int number) {
        this.hex(Integer.toHexString(number));
        return this;
    }

    @Override
    public IFormatterContext intValue(int number) {
        this.number(Integer.toString(number));
        return this;
    }

    @Override
    public IFormatterContext longValue(long number) {
        this.number(Long.toString(number));
        return this;
    }

    @Override
    public IFormatterContext floatValue(float number) {
        this.number(Float.toString(number));
        return this;
    }

    @Override
    public IFormatterContext doubleValue(double number) {
        this.number(Double.toString(number));
        return this;
    }

    @Override
    public IFormatterContext other(Object obj) {
        var ctx = new FormatterContext();
        DebugRenderer.format(obj, ctx);
        this.builder.append(ctx.build());
        return this;
    }

    @Override
    public IFormatterContext stringEscaped(String text) {
        var current = new StringBuilder();

        for (var c : text.toCharArray()) {
            switch (c) {
                case '\b':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\b");
                    current = new StringBuilder();
                    break;
                case '\t':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\t");
                    current = new StringBuilder();
                    break;
                case '\n':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\n");
                    current = new StringBuilder();
                    break;
                case '\f':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\f");
                    current = new StringBuilder();
                    break;
                case '\r':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\r");
                    current = new StringBuilder();
                    break;
                case '\"':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\"");
                    current = new StringBuilder();
                    break;
                case '\\':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\\");
                    current = new StringBuilder();
                    break;
                default:
                    current.append(c);
                    break;
            }
        }

        this.colored(current.toString(), STRING_COLOR);
        return this;
    }

    @Override
    public IFormatterContext charsEscaped(String text) {
        var current = new StringBuilder();

        for (var c : text.toCharArray()) {
            switch (c) {
                case '\b':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\b");
                    current = new StringBuilder();
                    break;
                case '\t':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\t");
                    current = new StringBuilder();
                    break;
                case '\n':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\n");
                    current = new StringBuilder();
                    break;
                case '\f':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\f");
                    current = new StringBuilder();
                    break;
                case '\r':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\r");
                    current = new StringBuilder();
                    break;
                case '\'':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\'");
                    current = new StringBuilder();
                    break;
                case '\\':
                    this.colored(current.toString(), STRING_COLOR);
                    this.stringEscape("\\\\");
                    current = new StringBuilder();
                    break;
                default:
                    current.append(c);
                    break;
            }
        }
        return this;
    }

    @Deprecated
    @Override
    public IFormatterContext direct(String alreadyFormatted) {
        this.builder.append(TextObject.nullToEmpty(alreadyFormatted));
        return this;
    }

    @Override
    public void subFormat(Consumer<IFormatterContext> o) {
        var ctx = new FormatterContext();
        o.accept(ctx);
        this.builder.append(ctx.build());
    }

    private void colored(String text, String keywordColor) {
        var hex = Color.hex(keywordColor);
        var color = hex.toAwt();
        var o = TextObject.literal(text);
        o.setColor(color);
        this.builder = this.builder.append(o);
    }

    public TextObject build() {
        return this.builder;
    }
}
