package org.checkerframework.checker.determinism;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.StubFiles;
import org.checkerframework.framework.source.SupportedLintOptions;

/**
 * The Determinism Checker prevents non-determinism in single-threaded programs. It enables a
 * programmer to indicate which computations should be the same across runs of a program, and then
 * verifies that property.
 *
 * @checker_framework.manual #determinism-checker Determinism Checker
 */
@SupportedLintOptions({
    // enables "invalid.typ.on.conditional" errors. This is currently disabled and should be enabled
    // once we fix type refinement of every statement after a conditional check.
    "enableconditionaltypecheck"
})
@StubFiles("junit.astub")
public class DeterminismChecker extends BaseTypeChecker {}
