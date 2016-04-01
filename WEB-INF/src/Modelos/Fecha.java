/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

import java.util.Calendar;
/**
 * Año, mes y fecha completa asi como los dias de un mes dado un año
 * @version 2.3
 * @author Arturo Hernándeez Peter
 *
 */
public class Fecha
{

    private String mes;
    private String anio;
    private String dia;
    private String fecha;
    private String fecha_ultimo_dia;



    private String mes_anterior;
    private String anio_anterior;
    private String dia_anterior;
    private String fecha_anterior;
    private String fecha_anterior_ultimo_dia;


    public Fecha()
    {

    }

    
    /**
     * @return the mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the dia
     */
    public String getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the mes_anterior
     */
    public String getMes_anterior() {
        return mes_anterior;
    }

    /**
     * @param mes_anterior the mes_anterior to set
     */
    public void setMes_anterior(String mes_anterior) {
        this.mes_anterior = mes_anterior;
    }

    /**
     * @return the anio_anterior
     */
    public String getAnio_anterior() {
        return anio_anterior;
    }

    /**
     * @param anio_anterior the anio_anterior to set
     */
    public void setAnio_anterior(String anio_anterior) {
        this.anio_anterior = anio_anterior;
    }

    /**
     * @return the dia_anterior
     */
    public String getDia_anterior() {
        return dia_anterior;
    }

    /**
     * @param dia_anterior the dia_anterior to set
     */
    public void setDia_anterior(String dia_anterior) {
        this.dia_anterior = dia_anterior;
    }



    
    /***************************************************************
     ************************OTRAS FUNCIONES **********************
     **************************************************************/

    public String obtenereFechaMensualAnterior(String fecha_actual)
    {
         String var_fecha_anterior;
         int var_anio_anterior;
         /// Ejemplo Fecha 1986-09-19
         var_anio_anterior = Integer.parseInt(fecha_actual.substring(0,4));
         var_anio_anterior = var_anio_anterior - 1;
         var_fecha_anterior = String.valueOf(var_anio_anterior) + fecha_actual.substring(4);
         

        return var_fecha_anterior;
    }// FIN obtenereFechaAnterior

    public String calcularFechaUltimoDia(String fecha, boolean es_mes_actual, int dia_maximo_carga)
    {

            String fecha_ultimo_dia = "";
            int anio_fecha = Integer.parseInt(fecha.substring(0,4));
            int mes_fecha  = Integer.parseInt(fecha.substring(5,7)) - 1;
            int dia_fecha =  Integer.parseInt(fecha.substring(8));
            

            Calendar fecha_calendario = Calendar.getInstance();
            fecha_calendario.set(
                                    anio_fecha
                                    , mes_fecha
                                    , dia_fecha
                                );

            ////System.out.println(" T1:: " + fecha_calendario.getTime().toString());
            /*Calendar calendario_dia_hoy = Calendar.getInstance();
            int dia_hoy = calendario_dia_hoy.get(Calendar.DAY_OF_MONTH) - 1;
            int mes_hoy = calendario_dia_hoy.get(Calendar.MONTH);
            int anio_hoy= calendario_dia_hoy.get(Calendar.YEAR);
            */
            if( es_mes_actual == true)
            {
                ///if(escoger_dia_maximo_carga == true)
                    dia_fecha = dia_maximo_carga;
                ///else
                ///    dia_fecha = dia_hoy;
            }
            else
                dia_fecha = fecha_calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
            

            fecha_calendario = Calendar.getInstance();
            fecha_calendario.set(anio_fecha
                                        , mes_fecha
                                        , dia_fecha
                                 );

            ////System.out.println(" T2:: " + fecha_calendario.getTime().toString());
            String nuevo_anio = String.valueOf(fecha_calendario.get(Calendar.YEAR));
            String nuevo_mes = "";
            int mes_nuevo = fecha_calendario.get(Calendar.MONTH) + 1;
            if( mes_nuevo <= 9)
                nuevo_mes += "0" + String.valueOf(mes_nuevo);
            else
                nuevo_mes += String.valueOf(mes_nuevo);

            int dia_nuevo = fecha_calendario.get(Calendar.DATE);
            String nuevo_dia = "";
            if(dia_nuevo <= 9)
                nuevo_dia += "0" + String.valueOf(dia_nuevo);
            else
                nuevo_dia += String.valueOf(dia_nuevo);

            fecha_ultimo_dia = nuevo_anio + "-" + nuevo_mes + "-" + nuevo_dia;

            ////System.out.println("fecha_actual:: " + fecha + "    fecha ultimo dia::  " + fecha_ultimo_dia);
            return fecha_ultimo_dia;
    }////FIN FIN calcularFechaUltimoDia


    
    //Considerar año bisiesto
    public static int getDiasMes(int mes,int anio){

        switch(mes){
            case 1: return 31;
            case 2:
                if(esAnioBisiesto(anio))
                    return 29;
                return 28;
            case 3: return 31;
            case 4: return 30;
            case 5: return 31;
            case 6: return 30;
            case 7: return 31;
            case 8: return 31;
            case 9: return 30;
            case 10: return 31;
            case 11: return 30;
            case 12: return 31;
            default: return 0;

        }

    }


    public static String getNombreMes(int mes){

        switch(mes){
            case 1: return "Enero";
            case 2: return "Febrero";
            case 3: return "Marzo";
            case 4: return "Abril";
            case 5: return "Mayo";
            case 6: return "Junio";
            case 7: return "Julio";
            case 8: return "Agosto";
            case 9: return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            case 12: return "Diciembre";
            default: return "";

        }

    }


    public static String setNombreMes(int mes){
        switch(mes){
            case 1: return "Ene";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Abr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Agos";
            case 9: return "Sept";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dic";
            default: return "";
        }
    }

    
 
    public static boolean esAnioBisiesto(int anio){

        if(anio%4==0)
            return true;
        return false;
    }


    /************************************************************
     * Obtiene la fecha anterior dada una fecha actual
     * @param fecha
     * @param esMensual
     * @return la fecha mensual o diaria
     */

    public String getAnterior(String fecha,boolean esMensual)
    {

         Calendar calendario = Calendar.getInstance();
         int diaHoy = calendario.get(Calendar.DAY_OF_MONTH);
         int mesActual = calendario.get(Calendar.MONTH)+1;
         int anioActual = calendario.get(Calendar.YEAR);

         String fechaDiaria;
         String fechaMensual;
         String fechaAnual;

         int anioFecha = Integer.parseInt(fecha.substring(0,4));
         int mesFecha = Integer.parseInt(fecha.substring(5,7));

         fechaAnual = String.valueOf(anioActual-1)+"-01-01";

        if(mesFecha>=0 && mesFecha<=9){
            fechaDiaria = String.valueOf(anioFecha-1)+"-0"+String.valueOf(mesFecha);
            fechaMensual = String.valueOf(anioFecha-1)+"-0"+String.valueOf(mesFecha)+"-01";
        }
        else{
            fechaDiaria = String.valueOf(anioFecha-1)+"-"+String.valueOf(mesFecha);
            fechaMensual = String.valueOf(anioFecha-1)+"-"+String.valueOf(mesFecha)+"-01";
        }

        if(diaHoy>=0 && diaHoy<=9)
            fechaDiaria += "-0"+String.valueOf(diaHoy-1);
        else
            fechaDiaria += "-"+String.valueOf(diaHoy-1);

         //fechaDiaria = String.valueOf(anioActual-1)+"-"+String.valueOf(mesActual)+"-"+String.valueOf(diaHoy);
         //fechaMensual = String.valueOf(anioActual-1)+"-"+String.valueOf(mesActual)+"-01";

         if(!esMensual)
         {
            if(anioFecha==anioActual)
            {
                 if(mesFecha==mesActual)
                    return fechaDiaria;
            }
         }

        return fechaMensual;
    }
    
    public String getAnteriorAnualIni(String fecha)
    {         
        String fechaAnual;
        int anioFecha = Integer.parseInt(fecha.substring(0,4));
        fechaAnual = String.valueOf(anioFecha-1)+"-01-01";

        return fechaAnual;
    }

   
    public boolean esFechaActual(String fecha){

         Calendar calendario = Calendar.getInstance();
         int mesActual = calendario.get(Calendar.MONTH)+1;
         int anioActual = calendario.get(Calendar.YEAR);

         int anioFechaFin = Integer.parseInt(fecha.substring(0,4));
         int mesFechaFin = Integer.parseInt(fecha.substring(5,7));

         if(anioFechaFin==anioActual)
         {
             if(mesFechaFin==mesActual)
             {
                  return true;
             }
         }

        return false;
    }

  /*  public String getFechaAnual(){

    }*/

     public static int getDiasPeriodo(String fechaIni,String fechaFin,String fechaMax){
        int dias=0;
        int mesFechaIni;
        int anioFechaIni;
        int mesFechaFin;
        int anioFechaFin;
        int mesActual;
        int diasMesActual;

        int i,j,k,t;

        Calendar fechaActual = Calendar.getInstance();

        mesFechaIni = Integer.parseInt(fechaIni.substring(5,7));
        mesFechaFin = Integer.parseInt(fechaFin.substring(5,7));

        anioFechaIni = Integer.parseInt(fechaIni.substring(0, 4));
        anioFechaFin = Integer.parseInt(fechaFin.substring(0, 4));

        //obtenemos el mes actual
        mesActual = fechaActual.get(Calendar.MONTH)+1;
        //diasMesActual = fechaActual.get(Calendar.DAY_OF_MONTH);
        diasMesActual = Integer.parseInt(fechaMax.substring(8,10));
        
        
        //los años
        i = anioFechaIni;
        //los meses
        j = mesFechaIni;
        k = mesFechaFin;

        //el resultado
        t = 0;

        while(i<=anioFechaFin){

            if(anioFechaIni==anioFechaFin){
                while(j<=mesFechaFin){

                    //si estamos en el mes actual
                    if(j==mesActual)
                        dias = dias + diasMesActual-1;
                    else
                        dias = dias + getDiasMes(j,i);
                    //suma de los dias
                    j++;
                }
            }
            else if(anioFechaIni!=anioFechaFin&&anioFechaIni==anioFechaFin-1){
                while(j<=(13-mesFechaIni)){

                    //si estamos en el mes actual
                    //suma de los dias
                    dias = dias + getDiasMes(j,i);
                    j++;

                }
                anioFechaIni=anioFechaFin;
            }
            else{
                if(esAnioBisiesto(i))
                    dias = dias +366;
                else
                    dias = dias +365;
                anioFechaIni++;
            }

            i++;
            j=0;

        }

        return dias;
    }


    public static String separarFechaDiariaAct(String fechaDiaria)
    {
        String []fechaResultante = new String[2];
        fechaResultante = fechaDiaria.split(";");

        return fechaResultante[0];
    }

    public static String separarFechaDiariaAnt(String fechaDiaria)
    {
        String []fechaResultante = new String[2];
        fechaResultante = fechaDiaria.split(";");

        return fechaResultante[1];
    }

    public static String separarUltimoDia(String fechaDiaria)
    {
        String []fechaResultante = new String[fechaDiaria.split(",").length];
        fechaResultante = fechaDiaria.split(",");

        return fechaResultante[fechaDiaria.split(",").length-1];
    }

    public static int contarDiasFechaDiaria(String fechaDiaria){
        int dias=0;

        String []fechaResultante = new String[2];
        fechaResultante = fechaDiaria.split(";");

        String []diasFecha = fechaResultante[0].split(",");

        return diasFecha.length;

    }

    /**
     * @return the fecha_anterior
     */
    public String getFecha_anterior() {
        return fecha_anterior;
    }

    /**
     * @param fecha_anterior the fecha_anterior to set
     */
    public void setFecha_anterior(String fecha_anterior) {
        this.fecha_anterior = fecha_anterior;
    }

    /**
     * @return the fecha_ultimo_dia
     */
    public String getFecha_ultimo_dia() {
        return fecha_ultimo_dia;
    }

    /**
     * @param fecha_ultimo_dia the fecha_ultimo_dia to set
     */
    public void setFecha_ultimo_dia(String fecha_ultimo_dia) {
        this.fecha_ultimo_dia = fecha_ultimo_dia;
    }

    /**
     * @return the fecha_anterior_ultimo_dia
     */
    public String getFecha_anterior_ultimo_dia() {
        return fecha_anterior_ultimo_dia;
    }

    /**
     * @param fecha_anterior_ultimo_dia the fecha_anterior_ultimo_dia to set
     */
    public void setFecha_anterior_ultimo_dia(String fecha_anterior_ultimo_dia) {
        this.fecha_anterior_ultimo_dia = fecha_anterior_ultimo_dia;
    }

    

    
}// FIN CLASE FECHA
