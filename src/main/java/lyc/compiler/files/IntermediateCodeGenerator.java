package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IntermediateCodeGenerator implements FileGenerator {

    private static IntermediateCodeGenerator intermedia;
    private List<String> polacaList;
    private Stack<Integer> pila;

    private IntermediateCodeGenerator() {
        this.polacaList = new ArrayList<>();
        this.pila = new Stack<Integer>();
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

    public void update(int pos, int desplazamiento) {
        int ultimaPos = polacaList.size();
        if(this.polacaList.get(pos) == null){
            Integer nuevaUltimaPos = ultimaPos+desplazamiento;
            System.out.println("NuevaUltimaPos: " + nuevaUltimaPos);
            System.out.println("Pos: " + pos);
            System.out.println("Desp: " + desplazamiento);
            this.polacaList.set(pos, nuevaUltimaPos.toString());
        }
    }

    public void next() {
        this.polacaList.add(null);
    }

    public void apilar(){
        int ultimaPos = polacaList.size();
        System.out.println("Apila: " + ultimaPos);
        this.pila.push(ultimaPos);
    }

    public int desapilar(){
        System.out.println("Predesapilar: ");
        int tope = this.pila.pop();
        System.out.println("Desapila: " + tope);
        return tope;
    }

    private String recorrerPolaca() {

        StringBuilder stringPolaca = new StringBuilder();

        for (String elem : polacaList) {
            stringPolaca.append(elem);
            stringPolaca.append(" ");
        }

        return stringPolaca.toString().trim();
    }
}
