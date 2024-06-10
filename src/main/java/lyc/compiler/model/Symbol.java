package lyc.compiler.model;

public class Symbol {
    private String name;
    private String value;
    private String type;
    private Integer length;

    public Symbol(String nombre, String tipo, String valor, Integer longitud) {
        this.name = nombre;
        this.type = tipo;
        this.value = valor;
        this.length = longitud;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    private String toStringObject(Object o) {
        return o != null ? "" + o : "-";
    }

    @Override
    public String toString() {
        return String.format(
                "%-30s|%-30s|%-30s|%-30s", toStringObject(name), toStringObject(type), toStringObject(value), toStringObject(length)
        );
    }


}
