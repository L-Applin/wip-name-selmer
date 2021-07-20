package ca.applin.selmer.typer;

import ca.applin.selmer.CompilerContext;
import ca.applin.selmer.ast.Ast_Variable_Decl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scope {

    private String name;
    private Scope parent;
    private Set<Type> declaredTypes;
    private Map<String, Ast_Variable_Decl> variableDecl;
    private CompilerContext compilerContext;

    public Scope(String name, CompilerContext compilerContext) {
        this.name = name;
        this.declaredTypes = new HashSet<>();
        this.variableDecl = new HashMap<>();
        this.compilerContext = compilerContext;
    }

    public Scope(String name, Scope parent, CompilerContext compilerContext) {
        this.name = name;
        this.parent = parent;
        this.declaredTypes = new HashSet<>();
        this.variableDecl = new HashMap<>();
        this.compilerContext = compilerContext;
    }

    public void addVariable(Ast_Variable_Decl variableDecl) {
        ensure_variable_not_exist(variableDecl);
        this.variableDecl.put(variableDecl.identifier, variableDecl);
    }

    private void ensure_variable_not_exist(Ast_Variable_Decl variableDecl) {
        Type varType = variableDecl.type;
        if (varType.is_know && declaredTypes.contains(varType)) {

        }
        if (parent != null )
            parent.ensure_variable_not_exist(variableDecl);
    }


    public boolean containsVariable(String varName) {
        return variableDecl.containsKey(varName);
    }
}
