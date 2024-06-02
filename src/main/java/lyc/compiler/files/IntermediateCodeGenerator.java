package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IntermediateCodeGenerator implements FileGenerator {

    private static IntermediateCodeGenerator intermedia;
    private List<String> polacaList;
    private Stack<String> pila;

    private IntermediateCodeGenerator() {
        this.polacaList = new ArrayList<>();
        this.pila = new Stack<String>();
    }

    public static IntermediateCodeGenerator getInstance() {
        if(intermedia == null) {
            intermedia = new IntermediateCodeGenerator();
        }
        return intermedia;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(this.intermedia.recorrerPolaca());
    }

    public void add(String dato) {
        this.polacaList.add(dato);
    }

    public void update(Integer pos,String dato) {
        if(this.polacaList.get(pos) == null)
            this.polacaList.set(pos, dato);
    }

    public void next() {
        this.polacaList.add(null);
    }

    public void apilar(String aux){
        this.pila.push(aux);
    }

    public String desapilar(){
        return this.pila.pop();
    }

    private String recorrerPolaca() {

        StringBuilder stringPolaca = new StringBuilder();

        for (String elem : polacaList) {
            stringPolaca.append(elem);
            stringPolaca.append(" ");
        }

        return stringPolaca.toString().trim();
    }

    /*public void addSymbol(String name, Type type) throws SemanticException {
        if (symbols.containsKey(name)) {
            throw new SemanticException("Variable ya declarada: " + name);
        }
        symbols.put(name, new Symbol(name, type));
    }

    public Symbol getSymbol(String name) throws SemanticException {
        Symbol symbol = symbols.get(name);
        if (symbol == null) {
            throw new SemanticException("Variable no declarada: " + name);
        }
        return symbol;
    }*/
}
