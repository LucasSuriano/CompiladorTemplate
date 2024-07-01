package lyc.compiler.files;

import lyc.compiler.model.DuplicateTokenException;
import lyc.compiler.model.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SymbolTableGenerator implements FileGenerator{

    private static SymbolTableGenerator symbolTable;
    private Map<String,SymbolTableToken> register;
    private List<Symbol> symbolList;
    public SymbolTableGenerator() {
        this.register = new HashMap<String,SymbolTableToken>();
        this.symbolList = new ArrayList<Symbol>();
    }

    public static SymbolTableGenerator getInstance() {
        if(symbolTable == null) {
            symbolTable = new SymbolTableGenerator();
        }
        return symbolTable;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        String file = String.format("%-40s|%-10s|%-40s|%-10s\n","NOMBRE","TIPODATO","VALOR","LONGITUD");
        for (Map.Entry<String, SymbolTableToken> entry : this.register.entrySet()) {
            file += entry.getValue().toString() + "\n";
        }
        fileWriter.write(file);
    }

    public String getFactor(String name) {
        if(exist(name))
            return getSymbol(name).getType();
        else
            throw new Error(name+ " No ha sido declarada");
    }

    public Symbol getSymbol(String name) {

        Iterator<Symbol> iterator = symbolList.iterator();

        while (iterator.hasNext()) {
            Symbol symbol = iterator.next();
            if (symbol.getName().equals(name))
                return symbol;
        }

        /*Si el name no se encuentra en la lista, devuelve Error*/
        throw new Error(name+ " no se encuentra en SymbolTable");
    }

    public void addSymbol(String name, String type, String value, Integer length) throws DuplicateTokenException {
        if (!exist(name)) {
            symbolList.add(new Symbol(name, type, value, length));
        }
        else {
            throw new DuplicateTokenException("El token '" +  name + "' ya se encuentra en la tabla de simbolos.");
        }
    }

    public Boolean exist(String name) {
        return symbolList.stream().anyMatch(symbol -> symbol.getName().equals(name));
    }

    public Integer getLength(String token) {
        return token.length();
    }

    public void addTokenInit(List<String> identifiers, String dataType) throws DuplicateTokenException {
        for(String id : identifiers) {
            if (!exist(id)) {
                this.register.put(id,new SymbolTableToken(id, dataType,"",null));
                addSymbol(id, dataType, "", null);
            } else {
                throw new Error("Error de sintaxis: la variable '" + id + "' ya habia sido declarada.");
            }
        }
        identifiers.clear();
    }

    //agregar exception para duplicados
    //en las reglas de expresion validar que sean de mismo tipo
    //validar que existan variables

    public void addTokenIdAssignment(String token) {
        /*if(!this.register.containsKey(token)) {
            this.register.put(token,new SymbolTableToken(token, "","",null));
            addSymbol(token, "", "", null);
        }*/
    }

    public void addTokenCteAssignment(String token, String dataType) throws DuplicateTokenException{
        if(!this.register.containsKey(token)) {
            String newToken = (dataType == "String" ? token.replaceAll("\"", "") : token);
            this.register.put(token,new SymbolTableToken("_"+newToken,dataType,newToken,dataType == "String" ? getLength(newToken) : null));
            addSymbol(newToken, dataType, newToken, dataType == "String" ? getLength(newToken) : null);
        }
    }

    public List<Symbol> getSymbolList() {
        return symbolList;
    }
}