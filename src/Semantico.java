import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {

   // registros semanticos
   private StringBuilder codigoObjeto = new StringBuilder();
   private Stack<String> pilhaTipos = new Stack<>();
   private Stack<String> pilhaRotulos = new Stack<>();
   private List<String> listaIdentificadores = new ArrayList<>();
   private Map<String, String> tabelaSimbolos = new HashMap<>();
   private String operadorRelacional = "";
   private String tipo = "";
   private int contadorRotulos = 0;

   public void executeAction(int action, Token token) throws SemanticError {
      System.out.println("Acao #" + action + ", Token: " + token);

      switch (action) {
         case  1: acao1(); break;
         case  2: acao2(); break;
         case  3: acao3(); break;
         case  4: acao4(); break;
         case  5: acao5(token); break;
         case  6: acao6(token); break;
         case  7: acao7(); break;
         case  8: acao8(); break;
         case  9: acao9(token); break;
         case 10: acao10(); break;
         case 11: acao11(); break;
         case 12: acao12(); break;
         case 13: acao13(token); break;
         case 14: acao14(); break;
         case 15: acao15(); break;
         case 16: acao16(); break;
         case 17: acao17(); break;
         case 18: acao18(token); break;
         case 19: acao19(token); break;
         case 20: acao20(); break;
         case 21: acao21(); break;
         case 22: acao22(token); break;
         case 23: acao23(); break;
         case 24: acao24(token); break;
         case 25: acao25(); break;
         case 26: acao26(token); break;
         case 27: acao27(); break;
         case 28: acao28(); break;
         case 29: acao29(); break;
         case 30: acao30(); break;
         case 31: acao31(token); break;
         case 32: acao32(); break;
         case 33: acao33(); break;
         case 34: acao34(); break;
         default:
            throw new SemanticError("Acao semantica nao implementada: " + action);
      }
   }

   private String novoRotulo() {
      return "rotulo" + contadorRotulos++;
   }

   private String tipoResultanteAritmetico(String tipo1, String tipo2) {
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         return "int64";
      }
      if ("float64".equals(tipo1) || "float64".equals(tipo2)) {
         return "float64";
      }
      return tipo2;
   }

   private void acao1() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push(tipoResultanteAritmetico(tipo1, tipo2));
      codigoObjeto.append("add\n");
   }

   private void acao2() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push(tipoResultanteAritmetico(tipo1, tipo2));
      codigoObjeto.append("sub\n");
   }

   private void acao3() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push(tipoResultanteAritmetico(tipo1, tipo2));
      codigoObjeto.append("mul\n");
   }

   private void acao4() {
      pilhaTipos.pop();
      pilhaTipos.pop();
      pilhaTipos.push("float64");
      codigoObjeto.append("div\n");
   }

   private void acao5(Token token) {
      pilhaTipos.push("int64");
      codigoObjeto.append("ldc.i8 ").append(token.getLexeme()).append("\n");
      codigoObjeto.append("conv.r8\n");
   }

   private void acao6(Token token) {
      pilhaTipos.push("float64");
      codigoObjeto.append("ldc.r8 ").append(token.getLexeme()).append("\n");
   }

   private void acao7() {
      String tipo = pilhaTipos.pop();
      pilhaTipos.push(tipo);
   }

   private void acao8() {
      String tipo = pilhaTipos.pop();
      pilhaTipos.push(tipo);
      codigoObjeto.append("ldc.i8 -1\n");
      codigoObjeto.append("conv.r8\n");
      codigoObjeto.append("mul\n");
   }

   private void acao9(Token token) {
      operadorRelacional = token.getLexeme();
   }

   private void acao10() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push("bool");

      if ("string".equals(tipo1) && "string".equals(tipo2)) {
         if ("==".equals(operadorRelacional)) {
            codigoObjeto.append("call bool [mscorlib]System.String::op_Equality(string, string)\n");
         } else if ("!=".equals(operadorRelacional)) {
            codigoObjeto.append("call bool [mscorlib]System.String::op_Inequality(string, string)\n");
         } else {
            codigoObjeto.append("call int32 [mscorlib]System.String::Compare(string, string)\n");
            codigoObjeto.append("ldc.i4.0\n");
            if ("<".equals(operadorRelacional)) {
               codigoObjeto.append("clt\n");
            } else if ("<=".equals(operadorRelacional)) {
               codigoObjeto.append("cgt\n");
               codigoObjeto.append("ldc.i4.1\n");
               codigoObjeto.append("xor\n");
            } else if (">".equals(operadorRelacional)) {
               codigoObjeto.append("cgt\n");
            } else if (">=".equals(operadorRelacional)) {
               codigoObjeto.append("clt\n");
               codigoObjeto.append("ldc.i4.1\n");
               codigoObjeto.append("xor\n");
            }
         }
         return;
      }

      if ("==".equals(operadorRelacional)) {
         codigoObjeto.append("ceq\n");
      } else if ("!=".equals(operadorRelacional)) {
         codigoObjeto.append("ceq\n");
         codigoObjeto.append("ldc.i4.1\n");
         codigoObjeto.append("xor\n");
      } else if ("<".equals(operadorRelacional)) {
         codigoObjeto.append("clt\n");
      } else if ("<=".equals(operadorRelacional)) {
         codigoObjeto.append("cgt\n");
         codigoObjeto.append("ldc.i4.1\n");
         codigoObjeto.append("xor\n");
      } else if (">".equals(operadorRelacional)) {
         codigoObjeto.append("cgt\n");
      } else if (">=".equals(operadorRelacional)) {
         codigoObjeto.append("clt\n");
         codigoObjeto.append("ldc.i4.1\n");
         codigoObjeto.append("xor\n");
      }
   }

   private void acao11() {
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
   }

   private void acao12() {
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.0\n");
   }

   private void acao13(Token token) throws SemanticError {
      String tipo = pilhaTipos.pop();

      if (!"bool".equals(tipo)) {
         throw new SemanticError("tipo incompatível em operador lógico !", token.getPosition());
      }

      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
      codigoObjeto.append("xor\n");
   }

   private void acao14() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         codigoObjeto.append("conv.i8\n");
      }
      codigoObjeto.append("call void [mscorlib]System.Console::Write(").append(tipo).append(")\n");
   }

   private void acao15() {
      pilhaTipos.pop();
      pilhaTipos.pop();
      pilhaTipos.push("bool");
      codigoObjeto.append("and\n");
   }

   private void acao16() {
      pilhaTipos.pop();
      pilhaTipos.pop();
      pilhaTipos.push("bool");
      codigoObjeto.append("or\n");
   }

   private void acao17() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push(tipoResultanteAritmetico(tipo1, tipo2));
      codigoObjeto.append("call float64 [mscorlib]System.Math::Pow(float64, float64)\n");
   }

   private void acao18(Token token) {
      pilhaTipos.push("char");
      codigoObjeto.append("ldc.i4 ").append(valorChar(token.getLexeme())).append("\n");
   }

   private int valorChar(String lexema) {
      if ("\\n".equals(lexema)) {
         return 10;
      }
      if ("\\s".equals(lexema)) {
         return 32;
      }
      if ("\\t".equals(lexema)) {
         return 9;
      }
      if (lexema != null && lexema.length() > 0) {
         return (int) lexema.charAt(lexema.length() - 1);
      }
      return 0;
   }

   private void acao19(Token token) {
      pilhaTipos.push("string");
      codigoObjeto.append("ldstr ").append(token.getLexeme()).append("\n");
   }

   private void acao20() {
      codigoObjeto.append(".assembly extern mscorlib {}\n");
      codigoObjeto.append(".assembly _programa{}\n");
      codigoObjeto.append(".module _programa.exe\n");
      codigoObjeto.append(".class public _unica{\n");
      codigoObjeto.append(".method static public void _principal(){\n");
      codigoObjeto.append(".entrypoint\n");
   }

   private void acao21() {
      codigoObjeto.append("ret\n");
      codigoObjeto.append("}\n");
      codigoObjeto.append("}\n");
   }

   private void acao22(Token token) {
      String lexema = token.getLexeme();
      if ("bool".equals(lexema)) {
         tipo = "bool";
      } else if ("int".equals(lexema)) {
         tipo = "int64";
      } else if ("float".equals(lexema)) {
         tipo = "float64";
      } else if ("char".equals(lexema)) {
         tipo = "char";
      } else if ("string".equals(lexema)) {
         tipo = "string";
      }
   }

   private void acao23() {
      for (String identificador : listaIdentificadores) {
         tabelaSimbolos.put(identificador, tipo);
         codigoObjeto.append(".locals(").append(tipo).append(" ").append(identificador).append(")\n");
      }
      listaIdentificadores.clear();
   }

   private void acao24(Token token) {
      listaIdentificadores.add(token.getLexeme());
   }

   private void acao25() {
      String tipoExpressao = pilhaTipos.pop();
      if ("int64".equals(tipoExpressao)) {
         codigoObjeto.append("conv.i8\n");
      }

      for (int i = 0; i < listaIdentificadores.size() - 1; i++) {
         codigoObjeto.append("dup\n");
      }

      for (String identificador : listaIdentificadores) {
         codigoObjeto.append("stloc ").append(identificador).append("\n");
      }
      listaIdentificadores.clear();
   }

   private void acao26(Token token) throws SemanticError {
      String identificador = token.getLexeme();
      String tipoIdentificador = tabelaSimbolos.get(identificador);

      if ("bool".equals(tipoIdentificador) || "char".equals(tipoIdentificador)) {
         throw new SemanticError(identificador + " - identificador inválido para comando de entrada", token.getPosition());
      }

      codigoObjeto.append("call string [mscorlib]System.Console::ReadLine()\n");
      if ("int64".equals(tipoIdentificador)) {
         codigoObjeto.append("call int64 [mscorlib]System.Int64::Parse(string)\n");
      } else if ("float64".equals(tipoIdentificador)) {
         codigoObjeto.append("call float64 [mscorlib]System.Double::Parse(string)\n");
      }
      codigoObjeto.append("stloc ").append(identificador).append("\n");
   }

   private void acao27() {
      pilhaTipos.pop();
      String rotuloFim = novoRotulo();
      pilhaRotulos.push(rotuloFim);
      String rotuloFalso = novoRotulo();
      codigoObjeto.append("brfalse ").append(rotuloFalso).append("\n");
      pilhaRotulos.push(rotuloFalso);
   }

   private void acao28() {
      String rotuloFalso = pilhaRotulos.pop();
      String rotuloFim = pilhaRotulos.pop();
      codigoObjeto.append("br ").append(rotuloFim).append("\n");
      pilhaRotulos.push(rotuloFim);
      codigoObjeto.append(rotuloFalso).append(":\n");
   }

   private void acao29() {
      String rotuloFim = pilhaRotulos.pop();
      codigoObjeto.append(rotuloFim).append(":\n");
   }

   private void acao30() {
      pilhaTipos.pop();
      String rotuloFalso = novoRotulo();
      codigoObjeto.append("brfalse ").append(rotuloFalso).append("\n");
      pilhaRotulos.push(rotuloFalso);
   }

   private void acao31(Token token) {
      String identificador = token.getLexeme();
      String tipoIdentificador = tabelaSimbolos.get(identificador);
      pilhaTipos.push(tipoIdentificador);
      codigoObjeto.append("ldloc ").append(identificador).append("\n");
      if ("int64".equals(tipoIdentificador)) {
         codigoObjeto.append("conv.r8\n");
      }
   }

   private void acao32() {
      String rotulo = novoRotulo();
      codigoObjeto.append(rotulo).append(":\n");
      pilhaRotulos.push(rotulo);
   }

   private void acao33() {
      pilhaTipos.pop();
      String rotulo = pilhaRotulos.pop();
      codigoObjeto.append("brtrue ").append(rotulo).append("\n");
   }

   private void acao34() {
      pilhaTipos.pop();
      String rotulo = pilhaRotulos.pop();
      codigoObjeto.append("brfalse ").append(rotulo).append("\n");
   }

   public String getCodigoObjeto() {
      return codigoObjeto.toString();
   }
}
