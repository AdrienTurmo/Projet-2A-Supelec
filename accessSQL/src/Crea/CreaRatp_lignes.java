package Crea;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;

public class CreaRatp_lignes {

	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		while (line != null ) {

			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern = Pattern.compile("[#(#]");
			String[] result = pattern.split(line2);
			int lg = result.length;

			String id = result[0];
			String ligne = result[1];
			String terms ="";
			//on a besoin de contrôler la longueur du résultat du patern car certain terminus comportent des parenthèses dans leur nom
			//cela créée alors une séparation supplémentaire parasite, que l'on élimine par un "collage"
			if (lg == 4) {
				terms = result[2];
			} else {
				terms = result[2]+result[3];
			}
			String type = result[lg-1];

			Pattern p = Pattern.compile("[)]") ;
			java.util.regex.Matcher m = p.matcher(terms) ;	        	  
			String terms2 = m.replaceAll("") ;

			Pattern p2 = Pattern.compile("[ ]") ;
			java.util.regex.Matcher m2 = p2.matcher(ligne) ;     	  
			String ligne2 = m2.replaceAll("") ;

			pstmt.setString(1, id);
			pstmt.setString(2, ligne2);
			pstmt.setString(3, terms2);
			pstmt.setString(4, type);
			pstmt.executeUpdate();
			line = br.readLine();

		}
	}
}
