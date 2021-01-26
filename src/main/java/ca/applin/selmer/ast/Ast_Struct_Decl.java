package ca.applin.selmer.ast;

import java.util.List;
import java.util.stream.Collectors;

public class Ast_Struct_Decl extends Ast_Declaration {
    public String                  name;
    public List<Ast_Struct_Member> struct_members;

    public Ast_Struct_Decl(String name, List<Ast_Struct_Member> struct_members) {
        this.name = name;
        this.struct_members = struct_members;
    }

    @Override
    public String toStringIndented(int level) {
        return "%sType struct: %s\n%s".formatted(
                DEFAULT_DEPTH_PER_LEVEL.repeat(level),
                name,
                struct_members.stream().map(m -> m.toStringIndented(level + 1)).collect(Collectors.joining("\n")));
    }

    @Override
    public String toString() {
        return ("(Struct %s (members "
                + struct_members.stream().map(Ast_Struct_Member::toString).collect(Collectors.joining(" "))
                + "))").formatted(name);
    }
}
