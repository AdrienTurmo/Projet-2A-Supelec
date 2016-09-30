package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;


public class CreaTer_routes {


	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		line = br.readLine();
		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern= Pattern.compile("[//,]");
			String[] result=pattern.split(line2);

			String id_route= result[0];
			String id_agence= result[1];
			String nom= result[3];
			nom = nom.replaceAll("\"","");
			String type= result[5];

			pstmt.setString(1,id_route);
			pstmt.setString(2,id_agence);
			pstmt.setString(3,nom);
			pstmt.setString(4,type);

			pstmt.executeUpdate();
			line = br.readLine();
			
		}
	}

}
