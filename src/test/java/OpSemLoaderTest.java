import ai.mender.opsem.LatexRenderer;
import ai.mender.opsem.OpSemLoader;
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
            WellTyped c a = "<c> \\vdash <a>"
            HasType c a b = "<c> \\vdash <a> : <b>"
            TypeOf c a = "<c> <a>"
            While b c = "\\text{WHILE } <b> \\text{ DO } <c>"
            Assign a b = "<a> ::= <b>"
            Seq a b = "<a> \\text{ ;; } <b>"
            If a b c = "\\text{IF }a  \\text{ THEN } b \\text{ ELSE } b)"
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
            WellTyped context (If b c1 c2)
        }
        rule WhileT {
            WellTyped context b
            WellTyped context c
            ~
            WellTyped context (While b c)
        }
    """;

    public static final String TREE = """
        latex {
            WellTyped a = "\\Gamma \\vdash <a>"
            HasType a b = "\\Gamma \\vdash <a> : <b>"
            TypeOf a = "\\Gamma\\,<a>"
            While b c = "\\text{WHILE } <b> \\text{ DO } <c>"
            Assign a b = "<a> ::= <b>"
            Seq a b = "<a> \\text{ ;; } <b>"
            If a b c = "\\text{IF }a  \\text{ THEN } b \\text{ ELSE } b)"
            Eq a b = "<a> = <b>"
        }

        rule Derivation912 {
            {
                Eq (TypeOf y) Ity
                ~
                HasType (V x) Ity
                ~
                WellTyped (Assign x (V y))
            }
            {
                {
                    Eq (TypeOf x) Ity
                    ~
                    HasType (V x) Ity
                }
                {
                    Eq (TypeOf y) Ity
                    ~
                    HasType (V y) Ity
                }
                ~
                HasType (Plus (V x) (V y)) Ity
                ~
                WellTyped (Assign y (Plus (V x) (V y)))
            }
            ~
            WellTyped (Seq (Assign x (V y)) (Plus (V x) (V y)))
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

        String latex = new LatexRenderer(subject.toProgram()).semRuleToLatex(subject.toProgram().rules().get(0));
        Approvals.verify(latex);
    }


    @Test
    void toLatexFullProgram()
    {
        OpSemLoader subject = new OpSemLoader(FULL_PROGRAM_1);
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        String latex = LatexRenderer.programToLatex(subject.toProgram());
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

        String latex = LatexRenderer.programToLatex(subject.toProgram());
        Approvals.verify(latex);
    }
    @Test
    void toLatexDerivationTree()
    {
        OpSemLoader subject = new OpSemLoader(TREE);
        assertTrue(subject.valid(), subject.getSyntaxErrors());

        String latex = LatexRenderer.programToLatex(subject.toProgram());
        Approvals.verify(latex);
    }
}
