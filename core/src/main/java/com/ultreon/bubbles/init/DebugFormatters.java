package com.ultreon.bubbles.init;

import com.ultreon.bubbles.debug.Formatter;
import com.ultreon.bubbles.debug.FormatterRegistry;
import com.ultreon.bubbles.debug.IFormatterContext;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.vector.*;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.text.v0.MutableText;
import com.ultreon.libs.text.v0.TextObject;
import net.fabricmc.api.EnvType;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

@SuppressWarnings({"rawtypes", "unused"})
public final class DebugFormatters {
    public static final Formatter<Number> NUMBER = FormatterRegistry.register(new Formatter<>(Number.class, new Identifier("java/number")) {
        @Override
        public void format(Number obj, IFormatterContext context) {
            context.number(obj);
        }
    });
    public static final Formatter<Boolean> BOOLEAN = FormatterRegistry.register(new Formatter<>(Boolean.class, new Identifier("java/boolean")) {
        @Override
        public void format(Boolean obj, IFormatterContext context) {
            context.keyword(obj ? "true" : "false");
        }
    });
    public static final Formatter<String> STRING = FormatterRegistry.register(new Formatter<>(String.class, new Identifier("java/string")) {
        @Override
        public void format(String obj, IFormatterContext context) {
            context.string("\"").stringEscaped(obj).string("\"");
        }
    });
    public static final Formatter<Character> CHARACTER = FormatterRegistry.register(new Formatter<>(Character.class, new Identifier("java/character")) {
        @Override
        public void format(Character obj, IFormatterContext context) {
            context.string("'").charsEscaped(obj.toString()).string("'");
        }
    });
    public static final Formatter<Enum> ENUM = FormatterRegistry.register(new Formatter<>(Enum.class, new Identifier("java/enum")) {
        @Override
        public void format(Enum obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    public static final Formatter<List> LIST = FormatterRegistry.register(new Formatter<>(List.class, new Identifier("java/list")) {
        @Override
        public void format(List obj, IFormatterContext context) {
            context.operator("[");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator("]");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("[...]");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator("]");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Map> MAP = FormatterRegistry.register(new Formatter<>(Map.class, new Identifier("java/map")) {
        @SuppressWarnings("unchecked")
        @Override
        public void format(Map obj, IFormatterContext context) {
            context.operator("{");

            Iterator<? extends Map.Entry> it = obj.entrySet().iterator();
            if (!it.hasNext()) {
                context.operator("}");
                return;
            }

            while (true) {
                Map.Entry<?, ?> e = it.next();
                if (e.getKey() == obj) {
                    context.keyword("{...}");
                } else if (e.getKey() == e) {
                    context.keyword("<...>");
                } else {
                    context.other(e.getKey());
                }

                context.operator(":");

                if (e.getValue() == obj) {
                    context.keyword("{...}");
                } else if (e.getValue() == e) {
                    context.keyword("<...>");
                } else {
                    context.other(e.getValue());
                }

                if (!it.hasNext()) {
                    context.operator("}");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Map.Entry> MAP_ENTRY = FormatterRegistry.register(new Formatter<>(Map.Entry.class, new Identifier("java/map/entry")) {
        @Override
        public void format(Map.Entry obj, IFormatterContext context) {
            if (obj.getKey() == obj) {
                context.keyword("<...>");
            } else {
                context.other(obj.getKey());
            }
            context.operator(": ");
            if (obj.getValue() == obj) {
                context.keyword("<...>");
            } else {
                context.other(obj.getValue());
            }
        }
    });
    public static final Formatter<Set> SET = FormatterRegistry.register(new Formatter<>(Set.class, new Identifier("java/set")) {
        @Override
        public void format(Set obj, IFormatterContext context) {
            context.operator("{");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator("}");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("{...}");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator("}");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<Collection> COLLECTION = FormatterRegistry.register(new Formatter<>(Collection.class, new Identifier("java/set")) {
        @Override
        public void format(Collection obj, IFormatterContext context) {
            context.operator("(");

            Iterator<?> it = obj.iterator();
            if (!it.hasNext()) {
                context.operator(")");
                return;
            }

            while (true) {
                Object e = it.next();
                if (e == obj) {
                    context.keyword("(...)");
                } else {
                    context.other(e);
                }
                if (!it.hasNext()) {
                    context.operator(")");
                    return;
                }
                context.separator();
            }
        }
    });
    public static final Formatter<UUID> UUID = FormatterRegistry.register(new Formatter<>(UUID.class, new Identifier("java/uuid")) {
        @Override
        public void format(UUID obj, IFormatterContext context) {
            Iterator<String> iterator = Arrays.stream(obj.toString().split("-")).iterator();
            if (!iterator.hasNext()) return;

            while (true) {
                String s = iterator.next();
                context.hex(s);
                if (!iterator.hasNext()) return;
                context.operator("-");
            }
        }
    });
    public static final Formatter<Color> AWT_COLOR = FormatterRegistry.register(new Formatter<>(Color.class, new Identifier("java/awt/color")) {
        @Override
        public void format(Color obj, IFormatterContext context) {
            String s = Integer.toHexString(obj.getRGB());

            context.operator("#");
            context.hex("0".repeat(8 - s.length()) + s);
        }
    });
    public static final Formatter<com.ultreon.libs.commons.v0.Color> COLOR = FormatterRegistry.register(new Formatter<>(com.ultreon.libs.commons.v0.Color.class, new Identifier("color")) {
        @Override
        public void format(com.ultreon.libs.commons.v0.Color obj, IFormatterContext context) {
            String s = Integer.toHexString(obj.getRgb());

            context.operator("#");
            context.hex("0".repeat(8 - s.length()) + s);
        }
    });
    public static final Formatter<Entity> ENTITY = FormatterRegistry.register(new Formatter<>(Entity.class, new Identifier("entity")) {
        @Override
        public void format(Entity obj, IFormatterContext context) {
            context.className(obj.getClass().getName()).space().other(obj.getUniqueId());
            context.operator(" (").other(obj.getName()).operator(") #").other(obj.getId());
        }
    });
    public static final Formatter<Player> PLAYER = FormatterRegistry.register(new Formatter<>(Player.class, new Identifier("player")) {
        @Override
        public void format(Player obj, IFormatterContext context) {
            context.className("Player").space().other(obj.getUniqueId());
            context.operator(" (").other(obj.getName()).operator(")");
        }
    });
    public static final Formatter<Identifier> IDENTIFIER = FormatterRegistry.register(new Formatter<>(Identifier.class, new Identifier("identifier")) {
        @Override
        public void format(Identifier obj, IFormatterContext context) {
            if (GameSettings.instance().getDebugOptions().isSpacedNamespace()) {
                context.operator("(")
                        .className(obj.location().replaceAll("_", " "))
                        .operator(") ")
                        .identifier(obj.path().replaceAll("_", " ").replaceAll("/", " -> "));
            } else {
                context.className(obj.location())
                        .operator(":")
                        .identifier(obj.path());
            }
        }
    });
    public static final Formatter<TextObject> TEXT_OBJECT = FormatterRegistry.register(new Formatter<>(TextObject.class, new Identifier("text_object")) {
        @Override
        public void format(TextObject obj, IFormatterContext context) {
            context.className(obj.getClass().getSimpleName())
                    .space()
                    .string("\"")
                    .stringEscaped(obj.getText())
                    .string("\"");
        }
    });

    public static final Formatter<MutableText> MUTABLE_COMPONENT = FormatterRegistry.register(new Formatter<>(MutableText.class, new Identifier("text_object/mutable")) {
        @Override
        public void format(MutableText obj, IFormatterContext context) {
            context.className("mutable text")
                    .space()
                    .other(obj.getText());
        }
    });

    public static final Formatter<Vec2d> VEC2D = FormatterRegistry.register(new Formatter<>(Vec2d.class, new Identifier("vector/2_double")) {
        @Override
        public void format(Vec2d obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec2f> VEC2F = FormatterRegistry.register(new Formatter<>(Vec2f.class, new Identifier("vector/2_float")) {
        @Override
        public void format(Vec2f obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec2i> VEC2I = FormatterRegistry.register(new Formatter<>(Vec2i.class, new Identifier("vector/2_int")) {
        @Override
        public void format(Vec2i obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec3d> VEC3D = FormatterRegistry.register(new Formatter<>(Vec3d.class, new Identifier("vector/3_double")) {
        @Override
        public void format(Vec3d obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec3f> VEC3F = FormatterRegistry.register(new Formatter<>(Vec3f.class, new Identifier("vector/3_float")) {
        @Override
        public void format(Vec3f obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec3i> VEC3I = FormatterRegistry.register(new Formatter<>(Vec3i.class, new Identifier("vector/3_int")) {
        @Override
        public void format(Vec3i obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec4d> VEC4D = FormatterRegistry.register(new Formatter<>(Vec4d.class, new Identifier("vector/4_double")) {
        @Override
        public void format(Vec4d obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec4f> VEC4F = FormatterRegistry.register(new Formatter<>(Vec4f.class, new Identifier("vector/4_float")) {
        @Override
        public void format(Vec4f obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });
    
    public static final Formatter<Vec4i> VEC4I = FormatterRegistry.register(new Formatter<>(Vec4i.class, new Identifier("vector/4_int")) {
        @Override
        public void format(Vec4i obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });

    public static final Formatter<Point> POINT = FormatterRegistry.register(new Formatter<>(Point.class, new Identifier("java/awt/point")) {
        @Override
        public void format(Point obj, IFormatterContext context) {
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
        }
    });

    public static final Formatter<Dimension> DIMENSION = FormatterRegistry.register(new Formatter<>(Dimension.class, new Identifier("java/awt/dimension")) {
        @Override
        public void format(Dimension obj, IFormatterContext context) {
            context.intValue(obj.width);
            context.space();
            context.operator("\u00D7");
            context.space();
            context.intValue(obj.height);
        }
    });

    public static final Formatter<Rectangle> RECTANGLE = FormatterRegistry.register(new Formatter<>(Rectangle.class, new Identifier("java/awt/rectangle")) {
        @Override
        public void format(Rectangle obj, IFormatterContext context) {
            context.operator("(");
            context.parameter("x", roundTo(obj.x, 5));
            context.separator();
            context.parameter("y", roundTo(obj.y, 5));
            context.operator(")");
            context.space();
            context.intValue(obj.width);
            context.space();
            context.operator("\u00D7");
            context.space();
            context.intValue(obj.height);
        }
    });

    public static final Formatter<EntityType> ENTITY_TYPE = FormatterRegistry.register(new Formatter<>(EntityType.class, new Identifier("entity_type")) {
        @Override
        public void format(EntityType obj, IFormatterContext context) {
            context.className("entity-type")
                    .space()
                    .other(obj.getId());
        }
    });
    public static final Formatter<EnvType> ENV_TYPE = FormatterRegistry.register(new Formatter<>(EnvType.class, new Identifier("env_type")) {
        @Override
        public void format(EnvType obj, IFormatterContext context) {
            context.enumConstant(obj);
        }
    });
    
    public static void initClass() {

    }

    private DebugFormatters() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    /**
     * Rounds to a given amount of decimal places.
     *
     * @param val the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    public static double roundTo(double val, int places) {
        return BigDecimal.valueOf(val).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Rounds to a given amount of decimal places.
     *
     * @param val the value to round.
     * @param places decimal places to round to. (This is inclusive)
     * @return the rounded value.
     */
    public static float roundTo(float val, int places) {
        return BigDecimal.valueOf(val).setScale(places, RoundingMode.HALF_UP).floatValue();
    }
}
