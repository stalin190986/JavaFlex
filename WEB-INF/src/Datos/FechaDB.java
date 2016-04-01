/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Fecha;

/**
 *
 * @author TIA
 */
public class FechaDB
{

      private List listaAnios;
      private List listaMeses;

      public FechaDB(){
          listaAnios = new ArrayList();
          listaMeses = new ArrayList();
      }

      /**
    * Obtiene la lista de años del modelo de estructura de surtido.
    * Tabla: DW_FECHA_DIM
    * @return la lista de años en forma descendente.
    * @see Fecha
    */
    public List getAnios()
    {
        List list = new ArrayList();
        Connection c = null;

        // QUERY DE AÑOS
        String query = "SELECT DISTINCT fe_anio as anios FROM dw_fecha_dim \n"
                + " order by fe_anio desc";

         try
         {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Fecha fecha = new Fecha();
                fecha.setAnio(rs.getString("anios"));
                list.add(fecha);
            }
         }
         catch (SQLException e)
         {
                System.out.println(e.toString());
         } finally
         {
                Conexion.close(c);
         }
            return list;

    }// Fin getAnios

    /**
     * Obtiene los meses dado un año.
     * Tabla: DW_FECHA_DIM
     * @param anio
     * @return la lista de meses. Si el año es 0
     *         obtienes los meses del ultimo año.
     * @see Fecha
     */

    public List getMeses(int anio)
    {
        List list = new ArrayList();
        Connection c = null;
        String query;
        String nombreMes="";

        if(anio!=0)
        {
            query = "SELECT DISTINCT date_format(fe_fecha,'%Y-%m-01') as fecha, month(fe_fecha) as meses \n"
             + " FROM DW_FECHA_DIM WHERE YEAR(fe_fecha)= "+anio+" order by meses desc";
        }
        else{
            //QUERY DE LOS MESES DEL ULTIMO AÑO
            query = "SELECT DISTINCT date_format(fe_fecha,'%Y-%m-01') as fecha, month(fe_fecha) as meses \n"
                    + "FROM DW_FECHA_DIM WHERE YEAR(fe_fecha) = \n"
                    + " (SELECT DISTINCT YEAR(fe_fecha) AS ANIOS FROM \n"
                    + "  DW_FECHA_DIM ORDER BY ANIOS DESC LIMIT 1) order by meses desc";

        }

        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                Fecha fecha = new Fecha();
                nombreMes = Fecha.getNombreMes(rs.getInt("MESES"));
                //fecha.setMes(rs.getString("MESES"));
                fecha.setMes(nombreMes);
                fecha.setFecha(rs.getString("FECHA"));
                list.add(fecha);
            }
         } catch (SQLException e) {
                System.out.println(e.toString());
         } finally {
                Conexion.close(c);
         }
            return list;
    }//Fin getMeses


    public String getMaxFechaCarga(String fechaFin)
    {
        List list = new ArrayList();
        Connection c = null;
        String query;
        String fecha="";

        query = "select max(fc_fecha) as fecha from dw_iproduct_fact where fc_fecha >= "
                        + " '"+fechaFin+"'";


        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                fecha =  rs.getString("FECHA");
            }
         } catch (SQLException e) {
                System.out.println(e.toString());
         } finally {
                Conexion.close(c);
         }
            return fecha;
    }//Fin getMaxFechaCarga


        public String generaFecha()
        {

        Connection c = null;
        String  query ="";
        String nomb_mes = "";
        String numero_mes = "";
        String anio = "";
        String anio_ant = "";
        String mes = "";
        String mes_ant = "";
        String dia = "";
        String fecha = "";
        String xmlList = "";
        String flag_anio = "0";
        String reg_anio_ant = "";
        // QUERY DE AÑOS
        query = "SELECT DISTINCT(FE_FECHA) AS FECHA, ";
        query += "YEAR(FE_FECHA) AS ANIO, ";
        query += "MONTH(FE_FECHA) AS MES, ";
        query += "DAY(FE_FECHA) AS DIA ";
        query += "FROM dw_fecha_dim ";
        query += "ORDER BY FECHA";

         try
         {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                fecha = rs.getString("FECHA");
                anio = rs.getString("ANIO");
                mes = rs.getString("MES");
                dia = rs.getString("DIA");
                reg_anio_ant = String.valueOf(Integer.parseInt(anio) - 1);

                if(!anio.equals(anio_ant)){
                    if(flag_anio.equals("1")){
                        xmlList += "\n</node>\n";
                        xmlList += "</node>\n";
                    }//endif
                    xmlList += "<node state='unchecked' label='"+anio+"' value = '"+anio+"' >\n";
                    if(!mes.equals(mes_ant)){
                        Fecha obj_fecha = new Fecha();
                        if(!mes.equals(mes_ant)){
                            if(mes.length() == 1){
                                numero_mes = "0"+mes;
                            }else{
                                numero_mes = mes;
                            }
                        }//endif
                        if(dia.length() == 1){
                             dia = "0"+dia;
                        }//endif
                        String reg_ant = reg_anio_ant+"-"+numero_mes+"-"+dia;
                        nomb_mes = Fecha.setNombreMes(Integer.parseInt(mes));
                        xmlList += "<node state='unchecked' label='"+nomb_mes+"' value = '"+numero_mes+"'>\n";
                        xmlList += "<node state='unchecked' label='"+fecha+"' value = '"+fecha+"' value_ant = '"+reg_ant+"'  />";
                    }else{
                        xmlList += "</node>\n";
                    }//endif
                    flag_anio = "1";
                }else{
                    if(!mes.equals(mes_ant)){
                        xmlList += "\n</node>\n";
                        Fecha obj_fecha = new Fecha();
                        if(!mes.equals(mes_ant)){
                            if(mes.length() == 1){
                                numero_mes = "0"+mes;
                            }else{
                                numero_mes = mes;
                            }//endif
                        }//endif
                        if(dia.length() == 1){
                             dia = "0"+dia;
                        }//endif
                        String reg_ant = reg_anio_ant+"-"+numero_mes+"-"+dia;
                        nomb_mes = Fecha.setNombreMes(Integer.parseInt(mes));
                        xmlList += "<node state='unchecked' label='"+nomb_mes+"' value = '"+numero_mes+"'>\n";
                        xmlList += "<node state='unchecked' label='"+fecha+"' value = '"+fecha+"' value_ant = '"+reg_ant+"'  />";
                    }else{
                        if(mes.length() == 1){
                            numero_mes = "0"+mes;
                        }else{
                            numero_mes = mes;
                        }//endif
                        if(dia.length() == 1){
                             dia = "0"+dia;
                        }//endif
                        String reg_ant = reg_anio_ant+"-"+numero_mes+"-"+dia;
                        xmlList += "<node state='unchecked' label='"+fecha+"' value = '"+fecha+"' value_ant = '"+reg_ant+"'  />";
                    }//endif
                    //xmlList += "</node>";
                }//endif
                mes_ant = mes;
                anio_ant = anio;
            }//endwhile
             xmlList += "\n</node>\n";
             xmlList += "</node>\n";


         } catch (SQLException e) {
                System.out.println("Error en query: generaFecha - "+e.toString());
         } finally {
                Conexion.close(c);
         }
            return xmlList;
        }


}// FIN CLASE
