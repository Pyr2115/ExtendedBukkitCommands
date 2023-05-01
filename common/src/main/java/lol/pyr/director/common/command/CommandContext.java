package lol.pyr.director.common.command;

import lol.pyr.director.common.message.Message;
import lol.pyr.director.common.parse.ParserType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandContext<S> {
    private final CommandManager<S> manager;
    private final S sender;
    private final String label;
    private final Deque<String> args;

    private String usage;
    private Message<S> lastMsg;

    public CommandContext(CommandManager<S> manager, S sender, String label, Deque<String> args) {
        this.manager = manager;
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    public S getSender() {
        return sender;
    }

    public String getLabel() {
        return label;
    }

    public CommandManager<S> getManager() {
        return manager;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    protected void setLastMessage(Message<S> msg) {
        this.lastMsg = msg;
    }

    public Message<S> getLastMessage() {
        return lastMsg;
    }

    public <T> T parse(Class<T> type) throws CommandExecutionException {
        try {
            ParserType<T, S> parser = manager.getParser(type);
            setLastMessage(parser);
            return parser.parse(args);
        } catch (NoSuchElementException exception) {
            throw new CommandExecutionException();
        }
    }

    public void ensureArgsNotEmpty() throws CommandExecutionException {
        setLastMessage(manager.getNotEnoughArgumentsMessage());
        if (argSize() == 0) throw new CommandExecutionException();
    }

    public String dumpAllArgs() {
        return String.join(" ", args);
    }

    public String popString() throws CommandExecutionException {
        setLastMessage(manager.getNotEnoughArgumentsMessage());
        if (argSize() == 0) throw new CommandExecutionException();
        return args.removeFirst();
    }

    public int argSize() {
        return args.size();
    }

    public boolean matchSuggestion(String... args) {
        if (args.length + 1 != this.args.size()) return false;
        if (args.length == 0) return true;
        Deque<String> clone = new ArrayDeque<>(this.args);
        for (String s : args) {
            String popped = clone.pop();
            if (!s.equals("*") && !s.equalsIgnoreCase(popped)) return false;
        }
        return true;
    }

    public List<String> suggestLiteral(String... args) throws CommandExecutionException {
        return suggestStream(Stream.of(args));
    }

    public List<String> suggestEnum(Enum<?>[] e) throws CommandExecutionException {
        return suggestStream(Arrays.stream(e).map(Enum::name));
    }

    public List<String> suggestCollection(Collection<String> args) throws CommandExecutionException {
        return suggestStream(args.stream());
    }

    public List<String> suggestStream(Stream<String> args) throws CommandExecutionException {
        ensureArgsNotEmpty();
        final String input = this.args.removeLast().toLowerCase();
        return args.filter(s -> s.toLowerCase().startsWith(input)).collect(Collectors.toList());
    }

    public void halt(Message<S> message) throws CommandExecutionException {
        setLastMessage(message);
        throw new CommandExecutionException();
    }

    public void halt() throws CommandExecutionException {
        halt(null);
    }
}