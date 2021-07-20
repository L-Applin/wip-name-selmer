package ca.applin.selmer.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.applin.selmer.ast.Ast_Binop;
import ca.applin.selmer.ast.Ast_Expression;
import ca.applin.selmer.ast.Ast_Literral_Expr;
import ca.applin.selmer.ast.Ast_Operator.Operator;
import ca.applin.selmer.ast.Ast_Tuple_Litteral;
import ca.applin.selmer.ast.Ast_Variable_Decl;
import ca.applin.selmer.typer.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VariableDeclarationTest {

    @Test
    public void simpleVariableDeclaration() {
        String expr = "x: Int = 5;";
        Ast_Variable_Decl ast = (Ast_Variable_Decl) Parser.parseString(expr);
        assertEquals("x", ast.identifier);
        assertEquals(Type.INT, ast.type);
        assertTrue(ast.initialization.isJust());
        Ast_Literral_Expr litteral = (Ast_Literral_Expr) ast.initialization.getOrElse(null);
        assertNotNull(litteral);
        assertEquals("5", litteral.litteral_value);
        assertTrue(ast.scope.containsVariable("x"));

        expr = "anInt: Int = 1 + 2;";
        ast = (Ast_Variable_Decl) Parser.parseString(expr);
        assertEquals("anInt", ast.identifier);
        assertEquals(Type.INT, ast.type);
        assertTrue(ast.initialization.isJust());
        Ast_Binop binop = (Ast_Binop) ast.initialization.getOrElse(null);
        assertNotNull(litteral);
        assertEquals(binop.operator, Operator.PLUS);
        assertEquals("1", ((Ast_Literral_Expr)binop.left).litteral_value);
        assertEquals("2", ((Ast_Literral_Expr)binop.right).litteral_value);
        assertTrue(ast.scope.containsVariable("anInt"));

        expr = "anotherInt: Int = (1 + 2) * 3;";
        ast = (Ast_Variable_Decl) Parser.parseString(expr);
        assertEquals("anotherInt", ast.identifier);
        assertEquals(Type.INT, ast.type);
        assertTrue(ast.initialization.isJust());
        binop = (Ast_Binop) ast.initialization.getOrElse(null);
        assertNotNull(litteral);
        assertEquals(Operator.TIMES, binop.operator);
        assertEquals("3", ((Ast_Literral_Expr)binop.right).litteral_value);
        Ast_Binop left_binop = (Ast_Binop) binop.left;
        assertEquals(Operator.PLUS, left_binop.operator);
        assertEquals("1", ((Ast_Literral_Expr)left_binop.left).litteral_value);
        assertEquals("2", ((Ast_Literral_Expr)left_binop.right).litteral_value);
        assertTrue(ast.scope.containsVariable("anotherInt"));

    }

    @Test
    public void tupleVariableDeclaration() {
        String expr = "x: (Int, Int) = (1, 2);";
        Ast_Variable_Decl ast = (Ast_Variable_Decl) Parser.parseString(expr);
        assertEquals("x", ast.identifier);
        assertEquals(Type.tuple(Type.INT, Type.INT), ast.type);
        assertTrue(ast.initialization.isJust());
        Ast_Tuple_Litteral litteral = (Ast_Tuple_Litteral) ast.initialization.getOrElse(null);
        assertNotNull(litteral);
        assertTrue(ast.scope.containsVariable("x"));
        assertEquals(2, litteral.items.size());
        Ast_Literral_Expr one = (Ast_Literral_Expr) litteral.items.get(0);
        Ast_Literral_Expr two = (Ast_Literral_Expr) litteral.items.get(1);
        assertEquals("1", one.litteral_value);
        assertEquals("2", two.litteral_value);
    }

}
