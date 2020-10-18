package org.checkerframework.dataflow.cfg.visualize;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.ControlFlowGraph;
import org.checkerframework.dataflow.cfg.UnderlyingAST;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGLambda;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGMethod;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGStatement;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.block.Block.BlockType;
import org.checkerframework.dataflow.cfg.block.ConditionalBlock;
import org.checkerframework.dataflow.cfg.block.SpecialBlock;
import org.checkerframework.dataflow.cfg.visualize.AbstractCFGVisualizer.VisualizeWhere;
import org.checkerframework.dataflow.expression.ArrayAccess;
import org.checkerframework.dataflow.expression.ClassName;
import org.checkerframework.dataflow.expression.FieldAccess;
import org.checkerframework.dataflow.expression.LocalVariable;
import org.checkerframework.dataflow.expression.MethodCall;
import org.checkerframework.javacutil.BugInCF;
import org.checkerframework.javacutil.UserError;

/** Generate a graph description in the DOT language of a control graph. */
@SuppressWarnings("nullness:initialization.fields.uninitialized") // uses init method
public class DOTCFGVisualizer<
                V extends AbstractValue<V>, S extends Store<S>, T extends TransferFunction<V, S>>
        extends AbstractCFGVisualizer<V, S, T> {

    /** The output directory. */
    protected String outDir;

    /** The (optional) checker name. Used as a part of the name of the output dot file. */
    protected @Nullable String checkerName;

    /** Mapping from class/method representation to generated dot file. */
    protected @OrderNonDet Map<String, String> generated;

    /** Terminator for lines that are left-justified. */
    protected static final String leftJustifiedTerminator = "\\l";

    @Override
    @SuppressWarnings("nullness") // assume arguments are set correctly
    public void init(@OrderNonDet Map<String, Object> args) {
        super.init(args);
        this.outDir = (String) args.get("outdir");
        if (this.outDir == null) {
            throw new BugInCF(
                    "outDir should never be null, provide it in args when calling DOTCFGVisualizer.init(args).");
        }
        this.checkerName = (String) args.get("checkerName");
        this.generated = new HashMap<>();
    }

    @Override
    public @PolyDet String getSeparator(@PolyDet DOTCFGVisualizer<V, S, T> this) {
        return leftJustifiedTerminator;
    }

    @Override
    public @Nullable @OrderNonDet Map<String, Object> visualize(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet ControlFlowGraph cfg,
            @PolyDet Block entry,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {

        String dotGraph = visualizeGraph(cfg, entry, analysis);
        String dotFileName = dotOutputFileName(cfg.underlyingAST);

        try {
            FileWriter fStream = new FileWriter(dotFileName);
            BufferedWriter out = new BufferedWriter(fStream);
            @SuppressWarnings("determinism") // true positive (debug output): hashCode
            @Det String tmp = dotGraph;
            out.write(tmp);
            out.close();
        } catch (IOException e) {
            throw new UserError("Error creating dot file (is the path valid?): " + dotFileName, e);
        }

        Map<@Det String, @NonDet Object> res = new HashMap<>();
        res.put("dotFileName", dotFileName);

        return res;
    }

    @SuppressWarnings("keyfor:enhancedfor.type.incompatible")
    @Override
    public @PolyDet String visualizeNodes(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet Set<@PolyDet Block> blocks,
            @PolyDet ControlFlowGraph cfg,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {

        StringBuilder sbDotNodes = new StringBuilder();

        IdentityHashMap<@Det Block, @Det List<Integer>> processOrder = getProcessOrder(cfg);

        // Definition of all nodes including their labels.
        for (@KeyFor("processOrder") Block v : blocks) {
            sbDotNodes.append("    ").append(v.getUid()).append(" [");
            if (v.getType() == BlockType.CONDITIONAL_BLOCK) {
                sbDotNodes.append("shape=polygon sides=8 ");
            } else if (v.getType() == BlockType.SPECIAL_BLOCK) {
                sbDotNodes.append("shape=oval ");
            } else {
                sbDotNodes.append("shape=rectangle ");
            }
            sbDotNodes.append("label=\"");
            if (verbose) {
                sbDotNodes
                        .append(getProcessOrderSimpleString(processOrder.get(v)))
                        .append(getSeparator());
            }
            String strBlock = visualizeBlock(v, analysis);
            if (strBlock.length() == 0) {
                if (v.getType() == BlockType.CONDITIONAL_BLOCK) {
                    // The footer of the conditional block.
                    sbDotNodes.append("\"];");
                } else {
                    // The footer of the block which has no content and is not a special or
                    // conditional block.
                    sbDotNodes.append("?? empty ??\"];");
                }
            } else {
                sbDotNodes.append(strBlock).append("\"];");
            }
        }
        return sbDotNodes.toString();
    }

    @Override
    protected @PolyDet String visualizeEdge(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet Object sId,
            @PolyDet Object eId,
            @PolyDet String flowRule) {
        return "    " + format(sId) + " -> " + format(eId) + " [label=\"" + flowRule + "\"];";
    }

    @Override
    public @PolyDet String visualizeBlock(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {
        return super.visualizeBlockHelper(bb, analysis, getSeparator());
    }

    @Override
    public @PolyDet String visualizeSpecialBlock(
            @PolyDet DOTCFGVisualizer<V, S, T> this, @PolyDet SpecialBlock sbb) {
        return super.visualizeSpecialBlockHelper(sbb);
    }

    @Override
    public @PolyDet String visualizeConditionalBlock(
            @PolyDet DOTCFGVisualizer<V, S, T> this, @PolyDet ConditionalBlock cbb) {
        // No extra content in DOT output.
        return "";
    }

    @Override
    public @PolyDet String visualizeBlockTransferInputBefore(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.BEFORE, bb, analysis, getSeparator());
    }

    @Override
    public @PolyDet String visualizeBlockTransferInputAfter(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.AFTER, bb, analysis, getSeparator());
    }

    /**
     * Create a dot file and return its name.
     *
     * @param ast an abstract syntax tree
     * @return the file name used for DOT output
     */
    protected String dotOutputFileName(UnderlyingAST ast) {
        StringBuilder srcLoc = new StringBuilder();
        StringBuilder outFile = new StringBuilder(outDir);

        outFile.append("/");

        if (ast.getKind() == UnderlyingAST.Kind.ARBITRARY_CODE) {
            CFGStatement cfgStatement = (CFGStatement) ast;
            String clsName = cfgStatement.getClassTree().getSimpleName().toString();
            outFile.append(clsName);
            outFile.append("-initializer-");
            outFile.append(ast.getUid());

            srcLoc.append("<");
            srcLoc.append(clsName);
            srcLoc.append("::initializer::");
            srcLoc.append(((JCTree) cfgStatement.getCode()).pos);
            srcLoc.append(">");
        } else if (ast.getKind() == UnderlyingAST.Kind.METHOD) {
            CFGMethod cfgMethod = (CFGMethod) ast;
            String clsName = cfgMethod.getSimpleClassName();
            String methodName = cfgMethod.getMethodName();
            StringJoiner params = new StringJoiner(",");
            for (VariableTree tree : cfgMethod.getMethod().getParameters()) {
                @SuppressWarnings(
                        "determinism" // all known implementations have @Det toString method: trees
                )
                @Det String tmp = tree.getType().toString();
                params.add(tmp);
            }
            outFile.append(clsName);
            outFile.append("-");
            outFile.append(methodName);
            if (params.length() != 0) {
                outFile.append("-");
                outFile.append(params);
            }

            srcLoc.append("<");
            srcLoc.append(clsName);
            srcLoc.append("::");
            srcLoc.append(methodName);
            srcLoc.append("(");
            srcLoc.append(params);
            srcLoc.append(")::");
            srcLoc.append(((JCTree) cfgMethod.getMethod()).pos);
            srcLoc.append(">");
        } else if (ast.getKind() == UnderlyingAST.Kind.LAMBDA) {
            CFGLambda cfgLambda = (CFGLambda) ast;
            String clsName = cfgLambda.getSimpleClassName();
            String methodName = cfgLambda.getMethodname();
            @SuppressWarnings("determinism") // true positive (debug output): hashCode
            @Det int hashCode = cfgLambda.getCode().hashCode();
            outFile.append(clsName);
            outFile.append("-");
            outFile.append(methodName);
            outFile.append("-");
            outFile.append(hashCode);

            srcLoc.append("<");
            srcLoc.append(clsName);
            srcLoc.append("::");
            srcLoc.append(methodName);
            srcLoc.append("(");
            @SuppressWarnings("determinism") // all known implementations have @Det toString method
            @Det String tmp = cfgLambda.getMethod().getParameters().toString();
            srcLoc.append(tmp);
            srcLoc.append(")::");
            srcLoc.append(((JCTree) cfgLambda.getCode()).pos);
            srcLoc.append(">");
        } else {
            throw new BugInCF("Unexpected AST kind: " + ast.getKind() + " value: " + ast);
        }
        if (checkerName != null && !checkerName.isEmpty()) {
            outFile.append('-');
            outFile.append(checkerName);
        }
        outFile.append(".dot");

        // make path safe for Windows
        String outFileName = outFile.toString().replace("<", "_").replace(">", "");

        generated.put(srcLoc.toString(), outFileName);

        return outFileName;
    }

    @Override
    protected @PolyDet String format(@PolyDet DOTCFGVisualizer<V, S, T> this, @PolyDet Object obj) {
        return escapeDoubleQuotes(obj);
    }

    @Override
    public @PolyDet String visualizeStoreThisVal(
            @PolyDet DOTCFGVisualizer<V, S, T> this, @PolyDet V value) {
        return storeEntryIndent + "this > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreLocalVar(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet LocalVariable localVar,
            @PolyDet V value) {
        return storeEntryIndent + localVar + " > " + escapeDoubleQuotes(value);
    }

    @Override
    public @PolyDet String visualizeStoreFieldVal(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet FieldAccess fieldAccess,
            @PolyDet V value) {
        return storeEntryIndent + fieldAccess + " > " + escapeDoubleQuotes(value);
    }

    @Override
    public @PolyDet String visualizeStoreArrayVal(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet ArrayAccess arrayValue,
            @PolyDet V value) {
        return storeEntryIndent + arrayValue + " > " + escapeDoubleQuotes(value);
    }

    @Override
    public @PolyDet String visualizeStoreMethodVals(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet MethodCall methodCall,
            @PolyDet V value) {
        return storeEntryIndent + escapeDoubleQuotes(methodCall) + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreClassVals(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet ClassName className,
            @PolyDet V value) {
        return storeEntryIndent + className + " > " + escapeDoubleQuotes(value);
    }

    @Override
    public @PolyDet String visualizeStoreKeyVal(
            @PolyDet DOTCFGVisualizer<V, S, T> this,
            @PolyDet String keyName,
            @PolyDet Object value) {
        return storeEntryIndent + keyName + " = " + value;
    }

    /**
     * Escape the double quotes from the input String, replacing {@code "} by {@code \"}.
     *
     * @param str the string to be escaped
     * @return the escaped version of the string
     */
    private static @PolyDet String escapeDoubleQuotes(final @PolyDet String str) {
        return str.replace("\"", "\\\"");
    }

    /**
     * Escape the double quotes from the string representation of the given object.
     *
     * @param obj an object
     * @return an escaped version of the string representation of the object
     */
    private static @PolyDet String escapeDoubleQuotes(final @PolyDet Object obj) {
        return escapeDoubleQuotes(String.valueOf(obj));
    }

    /**
     * Write a file {@code methods.txt} that contains a mapping from source code location to
     * generated dot file.
     */
    @Override
    public void shutdown() {
        try {
            // Open for append, in case of multiple sub-checkers.
            FileWriter fstream = new FileWriter(outDir + "/methods.txt", true);
            BufferedWriter out = new BufferedWriter(fstream);
            for (Map.Entry<String, String> kv : generated.entrySet()) {
                @SuppressWarnings(
                        "determinism") // process is order insensitive: order of writing files
                // doesn't matter
                Map.@Det Entry<String, String> tmp = kv;
                out.write(tmp.getKey());
                out.append("\t");
                out.write(tmp.getValue());
                out.append(lineSeparator);
            }
            out.close();
        } catch (IOException e) {
            throw new UserError(
                    "Error creating methods.txt file in: " + outDir + "; ensure the path is valid",
                    e);
        }
    }

    @Override
    protected String visualizeGraphHeader(@PolyDet DOTCFGVisualizer<V, S, T> this) {
        return "digraph {" + lineSeparator;
    }

    @Override
    protected String visualizeGraphFooter(@PolyDet DOTCFGVisualizer<V, S, T> this) {
        return "}";
    }
}
