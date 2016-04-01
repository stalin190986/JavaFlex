
package Servicios;

/**
 * Representa la conexion a la base de datos
 * @version 2.3
 * @author Arturo Hernándeez Peter
 */

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

public class Conexion {
    private String url;
	private static Conexion instance;
        private String user;
        private String pass;
        private String puerto;
        private String base;
        private static List<String> servidor;
        private String servidor_consulta;

        private final String USERPASS = "root";
        private final String USERPASSMAGDA = "ett";
        private final String SERVERPROD = "127.0.0.1";
        private final String SERVERMAGDA = "132.142.160.139";
        private final String SERVERDESA = "127.0.0.1";

	private Conexion()
	{
            try {

                 servidor = new ArrayList();

                  getInterfaces();

                  Class.forName("com.mysql.jdbc.Driver");

                  puerto="3309";
                  base="BI_DWH";
                  servidor_consulta="";
                  if(buscarServidor(SERVERMAGDA)==true)
                  {
                        user=USERPASSMAGDA;
                        pass=USERPASSMAGDA;
                        servidor_consulta=SERVERMAGDA;
                  }
                  else
                  {
                      if(buscarServidor(SERVERPROD)==true || buscarServidor(SERVERDESA))
                      {
                            user=USERPASS;
                            pass="";
                            servidor_consulta=SERVERPROD;
                      }
                      else
                      {
                            servidor_consulta=SERVERPROD;
                            user=USERPASS;
                            pass="";
                      }

                  }

                  /*
                  if(servidor.compareTo(SERVERMAGDA)==0)
                  {
                    user=USERPASSMAGDA;
                    pass=USERPASSMAGDA;
                  }
                  else if(servidor.compareTo(SERVERPROD)==0 || servidor.compareTo(SERVERDESA)==0){
                      user=USERPASS;
                      pass=USERPASS;
                  }else{
                      servidor = SERVERPROD;
                      user=USERPASS;
                      pass=USERPASS;
                  }
                  */

                  url = "jdbc:mysql://"+servidor_consulta+":"+puerto+"/"+base;
                  //System.out.println(url);
            } catch (Exception e) {
                    System.out.println(e.toString());
            }
	}

public static void  getInterfaces ()
{
         // int i=0;
          //int j=0;
          int cantidad_octetos=0;
          String octeto;
       try {
         Enumeration e = NetworkInterface.getNetworkInterfaces();
         while(e.hasMoreElements())
         {
            NetworkInterface ni = (NetworkInterface) e.nextElement();
            //System.out.println("Net interface: "+ni.getName());
            Enumeration e2 = ni.getInetAddresses();

            while (e2.hasMoreElements())
            {
               InetAddress ip = (InetAddress) e2.nextElement();
                // System.out.println(ip.toString());
               /*if(j==1 && i==1){
                  servidor = ip.toString().substring(1);
               }*/

                //System.out.println(ip.getHostAddress());
                octeto = ip.getHostAddress().toString();
                cantidad_octetos = octeto.length();
               if(cantidad_octetos <= 15)
               {
                    servidor.add(ip.getHostAddress());
                    //System.out.println("Direccion IP: "+ ip.getHostAddress());
               }
              // j++;
            }
         //   i++;
         }
       }
       catch (Exception e) {
          e.printStackTrace();
       }

} // getInterfaces

public static boolean buscarServidor(String ip_servidor)
{
    boolean retorno = false;
    if(ip_servidor.equalsIgnoreCase("127.0.0.1") == true)
        retorno = true;
    else
    {
        for (String ips : servidor)
        {
        if(ips.compareTo(ip_servidor)==0)
            retorno = true;
        }
        retorno = false;
    }
    return retorno;
}//FIN FIN buscarServidor

        public static Connection getConnection() throws SQLException
        {
            if (instance == null) {
                    instance = new Conexion();
            }
            try {
                    return DriverManager.getConnection(instance.url,instance.user,instance.pass);

            } catch (SQLException e) {
                    throw e;
            }
	}

        public static void close(Connection connection)
	{
            try {
                    if (connection != null) {
                            connection.close();
                    }
            } catch (SQLException e) {
                   System.out.println(e.toString());
            }
	}
}
