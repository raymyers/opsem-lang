package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Cond(List<Exprs> exprs) implements BaseAst {
    public String toLatex() {
        String inner = exprs.stream().map(e -> e.toLatex()).collect(Collectors.joining(" ,\\, "));
        if (exprs.size() == 1) {
            return inner;
        }
        return "(" + inner + ")";
    }
}
