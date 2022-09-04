package com.skdziwak.telebridge.modules.telegram.commands.core;

import com.google.common.collect.Streams;
import com.skdziwak.telebridge.TelebridgeApplication;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Stream;

@Component
public class TelegramCommands {
    private final Map<String, Class<? extends AbstractCommand>> commandsMap;
    private final List<Class<? extends AbstractCommand>> commands;

    public TelegramCommands() {
        this.commandsMap = new HashMap<>();
        this.commands = new LinkedList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Command.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(TelebridgeApplication.BASE_PACKAGE)) {
            Class<?> commandClass;
            try {
                commandClass = TelegramCommands.class.getClassLoader().loadClass(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            assert AbstractCommand.class.isAssignableFrom(commandClass) : "All @Command annotated classes should extend AbstractCommand";
            @SuppressWarnings("unchecked")
            Class<? extends AbstractCommand> abstractCommandClass = (Class<? extends AbstractCommand>) commandClass;
            Command annotation = commandClass.getAnnotation(Command.class);

            Streams.concat(Stream.of(annotation.name()), Arrays.stream(annotation.alias()))
                    .forEach(alias -> {
                        assert !commandsMap.containsKey(alias) :
                                "Alias conflict detected between classes " + commandClass.getName() +
                                        " and " + commandsMap.get(alias).getName();
                        commandsMap.put(alias, abstractCommandClass);
                    });
            commands.add(abstractCommandClass);
        }
    }

    public Optional<Class<? extends AbstractCommand>> findCommand(String name) {
        return Optional.ofNullable(commandsMap.get(name));
    }

    public Collection<Class<? extends AbstractCommand>> getAllCommands() {
        return commands;
    }
}
