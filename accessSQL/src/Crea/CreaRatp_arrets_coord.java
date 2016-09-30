package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;

public class CreaRatp_arrets_coord {

	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern = Pattern.compile("[//;]");
			String[] result = pattern.split(line2);
			
			// ici on a besoin de savoir la longueur du résultat car toute les colonnes de sont pas remplies
			
			int lg = result.length;

			if (lg == 5) { 
				//si la longueur est 5, tout est rempli et donc on remplit tout
				String id = result[0];
				String x_m = result[1];
				String y_m = result[2];
				String x_r = result[3];
				String y_r = result[4];			        	

				pstmt.setString(1, id);
				pstmt.setString(2, x_m);
				pstmt.setString(3, y_m);
				pstmt.setString(4, x_r);
				pstmt.setString(5, y_r);
				pstmt.executeUpdate();

				line = br.readLine();

			} else if (lg == 3) {
				//si la longueur est 3, seule les 3 premières colonnes sont à replir, les deux dernières restent vide
				String id = result[0];
				String x_m = result[1];
				String y_m = result[2];			        	

				pstmt.setString(1, id);
				pstmt.setString(2, x_m);
				pstmt.setString(3, y_m);
				pstmt.setString(4,"");
				pstmt.setString(5,"");
				pstmt.executeUpdate();	

				line = br.readLine();

			} else {
				//si la longueur est autre (c'est à dire égale à 1), on a ue l'identifiant et pas de coordonnées
				String id = result[0];

				pstmt.setString(1, id);
				pstmt.setString(2,"");
				pstmt.setString(3,"");
				pstmt.setString(4,"");
				pstmt.setString(5,"");
				pstmt.executeUpdate();
				line = br.readLine();
				
			}
		}	   
	}
}

