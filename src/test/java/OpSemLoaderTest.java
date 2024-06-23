import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpSemLoaderTest {


    public static final String FULL_PROGRAM_1 = """
        rule Skip {
            ~
            Skip, s => s
        }
                
        rule Assign {
            ~
            Assign x a, s => Put s (Eval a s)
        }
                
        rule Seq {
            c1, s1 => s2
            c2, s2 => s3
            ~
            Seq c1 c2, s1 => s3
        }
                
        rule WhileTrue {
            bval b s1
            c, s1 => s2
            While b c, s2 => s3
            ~
            While b c, s1 => s3
        }
    """;

    @Test
    void generatAst()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_1);
        assertTrue(subject.valid());

        ApprovalHelpers.verifyAsJson(subject.toProgram());
    }

    @Test
    void toLatex()
    {
        String input = """                    
            rule WhileTrue {
                bval b s1
                c, s1 => s2
                While b c, s2 => s3
                ~
                While b c, s1 => s3
            }
        """;
        OpSemLoader subject = new OpSemLoader(input);
        assertTrue(subject.valid());

        String latex = subject.toProgram().rules().get(0).toLatex();
        Approvals.verify(latex);
    }


    @Test
    void toLatexFullProgram()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_1);
        assertTrue(subject.valid());

        String latex = subject.toProgram().toLatex();
        Approvals.verify(latex);
    }
}
