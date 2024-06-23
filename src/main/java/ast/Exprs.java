package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Exprs(List<Expr> exprs) {
    public String toLatex() {
        return exprs.stream().map(e -> {
            if (e instanceof Expr.ExprsWrap ew) return Expr.ExprsWrap.toLatex(ew);
            if (e instanceof Expr.Var v) return Expr.Var.toLatex(v);
            return ""; // Shouldn't happen
        }).collect(Collectors.joining("\\,"));
    }
}
