package ai.mender.opsem;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "opsem", mixinStandardHelpOptions = true, version = "opsem 0.0.0",
         description = "Generate LaTex from opsem")
class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "The file to process.")
    private File file;

//    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
//    private String algorithm = "SHA-256";

    @Override
    public Integer call() throws Exception {
        OpSemLoader subject = new OpSemLoader(file);
        if (!subject.valid()) {
            System.err.println(subject.getSyntaxErrors());
            return 1;
        }

        System.out.println(LatexRenderer.programToLatex(subject.toProgram()));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}