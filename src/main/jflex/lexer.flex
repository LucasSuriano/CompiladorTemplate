package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]
Letter = [a-zA-Z]
Digit = [0-9]

OpenComment = "*-"
CloseComment = "-*"
Comment = {OpenComment} (.)+ {CloseComment}

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = ":="

Greater = ">"
Less = "<"
Equal = "=="

OpenBracket = "("
CloseBracket = ")"
OpenCurlyBracket = "{"
CloseCurlyBracket = "}"
OpenSquareBracket = "["
CloseSquareBracket = "]"
Colon = ":"
Comma = ","
Quote = [\"]

Init = init
Leer = leer
Escribir = escribir

Int = Int
Float = Float
String = String

Mientras = mientras
Si = si
Sino = sino

And = AND
Or = OR
Not = NOT

Contar_Primos = ContarPrimos
Aplicar_Descuento = aplicarDescuento

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
FloatConstant = {Digit}+ "." {Digit}+ | {Digit}+ "." | "." {Digit}+
StringConstant = {Quote} (.)+ {Quote}
%%


/* keywords */

<YYINITIAL> {

  /* operators */
  {Plus}                                    { return symbol(ParserSym.PLUS); }
  {Sub}                                     { return symbol(ParserSym.SUB); }
  {Mult}                                    { return symbol(ParserSym.MULT); }
  {Div}                                     { return symbol(ParserSym.DIV); }
  {Assig}                                   { return symbol(ParserSym.ASSIG); }

  {Greater}                                 { return symbol(ParserSym.GREATER); }
  {Less}                                    { return symbol(ParserSym.LESS); }
  {Equal}                                   { return symbol(ParserSym.EQUAL); }

  {And}                                     { return symbol(ParserSym.AND); }
  {Or}                                      { return symbol(ParserSym.OR); }
  {Not}                                     { return symbol(ParserSym.NOT); }

  {Contar_Primos}                           { return symbol(ParserSym.CONTAR_PRIMOS); }
  {Aplicar_Descuento}                       { return symbol(ParserSym.APLICAR_DESCUENTO); }

  {OpenBracket}                             { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket}                            { return symbol(ParserSym.CLOSE_BRACKET); }
  {OpenCurlyBracket}                        { return symbol(ParserSym.OPEN_CURLY_BRACKET); }
  {CloseCurlyBracket}                       { return symbol(ParserSym.CLOSE_CURLY_BRACKET); }
  {OpenSquareBracket}                       { return symbol(ParserSym.OPEN_SQUARE_BRACKET); }
  {CloseSquareBracket}                      { return symbol(ParserSym.CLOSE_SQUARE_BRACKET); }
  {Colon}                                   { return symbol(ParserSym.COLON); }
  {Comma}                                   { return symbol(ParserSym.COMMA); }

  {Init}                                    { return symbol(ParserSym.INIT); }
  {Leer}                                    { return symbol(ParserSym.LEER); }
  {Escribir}                                { return symbol(ParserSym.ESCRIBIR); }

  {Mientras}                                { return symbol(ParserSym.MIENTRAS); }
  {Si}                                      { return symbol(ParserSym.SI); }
  {Sino}                                    { return symbol(ParserSym.SINO); }

  {Int}                                     { return symbol(ParserSym.INT); }
  {Float}                                   { return symbol(ParserSym.FLOAT); }
  {String}                                  { return symbol(ParserSym.STRING); }

  /* identifiers */
  {Identifier}                              { if(yylength() > MAX_LENGTH){ throw new InvalidLengthException(yytext()); }
                                                  else{return symbol(ParserSym.IDENTIFIER, yytext());}}
    /* Constants */
  {IntegerConstant}                         { long number = Long.parseLong(yytext());
                                                  if(number > MAX_INT_16 || number < 0){ throw new InvalidIntegerException(yytext()); }
                                                  else{return symbol(ParserSym.INTEGER_CONSTANT, yytext());}}

  {FloatConstant}                           { return symbol(ParserSym.FLOAT_CONSTANT, yytext()); }

  {StringConstant}                          { if(yylength() > MAX_LENGTH){ throw new InvalidLengthException(yytext()); }
                                                else{return symbol(ParserSym.STRING_CONSTANT, yytext());}}

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  {Comment}                      { /* ignore */ }
}


/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
