package ast;

public interface Expr extends BaseAst {

    record Var(String name) implements Expr {

    }

    record ExprsWrap(Exprs exprs) implements Expr {

    }
}
