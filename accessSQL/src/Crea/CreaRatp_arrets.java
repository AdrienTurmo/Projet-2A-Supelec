package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;

public class CreaRatp_arrets {

	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern = Pattern.compile("[//#####]");
			String[] result = pattern.split(line2);
			String id = result[0];
			String x = result[1];
			String y = result[2];
			String nom = result[3];
			String commune = result[4];
			String reseau = result[5];

			pstmt.setString(1, id);
			pstmt.setString(2, x);
			pstmt.setString(3, y);
			pstmt.setString(4, nom);
			pstmt.setString(5, commune);
			pstmt.setString(6, reseau);
			pstmt.executeUpdate();
			line = br.readLine();

		}

	}
}
