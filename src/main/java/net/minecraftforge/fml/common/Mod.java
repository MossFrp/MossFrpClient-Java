package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//感谢CustomSkinLoader群大佬为forge同时高低版本支持的解答
//本部分代码参考自CustomSkinLoader
//https://github.com/MinecraftForge/MinecraftForge/blob/1.13.x/src/main/java/net/minecraftforge/fml/common/Mod.java
//https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/fml/common/Mod.java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    String value();

    String modid() default "";

    String name() default "";

    String version() default "";

    String dependencies() default "";
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface EventHandler {}
}