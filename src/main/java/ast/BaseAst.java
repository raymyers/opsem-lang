package ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(include=JsonTypeInfo.As.PROPERTY, use= JsonTypeInfo.Id.SIMPLE_NAME)
public interface BaseAst {
}
