package ca.applin.selmer.ast;

public class Ast_Compiler_Instruction extends Ast_Statement {
    public String instruction;

    public Ast_Compiler_Instruction(String instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "(Instruction %s".formatted(instruction);
    }
}
