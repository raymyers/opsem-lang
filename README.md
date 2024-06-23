# OpSem

> Experimental DSL for Operational Semantics, renders to LaTeX

For now, see the [tests](./src/test/java/OpSemLoaderTest.java) for usage. We can make a CLI if there's interest.

## Requirements
* Java 21+

## Run

`./gradlew test`

```
./gradlew install
./build/install/opsem/bin/opsem examples/example.opsem
```

See `examples/example.tex` for the appropriate LaTeX headers, such as including the semantics package. 
To render it, try OverLeaf.

[//]: # ([![.github/workflows/build.yml]&#40;https://github.com/vicsz/antlr4-gradle-template-CSVtoXML-translator/actions/workflows/build.yml/badge.svg&#41;]&#40;https://github.com/vicsz/antlr4-gradle-template-CSVtoXML-translator/actions/workflows/build.yml&#41;)

## LaTeX setup

Perhaps you want to render a PDG from the command line without going to OverLeaf.

With TexLive or MacTex installed, this might work:

`tlmgr install semantic`

`pdflatex examples/example.tex`

## References
From template: [antlr4-gradle-template-CSVtoXML-translator](https://github.com/vicsz/antlr4-gradle-template-CSVtoXML-translator)

http://scholar.princeton.edu/scuellar/blog/2014/03/deduction-trees-latex

