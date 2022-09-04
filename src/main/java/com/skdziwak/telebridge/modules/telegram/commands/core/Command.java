package com.skdziwak.telebridge.modules.telegram.commands.core;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.telegram.commands.core.scope.CommandScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope(CommandScope.COMMAND_SCOPE)
public @interface Command {
    String name();
    String[] alias() default {};
    String[] arguments() default {};
    LanguageKey[] description() default {};
    PermissionLevel permission() default PermissionLevel.ALL;
    boolean requiresBridge() default false;
}
