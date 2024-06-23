package ast;

import java.util.List;

public record LatexRendering(String label, List<String> params, String rendering) implements BaseAst  {
}
