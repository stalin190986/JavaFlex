/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

///VER http://livedocs.adobe.com/blazeds/1/blazeds_devguide/help.html?content=serialize_data_3.html

package Modelos;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

/**
 *
 * @author sarroyabe
 */
public class ObjetoFiltroJava implements Externalizable
{
    public String label;
    public String data;
    public String filtro;
    public int tabla = 0;   /// 0 existe solo dimension	 1 dw_entradas_fact     2  dw_iproduct_mm_fact2		3 ambas tablas
    public int grupo_checkbox = 0;


    public ObjetoFiltroJava()
    {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
            // Read in the server properties from the client representation.
            /*
             label = (String)in.readObject();
            data = (String)in.readObject();
            */

            label = (String)in.readObject();
            data = (String)in.readObject();
            filtro = (String)in.readObject();
            tabla = (Integer)in.readInt();
            grupo_checkbox = (Integer)in.readInt();

    }////   readExternal

    public void writeExternal(ObjectOutput out) throws IOException
    {
            // Write out the client properties from the server representation
            out.writeObject(label);
            out.writeObject(data);
            out.writeObject(filtro);
            out.writeInt(tabla);
            out.writeInt(grupo_checkbox);
    }////   writeExternal
        

}///FIN DE CLASE


