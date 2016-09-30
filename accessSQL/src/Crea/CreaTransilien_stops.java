package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;


public class CreaTransilien_stops {


	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		line = br.readLine();
		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern= Pattern.compile("[,]");
			String[] result=pattern.split(line2);
			int lg = result.length;

			String id_ligne= result[0];
			String nom= result[1];
			nom = nom.replaceAll("\"","");
			String lat= result[3];
			String lon= result[4];
			String type_ligne= result[7];

			String parent = "";

			if (lg == 9) { 
				parent= result[8];
			}

			pstmt.setString(1,id_ligne);
			pstmt.setString(2,nom);
			pstmt.setString(3,lat);
			pstmt.setString(4,lon);
			pstmt.setString(5,type_ligne);
			pstmt.setString(6,parent);

			pstmt.executeUpdate();
			line = br.readLine();

		}
	}

}
