import ast.Program;
import ast.SemRule;
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

    public static final String FULL_PROGRAM_2 = """
        latex {
            context = "\\Gamma"
            WellTyped c a = "c \\bot a"
            HasType c a b = "c \\bot a : b"
            TypeOf c a = "c a"
        }
        
        rule SkipT {
            ~
            WellTyped context SKIP
        }
                
        rule AssignT {
            HasType context a (TypeOf context x)
            ~
            WellTyped context (Assign x a)
        }
                
        rule SeqT {
            WellTyped context c1
            WellTyped context c2
            ~
            WellTyped context (Seq c1 c2)
        }
        rule IfT {
            WellTyped context b
            WellTyped context c1
            WellTyped context c2
            ~
            WellTyped (If b c1 c2)
        }
        rule WhileT {
            WellTyped context b
            WellTyped context c
            ~
            WellTyped context (While b c)
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
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        String latex = SemRule.toLatex(subject.toProgram().rules().get(0));
        Approvals.verify(latex);
    }


    @Test
    void toLatexFullProgram()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_1);
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        String latex = Program.toLatex(subject.toProgram());
        Approvals.verify(latex);
    }


    @Test
    void astFullProgramWithRenderings()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_2);
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        ApprovalHelpers.verifyAsJson(subject.toProgram());
    }
    @Test
    void toLatexFullProgramWithRenderings()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_2);
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        String latex = Program.toLatex(subject.toProgram());
        Approvals.verify(latex);
    }
}
