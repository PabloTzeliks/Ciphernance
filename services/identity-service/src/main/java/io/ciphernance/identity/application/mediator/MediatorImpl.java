package io.ciphernance.identity.application.mediator;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Component
public class MediatorImpl implements Mediator {

    private final ApplicationContext context;

    public MediatorImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R send(Command<R> command) {
        Class<?> commandType = command.getClass();

        CommandHandler handler = context.getBeansOfType(CommandHandler.class)
                .values()
                .stream()
                .filter(h -> resolvesCommand(h, commandType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for command: " + commandType.getSimpleName()
                ));

        return (R) handler.handle(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(Query<R> query) {
        Class<?> queryType = query.getClass();

        QueryHandler handler = context.getBeansOfType(QueryHandler.class)
                .values()
                .stream()
                .filter(h -> resolvesQuery(h, queryType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for query: " + queryType.getSimpleName()
                ));

        return (R) handler.handle(query);
    }

    private boolean resolvesCommand(CommandHandler handler, Class<?> commandType) {
        Type[] genericInterfaces = handler.getClass().getGenericInterfaces();

        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType parameterized) {
                if (parameterized.getRawType().equals(CommandHandler.class)) {

                    Type commandGenericType = parameterized.getActualTypeArguments()[0];
                    return commandGenericType.equals(commandType);
                }
            }
        }
        return false;
    }

    private boolean resolvesQuery(QueryHandler handler, Class<?> queryType) {
        Type[] genericInterfaces = handler.getClass().getGenericInterfaces();

        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType parameterized) {
                if (parameterized.getRawType().equals(QueryHandler.class)) {

                    Type queryGenericType = parameterized.getActualTypeArguments()[0];
                    return queryGenericType.equals(queryType);
                }
            }
        }
        return false;
    }
}
