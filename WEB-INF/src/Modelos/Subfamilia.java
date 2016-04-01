/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

/**
 *
 * @author TIA
 */
public class Subfamilia {

    private long codigo;
    private String nombre;
    private String nuevoEliminado;
    
    public Subfamilia(){

    }

    /**
     * @return the codigo
     */
    public long getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the nuevoEliminado
     */
    public String getNuevoEliminado() {
        return nuevoEliminado;
    }

    /**
     * @param nuevoEliminado the nuevoEliminado to set
     */
    public void setNuevoEliminado(String nuevoEliminado) {
        this.nuevoEliminado = nuevoEliminado;
    }

    
}
