import ast.Program;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class OpSemLoader {

    private final List<String> syntaxErrors;
    private final OpSemParser parser;
    private final OpSemParser.StartContext startContext;

    private class ErrorListener extends ConsoleErrorListener {

        private final List<String> syntaxErrors = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            syntaxErrors.add(String.format("Syntax Error on %s:%s - %s", line, charPositionInLine, msg));
        }

        public List<String> getSyntaxErrors(){
            return syntaxErrors;
        }
    }

    OpSemLoader(String input){
        OpSemLexer lexer = new OpSemLexer(CharStreams.fromString(input));
        this.parser = new OpSemParser(new CommonTokenStream(lexer));

        ErrorListener errorListener = new ErrorListener();

        //Remove and Replace Default Console Error Listeners
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        lexer.addErrorListener(errorListener);

        syntaxErrors = errorListener.getSyntaxErrors();
        startContext = parser.start();
    }

    private String getStringTree() {
        return startContext.toStringTree(parser);
    }

    public Program toProgram() {
        return new OpSemTransformer().transformStart(startContext);
    }

    public boolean valid(){
        return syntaxErrors.size() == 0;
    }

    public String getSyntaxErrors(){
        return String.join("\n",syntaxErrors) + "\n";
    }
}
