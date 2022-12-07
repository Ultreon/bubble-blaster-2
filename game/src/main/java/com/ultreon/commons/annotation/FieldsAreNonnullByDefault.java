package com.ultreon.commons.annotation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation can be applied to a package, class or method to indicate that
 * the method parameters in that element are nonnull by default unless there is:
 * <ul>
 * <li>An explicit nullness annotation
 * <li>The method overrides a method in a superclass (in which case the
 * annotation create the corresponding parameter in the superclass applies)
 * <li>There is a default parameter annotation (like {@link ParametersAreNullableByDefault})
 * applied to a more tightly nested element.
 * </ul>
 *
 * @see Nonnull
 */
@Documented
@Nonnull
@TypeQualifierDefault(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsAreNonnullByDefault {
}
