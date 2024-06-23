package ast;

import java.util.Optional;

public record CondLine(Cond pre, Optional<Cond> post) implements BaseAst  {
    static String toLatex(CondLine condLine) {
        if (condLine.post.isPresent()) {
            return Cond.toLatex(condLine.pre) + " => " + Cond.toLatex(condLine.post.get());
        }
        return Cond.toLatex(condLine.pre);
    }
}
