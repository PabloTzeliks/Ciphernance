package io.ciphernance.identity.infrastructure.mediator;

import io.ciphernance.identity.application.mediator.*;
import io.ciphernance.identity.application.exception.HandlerNotFoundException;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MediatorImpl implements Mediator {

    private final Map<Class<?>, CommandHandler<?, ?>> commandHandlerMap;
    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlerMap;

    public MediatorImpl(List<CommandHandler<?, ?>> commandHandlers,
                        List<QueryHandler<?, ?>> queryHandlers) {

        this.commandHandlerMap = new HashMap<>();
        this.queryHandlerMap = new HashMap<>();

        commandHandlers.forEach(handler ->
                commandHandlerMap.put(resolveCommandType(handler), handler)
        );

        queryHandlers.forEach(handler ->
                queryHandlerMap.put(resolveQueryType(handler), handler)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R send(Command<R> command) {
        Class<?> commandType = command.getClass();

        CommandHandler<Command<R>, R> handler =
                (CommandHandler<Command<R>, R>) commandHandlerMap.get(commandType);

        if (handler == null) {
            throw new HandlerNotFoundException(commandType, "command");
        }

        return handler.handle(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        Class<?> queryType = query.getClass();

        QueryHandler<Query<R>, R> handler =
                (QueryHandler<Query<R>, R>) queryHandlerMap.get(queryType);

        if (handler == null) {
            throw new HandlerNotFoundException(queryType, "query");
        }

        return handler.handle(query);
    }

    private Class<?> resolveCommandType(CommandHandler<?, ?> handler) {
        return resolveFirstGenericType(handler.getClass(), CommandHandler.class);
    }

    private Class<?> resolveQueryType(QueryHandler<?, ?> handler) {
        return resolveFirstGenericType(handler.getClass(), QueryHandler.class);
    }

    private Class<?> resolveFirstGenericType(Class<?> handlerClass, Class<?> interfaceClass) {
        Type[] interfaces = handlerClass.getGenericInterfaces();

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType parameterized) {
                if (parameterized.getRawType().equals(interfaceClass)) {
                    return (Class<?>) parameterized.getActualTypeArguments()[0];
                }
            }
        }

        throw new IllegalStateException(
                "Could not resolve generic type for: " + handlerClass.getSimpleName()
        );
    }
}