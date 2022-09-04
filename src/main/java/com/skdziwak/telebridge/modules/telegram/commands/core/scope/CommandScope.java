package com.skdziwak.telebridge.modules.telegram.commands.core.scope;


import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandScope implements Scope {
    public static final String COMMAND_SCOPE = "telegram_command_scope";
    private static final ThreadLocal<BeansStore> stores = new ThreadLocal<>();

    @NotNull
    @Override
    public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        BeansStore beanStore = getBeanStore();
        if(!beanStore.scopedObjects.containsKey(name)) {
            beanStore.scopedObjects.put(name, objectFactory.getObject());
        }
        return beanStore.scopedObjects.get(name);
    }

    @Override
    public Object remove(@NotNull String name) {
        BeansStore beanStore = getBeanStore();
        beanStore.destructionCallbacks.remove(name);
        return beanStore.scopedObjects.get(name);
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable runnable) {
        BeansStore beanStore = getBeanStore();
        beanStore.destructionCallbacks.put(name, runnable);
    }

    @Override
    public Object resolveContextualObject(@NotNull String name) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    public static void runInContext(CommandContext commandContext, Runnable runnable) {
        try {
            stores.set(new BeansStore(commandContext));
            runnable.run();
        } finally {
            stores.remove();
        }
    }

    static BeansStore getBeanStore() {
        return Objects.requireNonNull(stores.get(), "CommandScope is not available");
    }

    public static class BeansStore {
        private final Map<String, Object> scopedObjects = Collections.synchronizedMap(new HashMap<>());
        private final Map<String, Runnable> destructionCallbacks = Collections.synchronizedMap(new HashMap<>());
        private final CommandContext commandContext;
        private BeansStore(CommandContext commandContext) {
            this.commandContext = commandContext;
        }

        public CommandContext getCommandContext() {
            return commandContext;
        }
    }
}
