package ast;

import java.util.List;
import java.util.stream.Collectors;

public record Cond(List<Exprs> exprs) implements BaseAst {
    public static String toLatex(Cond cond) {
        String inner = cond.exprs.stream().map(e -> e.toLatex()).collect(Collectors.joining(" ,\\, "));
        if (cond.exprs.size() == 1) {
            return inner;
        }
        return "(" + inner + ")";
    }
}
