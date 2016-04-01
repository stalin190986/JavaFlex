/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

/**
 *
 * @author TIA
 */
public class Proveedor {
    
    private int folioPrincipal;
    private String razonSocialPrin;

    public Proveedor(){

    }

    
    /**
     * @return the folioPrincipal
     */
    public int getFolioPrincipal() {
        return folioPrincipal;
    }

    /**
     * @param folioPrincipal the folioPrincipal to set
     */
    public void setFolioPrincipal(int folioPrincipal) {
        this.folioPrincipal = folioPrincipal;
    }
  

    /**
     * @return the razonSocialPrin
     */
    public String getRazonSocialPrin() {
        return razonSocialPrin;
    }

    /**
     * @param razonSocialPrin the razonSocialPrin to set
     */
    public void setRazonSocialPrin(String razonSocialPrin) {
        this.razonSocialPrin = razonSocialPrin;
    }

}
