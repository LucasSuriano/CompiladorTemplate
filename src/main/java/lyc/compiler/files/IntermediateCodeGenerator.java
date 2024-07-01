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
        System.out.println("ADD: " + dato);
        this.polacaList.add(dato);
    }

    public void update(int pos, int desplazamiento) {
        int ultimaPos = polacaList.size();
        System.out.println("LO QUE QUIERO ACTUALIZAR: " + this.polacaList.get(pos));
        System.out.println("POS ACTUALIZAR: " + pos);
        System.out.println("ULTIMA POS ACTUALIZAR: " + ultimaPos);
        if(this.polacaList.get(pos).compareTo("#") == 0){
            Integer nuevaUltimaPos = ultimaPos+desplazamiento;
            System.out.println("NuevaUltimaPos: " + nuevaUltimaPos);
            System.out.println("Pos: " + pos);
            System.out.println("Desp: " + desplazamiento);
            this.polacaList.set(pos, nuevaUltimaPos.toString());
        }
    }

    public void goTo(Integer pos) {
        int ultimaPos = polacaList.size();
        System.out.println("POS GOTO: " + pos);
        System.out.println("ULTIMA POS GOTO: " + ultimaPos);
        this.polacaList.add(pos.toString());
    }

    public void next() {
        this.polacaList.add("#");
        System.out.println("DEJA ESPACIO LIBRE");
    }

    public void apilar(){
        int ultimaPos = polacaList.size();
        System.out.println("APILAR: " + ultimaPos);
        this.pila.push(ultimaPos);
    }

    public int desapilar(){
        System.out.println("DESAPILAR");
        int tope = this.pila.pop();
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

    public boolean analizar(String t1, String t2){

        if( t1.contains(t2) || t2.contains(t1) )
            return true;
        else if((t1 == "Int" && t2 == "Float") || (t1 == "Float" && t2 == "Int")) {
            return true;
        }
        else {
            throw new Error(t1 + " y " + t2 + " Error de tipo");
        }
    }

    public void updateIndice(Integer pos, String dato) {
        if(this.polacaList.get(pos).compareTo("#") == 0){
            this.polacaList.set(pos, dato);
        }
    }

    public List<String> getPolacaList(){
        return this.polacaList;
    }
}
