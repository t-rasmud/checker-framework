package org.checkerframework.checker.determinism;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SupportedLintOptions;

/**
 * The Determinism Checker prevents non-determinism in single-threaded programs. It enables a
 * programmer to indicate which computations should be the same across runs of a program, and then
 * verifies that property.
 *
 * @checker_framework.manual #determinism-checker Determinism Checker
 */
@SupportedLintOptions({
    // disables "invalid.typ.on.conditional" errors. Use this option to reduce false positives
    // until we fix type refinement of every statement after a conditional check.
    "disableconditionaltypecheck"
})
public class DeterminismChecker extends BaseTypeChecker {}
