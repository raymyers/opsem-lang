import ast.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpSemTransformer {
    public Program transformStart(OpSemParser.StartContext ctx ) {
        List<SemRule> rules = ctx.semRule().stream().map(this::transformSemRule).collect(Collectors.toUnmodifiableList());
        return new Program(rules);
    }
    public SemRule transformSemRule(OpSemParser.SemRuleContext ctx) {
        String name = ctx.VARIABLE().getText();
        List<CondLine> topLines = ctx.topCondLine().stream().map(this::transformTopCondLine).collect(Collectors.toUnmodifiableList());
        List<CondLine> bottomLines = ctx.bottomCondLine().stream().map(this::transformBottomCondLine).collect(Collectors.toUnmodifiableList());
        return new SemRule(name, topLines, bottomLines);
    }

    private CondLine transformBottomCondLine(OpSemParser.BottomCondLineContext ctx) {
        return transformCondLine(ctx.condLine());
    }

    private CondLine transformTopCondLine(OpSemParser.TopCondLineContext ctx) {
        return transformCondLine(ctx.condLine());
    }

    private CondLine transformCondLine(OpSemParser.CondLineContext ctx) {
        return switch (ctx.cond().size()) {
            case 0 -> throw new RuntimeException("Parse error, expected 1 or 2 conds, got 0");
            case 1 -> new CondLine(transformCond(ctx.cond().get(0)), Optional.empty());
            case 2 -> new CondLine(
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
