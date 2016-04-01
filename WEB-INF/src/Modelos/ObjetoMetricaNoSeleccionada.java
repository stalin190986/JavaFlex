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
 * SIS SIS REQ 2012 074
 */
public class ObjetoMetricaNoSeleccionada implements Externalizable
{

    public String periodo;
    public int posicion_inicial = 0;
    public int posicion_final = 0;


    public ObjetoMetricaNoSeleccionada()
    {
        
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
            // Read in the server properties from the client representation.
            /*
             label = (String)in.readObject();
            data = (String)in.readObject();
            */

            periodo = (String)in.readObject();
            posicion_inicial = (Integer)in.readInt();
            posicion_final = (Integer)in.readInt();

    }////   readExternal

    public void writeExternal(ObjectOutput out) throws IOException
    {
            // Write out the client properties from the server representation
            out.writeObject(periodo);
            out.writeInt(posicion_inicial);
            out.writeInt(posicion_final);
    }////   writeExternal

}////FIN CLASE 
