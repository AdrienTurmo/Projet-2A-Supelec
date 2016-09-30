

package Crea;

import java.sql.*;
import java.util.regex.Pattern;

public class CreamodifDesNoms {

	public static void ModifNoms(){
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con=null;
		Statement stmt= null;
		PreparedStatement pstmt= null;


		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			pstmt = con.prepareStatement("UPDATE ratp_arrets SET nom= ? WHERE identifiant= ?");
			
			String[] chara = {"â" , "é" , "è", "ï", "ë", "ù"};
			String[] charabis = {"a", "e" , "e" , "i" , "e" ,"u"};

			for (int i= 0; i<chara.length; i++){
				ResultSet station =
stmt.executeQuery("SELECT identifiant,nom FROM ratp_arrets WHERE nom LIKE \"%" + chara[i] + "%\"");
				String[] nomtemp;
				String nom2;
				System.out.println(chara[i]);
				while (station.next()){
					String ident = station.getString(1);
					String nom = station.getString(2);
					Pattern pattern= Pattern.compile(chara[i]);
					nomtemp = pattern.split(nom);
					nom2 = nomtemp[0];
					if (nomtemp.length == 1){
						nom2 = nom2 + charabis[i];
					}else{

						for(int j=1; j<nomtemp.length; j++){
							nom2 = nom2+charabis[i] + nomtemp[j] ;
						}
					}
					System.out.println(nom + "->"+nom2);
					pstmt.setString(1, nom2);
					pstmt.setString(2, ident);
					pstmt.executeUpdate();
				}
			}
			
		}
		catch(Exception e) 
		{ System.err.println(  e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close(); 
				pstmt.close();
				con.close();
			} 
		catch(Exception e){}
	}
}


