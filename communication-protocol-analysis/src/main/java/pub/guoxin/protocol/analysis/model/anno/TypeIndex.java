package pub.guoxin.protocol.analysis.model.anno;

import pub.guoxin.protocol.analysis.conf.convert.TypeConvert;

import java.lang.annotation.*;

/**
 * 数据段类型 - 用于标记数据段类型
 * <p>
 * Created by guoxin on 18-6-13.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeIndex {
    Class<? extends TypeConvert> convert() default TypeConvert.class;
}