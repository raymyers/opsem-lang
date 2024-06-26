package ai.mender.opsem;

import ast.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import ai.mender.opsem.generated.OpSemParser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class OpSemTransformer {

    public Program transformStart(OpSemParser.StartContext ctx ) {
        var semRuleBlocks = ctx.block().stream().filter(b -> b.semRule() != null);

        List<SemRule> rules = semRuleBlocks
                .map(ai.mender.opsem.generated.OpSemParser.BlockContext::semRule)
                .map(this::transformSemRule)
                .collect(Collectors.toUnmodifiableList());
        var latexBlocks = ctx.block().stream().filter(b -> b.latexBlock() != null);
        return new Program(rules, findAllLatexRenderings(latexBlocks));
    }

    private List<LatexRendering> findAllLatexRenderings(Stream<OpSemParser.BlockContext> latexBlocks) {
        List<LatexRendering> latexRenderings = latexBlocks
                .map(OpSemParser.BlockContext::latexBlock)
                .flatMap(b -> b.latexRendering().stream())
                .map(this::transformLatexRendering)
                .collect(Collectors.toUnmodifiableList());
        return latexRenderings;
    }

    private LatexRendering transformLatexRendering(OpSemParser.LatexRenderingContext ctx) {
        List<TerminalNode> varNodes = ctx.VARIABLE();
        if (varNodes.isEmpty()) {
            // Grammar should forbid this
            throw new RuntimeException("No label found in LaTeX Rendering");
        }
        var label = varNodes.get(0).getText();
        List<String> params = varNodes.subList(1, varNodes.size()).stream().map(TerminalNode::getText).toList();
        String rendering = unwrapQuotes(ctx.StringLiteral().getText());
        return new LatexRendering(label, params, rendering);
    }

    private static String unwrapQuotes(String text) {
        String leadingQuote = "^\"";
        String endingQuote = "\"$";
        return text.replaceFirst(leadingQuote, "")
                .replaceFirst(endingQuote, "");
    }

    public SemRule transformSemRule(OpSemParser.SemRuleContext ctx) {
        String name = ctx.VARIABLE().getText();
        OpSemParser.SemBlockContext semBlockContext = ctx.semBlock();
        List<CondLayer> layers = semBlockToLayers(semBlockContext);
        return new SemRule(name, layers);
    }

    private List<CondLayer> semBlockToLayers(OpSemParser.SemBlockContext semBlockContext) {
        return semBlockContext.condLayer().stream().map(this::transformCondLayer).collect(Collectors.toUnmodifiableList());
    }

    private CondLayer transformCondLayer(OpSemParser.CondLayerContext ctx) {
        return new CondLayer(ctx.condLine().stream().map(this::transformCondLine).collect(Collectors.toUnmodifiableList()));
    }

    private CondLine transformCondLine(OpSemParser.CondLineContext ctx) {
        if (ctx.semBlock() != null) {
            return new CondLine.CondBlock(semBlockToLayers(ctx.semBlock()));
        }
        return switch (ctx.cond().size()) {
            case 0 -> throw new RuntimeException("Parse error, expected 1 or 2 conds, got 0");
            case 1 -> new CondLine.SimpleCondLine(
                    transformCond(ctx.cond().get(0)), Optional.empty());
            case 2 -> new CondLine.SimpleCondLine(
                    transformCond(ctx.cond().get(0)),
                    Optional.of(transformCond(ctx.cond().get(1))));
            default -> throw new RuntimeException("Parse error, expected 1 or 2 conds, got more");
        };
    }
    private Cond transformCond(OpSemParser.CondContext ctx) {
        return new Cond(ctx.exprs().stream().map(this::transformExprs).collect(Collectors.toUnmodifiableList()));
    }
    private Exprs transformExprs(OpSemParser.ExprsContext ctx) {
        return new Exprs(ctx.expr().stream().map(this::transformExpr).collect(Collectors.toUnmodifiableList()));
    }

    private Expr transformExpr(OpSemParser.ExprContext ctx) {
        if (null == ctx.VARIABLE()) {
            return new Expr.ExprsWrap(transformExprs(ctx.exprs()));
        }
        return new Expr.Var(ctx.VARIABLE().getText());
    }
}
