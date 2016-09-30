package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;


public class CreaTer_trips {


	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		line = br.readLine();
		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern= Pattern.compile("[//,]");
			String[] result=pattern.split(line2);

			String id_ligne= result[0];
			String id_service= result[1];
			String id_voyage= result[2];
			String nom= result[3];
			nom = nom.replaceAll("\"","");
			String dir= result[4];

			pstmt.setString(1,id_ligne);
			pstmt.setString(2,id_service);
			pstmt.setString(3,id_voyage);
			pstmt.setString(4,nom);
			pstmt.setString(5,dir);

			pstmt.executeUpdate();
			line = br.readLine();

		}
	}
}