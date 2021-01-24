package ca.applin.selmer.ast;

public class Ast_Compiler_Instruction extends Ast_Declaration {
    public String instruction;

    @Override
    public String toString() {
        return "(Instruction %s".formatted(instruction);
    }
}
