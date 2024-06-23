package ast;

import java.util.List;

public record Cond(List<Exprs> exprs) implements BaseAst {
}
