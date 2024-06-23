package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Exprs(List<Expr> exprs) {
    public String toLatex() {
        return exprs.stream().map(e -> e.toLatex()).collect(Collectors.joining("\\,"));
    }
}
