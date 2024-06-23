package ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Expr extends BaseAst {
    String toLatex();

    record Var(String name) implements Expr {

        @Override
        public String toLatex() {
            Pattern endWithDigits = Pattern.compile(".*(\\D)(\\d+)$");
            Matcher matcher = endWithDigits.matcher(name);
            if (matcher.matches()) {
                String base = matcher.group(1);
                String sub = matcher.group(2);
                return base + "_" + sub;
            }
            return name;
        }
    }

    record ExprsWrap(Exprs exprs) implements Expr {

        @Override
        public String toLatex() {
            return "(" + exprs.toLatex() + ")";
        }
    }
}
