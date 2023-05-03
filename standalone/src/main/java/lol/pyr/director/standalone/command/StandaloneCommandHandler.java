package lol.pyr.director.standalone.command;

import lol.pyr.director.common.command.CommandExecutionException;
import lol.pyr.director.common.command.CommandHandler;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused", "RedundantThrows"})
public interface StandaloneCommandHandler extends CommandHandler<StandaloneCommandContext> {
    void run(StandaloneCommandContext context) throws CommandExecutionException;

    default List<String> suggest(StandaloneCommandContext context) throws CommandExecutionException {
        return Collections.emptyList();
    }
}