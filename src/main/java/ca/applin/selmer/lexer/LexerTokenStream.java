package ca.applin.selmer.lexer;

import ca.applin.selmer.lexer.LexerToken.Lexer_Token_Type;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LexerTokenStream implements Iterable<LexerToken> {

    public List<LexerToken> tokens;
    int current_index;
    public boolean has_been_surrounded = false;

    public LexerTokenStream(List<LexerToken> tokens) {
        this.tokens = tokens;
    }

    public void put(LexerToken token) {
        tokens.add(token);
    }

    public LexerToken current() {
        return tokens.get(current_index);
    }

    public LexerToken peek(int n) {
        return tokens.get(current_index + n);
    }

    public LexerToken peek() {
        return this.peek(0);
    }

    public LexerToken peek_next() {
        return tokens.get(current_index + 1);
    }

    public LexerToken take_one() {
        return tokens.get(current_index++);
    }

    public List<LexerToken> take_many(int n) {
        return tokens.subList(current_index, current_index + n);
    }

    public void advance(int n) {
        current_index += n;
    }

    public void advance() {
        advance(1);
    }

    public List<LexerToken> take_while(Predicate<LexerToken> predicate) {
        return take_while(predicate, true);
    }

    // replace by sublist
    public List<LexerToken> take_while(Predicate<LexerToken> pred, boolean advance) {
        List<LexerToken> toks = new ArrayList<>();
        for (int i = current_index; i < tokens.size(); i++) {
            LexerToken tok = tokens.get(i);
            if (pred.test(tok)) {
                if (advance) {
                    current_index++;
                }
                toks.add(tok);
            } else {
                break;
            }
        }
        return toks;
    }

    public void advance_if(Predicate<LexerToken> test) {
        if (end()) {
            return;
        }
        if (test.test(current())) {
            advance();
        }
    }

    public void log_all_file(PrintStream printStream) {
        tokens.forEach(printStream::println);
//        Map<String, List<LexerToken>> ts = this.tokens.stream()
//                .collect(Collectors.groupingBy(t -> new File(t.filename).getName()));
//        ts.forEach(log_for_file(printStream));
    }

    private BiConsumer<String, List<LexerToken>> log_for_file(PrintStream printStream) {
        return (String filename, List<LexerToken> toks) -> {
            printStream.printf("Tokens for file %s%n", filename);
            toks.stream()
                    .map(t -> "%s:%d:%d".formatted(filename, t.line, t.col))
                    .map(String::length)
                    .max(Integer::compareTo)
                    .ifPresent(i -> toks.forEach(t -> {
                        String info = "%s:%d:%d".formatted(filename, t.line, t.col);
                        if (info.length() < i) {
                            int pad = i - info.length();
                            info += " ".repeat(pad);
                        }
                        printStream.println(info + " %s:[%s]".formatted(t.token_type.name(), t.value.equals("\n") ? "\\n" : t.value));
                    }));
        };
    }

    public boolean end() {
        return current_index >= tokens.size();
    }

    public void assert_current(Lexer_Token_Type token_type, String msg) {
        assert current().token_type == token_type : msg;
    }

    public void assert_current(Lexer_Token_Type token_type) {
        assert current().token_type == token_type;
    }

    @Override
    public Iterator<LexerToken> iterator() {
        return tokens.iterator();
    }

    @Override
    public String toString() {
        return tokens.toString();
    }

    public List<LexerTokenStream> split() {
        List<LexerTokenStream> splits = new ArrayList<>();
        int last_split_index = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).token_type == Lexer_Token_Type.SEMI_COLON) {
                ArrayList<LexerToken> copy = new ArrayList<>();
                for (int j = last_split_index; j < i; j++) {
                    copy.add(tokens.get(j));
                }
                splits.add(new LexerTokenStream(copy));
                last_split_index = i + 1;
            }
        }
        return splits;
    }

    public void skip_new_lines() {
        while (!end() && current().token_type == Lexer_Token_Type.NEW_LINE) advance();
    }
}
