package ast;

import java.util.Optional;

public record CondLine(Cond pre, Optional<Cond> post) implements BaseAst  {
    String toLatex() {
        if (post.isPresent()) {
            return pre.toLatex() + " => " + post.get().toLatex();
        }
        return pre.toLatex();
    }
}
