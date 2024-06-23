package ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Expr extends BaseAst {

    record Var(String name) implements Expr {

        public static String toLatex(Var var) {
            Pattern endWithDigits = Pattern.compile(".*(\\D)(\\d+)$");
            Matcher matcher = endWithDigits.matcher(var.name);
            if (matcher.matches()) {
                String base = matcher.group(1);
                String sub = matcher.group(2);
                return base + "_" + sub;
            }
            return var.name;
        }
    }

    record ExprsWrap(Exprs exprs) implements Expr {

        public static String toLatex(ExprsWrap exprsWrap) {
            return "(" + exprsWrap.exprs.toLatex() + ")";
        }
    }
}
