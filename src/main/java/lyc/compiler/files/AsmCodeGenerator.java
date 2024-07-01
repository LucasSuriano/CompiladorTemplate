package lyc.compiler.files;

import lyc.compiler.model.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AsmCodeGenerator implements FileGenerator {

    private static AsmCodeGenerator assembler;
    public AsmCodeGenerator(List<String> assemblerList) {
        this.assemblerList = assemblerList;
    }
    public AsmCodeGenerator() {
        this.assemblerList = new ArrayList<>();
    }

    List<String> assemblerList = new ArrayList<>();
    SymbolTableGenerator gts;
    IntermediateCodeGenerator gci;
    int auxCount = 0;
    int labelCount = 0;
    int cellNumber = 1;
    public AsmCodeGenerator(SymbolTableGenerator gts, IntermediateCodeGenerator gci)
    {
        this.gts = gts;
        this.gci = gci;
    }

    public static AsmCodeGenerator getInstance(SymbolTableGenerator gts, IntermediateCodeGenerator gci) {
        if(assembler == null) {
            assembler = new AsmCodeGenerator(gts, gci);
        }
        return assembler;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        try {
            if (assemblerList.isEmpty())
                return;

            assemblerList.forEach(asm -> {
                try {
                    fileWriter.write(asm.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateAssembler(){

        List<String> code = this.generateCode();
        List<String> data = this.generateData();

        assemblerList.add("include grupo8.asm");

        assemblerList.add(".MODEL LARGE");
        assemblerList.add(".386");
        assemblerList.add(".STACK 200h\n");

        assemblerList.add(".DATA\n");
        assemblerList.addAll(data);

        assemblerList.add("\n.CODE");
        assemblerList.add("START:\n");
        assemblerList.add("\nMOV AX, @DATA");
        assemblerList.add("MOV DS, AX");
        assemblerList.add("MOV ES, AX\n");
        assemblerList.addAll(code);

        assemblerList.add("\nMOV AX, 4C00h");
        assemblerList.add("INT 21h");
        assemblerList.add("END START");
    }
    private List<String> generateData() {
        List<String> data = new ArrayList<>();
        for(Symbol symbol : this.gts.getSymbolList()){
            if(symbol.getType() == "String")
                data.add(String.format("%-20s %-5s %-30s,'$'", symbol.getName(),"db", symbol.getValue()));
            else
                data.add(String.format("%-20s %-5s %-30s", symbol.getName(),"dd", symbol.getValue()));
        }

        for(int i=1; i <= this.auxCount ; i++)
            data.add(String.format("%-20s %-5s %-30s", "@aux" + i,"dd", "?"));

        data.add(String.format("%-20s %-5s %-30s,'$'", "_NEWLINE","db", "0DH,0AH"));
        return data;
    }
    private List<String> generateCode() {
        List<String> code = new ArrayList<>();
        Stack<String> operandStack  = new Stack<String>();
        Queue<String> labelQueue  = new LinkedList<String>();
        Stack<Integer> cellNumberStack = new Stack<Integer>();

        auxCount = 0;
        labelCount = 0;
        cellNumber = 1;

        for(String cell : this.gci.getPolacaList()){

            while(!cellNumberStack.isEmpty() && cellNumber == cellNumberStack.peek()){
                code.add(labelQueue.remove() + ":");
                cellNumberStack.pop();
            }

            switch (cell){
                case "escribir":
                {
                    String op = operandStack.pop();
                    String type = this.getSymbolType(op);

                    if(type == "String")
                        code.add("displayString " + op );
                    if(type == "Integer")
                        code.add("displayInteger " + op );
                    if(type == "Float")
                        code.add("displayFloat " + op );

                    code.add("displayString _NEWLINE");
                    break;
                }
                case "leer":
                {
                    String op = operandStack.pop();
                    String type = this.getSymbolType(op);

                    if(type == "String")
                        code.add("GetString " + op );
                    if(type == "Integer")
                        code.add("GetInteger " + op );
                    if(type == "Float")
                        code.add("GetFloat " + op );

                    break;
                }
                case ":=":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    code.add("FLD " + op1);
                    code.add("FSTP " + op2);
                    code.add("");
                    break;
                }
                case "+":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    String aux = "@aux" + (++auxCount);
                    code.add("FLD " + op1);
                    code.add("FLD " + op2);
                    code.add("FADD");
                    code.add("FSTP " + aux);
                    code.add("");
                    operandStack.add(aux);
                    break;
                }
                case "-":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    String varAux = "@aux" + (++auxCount);
                    code.add("FLD " + op1);
                    code.add("FLD " + op2);
                    code.add("FSUB");
                    code.add("FSTP " + varAux);
                    code.add("");
                    operandStack.add(varAux);
                    break;
                }
                case "/":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    String varAux = "@aux" + (++auxCount);
                    code.add("FLD " + op1);
                    code.add("FLD " + op2);
                    code.add("FDIV");
                    code.add("FSTP " + varAux);
                    code.add("");
                    operandStack.add(varAux);
                    break;
                }
                case "*":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    String varAux = "@aux" + (++auxCount);
                    code.add("FLD " + op1);
                    code.add("FLD " + op2);
                    code.add("FMUL");
                    code.add("FSTP " + varAux);
                    code.add("");
                    operandStack.add(varAux);
                    break;
                }
                case "CMP":
                {
                    String op2 = operandStack.pop();
                    String op1 = operandStack.pop();
                    code.add("FLD " + op1);
                    code.add("FLD " + op2);
                    code.add("FXCH");
                    code.add("FCOMP");
                    code.add("FSTSW AX");
                    code.add("SAHF");
                    break;
                }
                case "BLE":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JNA " + label);
                    code.add("");
                    break;
                }
                case "BGE":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JAE " + label);
                    code.add("");
                    break;
                }
                case "BLT":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JB " + label);
                    code.add("");
                    break;
                }
                case "BGT":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JA " + label);
                    code.add("");
                    break;
                }
                case "BEQ":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JE " + label);
                    code.add("");
                    break;
                }
                case "BNE":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add("JNE " + label);
                    code.add("");
                    break;
                }
                case "BI":
                {
                    code.add("JMP " + labelQueue.remove());
                    code.add("");
                    break;
                }
                case "ET":
                {
                    String label = "label" + (++labelCount);
                    labelQueue.add(label);
                    code.add(label + ":");
                    code.add("");
                    break;
                }
                default: {
                    if(cell.startsWith("#")){
                        int cellJumpNumber = Integer.parseInt(cell.substring(1));
                        if(cellJumpNumber >= cellNumber){
                            cellNumberStack.add(cellJumpNumber);
                        }
                    }
                    else{
                        String operandName = getSymbolName(cell);
                        operandStack.add(operandName);
                    }
                    break;
                }
            }
            cellNumber++;
        }

        if(!cellNumberStack.isEmpty() && cellNumber == cellNumberStack.peek()){
            code.add(labelQueue.remove() + ":");
            cellNumberStack.pop();
        }
        return code;
    }


    private String getSymbolName(String value)
    {
        for(Symbol s : this.gts.getSymbolList())
        {
            if( s.getValue().equals(value))
            {
                return s.getName();
            }
        }
        return value;
    }

    private String getSymbolType(String name)
    {
        for(Symbol s : this.gts.getSymbolList())
        {
            if( s.getName().equals(name))
            {
                return s.getType();
            }
        }
        return null;
    }
}
