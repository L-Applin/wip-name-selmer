package ca.applin.selmer.typer;

import static org.junit.jupiter.api.Assertions.*;

import ca.applin.selmer.CompilerContext;

public class ScopeTest {
    public static final Scope TEST_SCOPE = new Scope("__test_scope__", new CompilerContext());
}