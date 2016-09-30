package Crea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AlterRatp_lignes {
	
	public static void AlterData(String nom_col1, String nom_col2) {

		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;



		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
						
			//changement de nom d'une des stations car ça posait problème
			stmt.executeUpdate("UPDATE ratp_arrets SET nom = \"Les Courtilles Asnieres - Gennevilliers\" WHERE nom = \"Asnieres-Gennevilliers Les Courtilles\"");
			
			//rajout des colonnes à la base ratp_lignes
			stmt.executeQuery("ALTER TABLE ratp_lignes ADD \""+nom_col1+"\" varchar(50) , \""+nom_col2+"\" varchar(50)");
			
			
			//ligne metro 1-14
			for (int i =1;i<=14;i++){
				Séquencement.Seq("metro", ""+i, true, 0);
			}
			
			for (int i =1;i<=14;i++){
				Séquencement.Seq("metro", ""+i, true, 1);
			}
			
			
			//ligne metro 3B
			Séquencement.Seq("metro", "3B", true, 0);
			Séquencement.Seq("metro", "3B", true, 1);
			
			
			//ligne metro 7B
			Séquencement.Seq("metro", "7B", true, 0);
			Séquencement.Seq("metro", "7B", true, 1);					
				
									
			//ligne metro 13						
			for (int i =26; i<=32;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \""+i+"\")&(ligne = 13)");
			}
			for (int i =24; i<=32;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \""+i+"\")&(ligne = 13)");
			}
						
			
			//ligne metro 10
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \"15\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \"17\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \"19\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 15 WHERE (sens1 =  \"16\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 16 WHERE (sens1 =  \"18\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 17 WHERE (sens1 =  \"20\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 18 WHERE (sens1 =  \"21\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 19 WHERE (sens1 =  \"22\")&(ligne = 10)");
			
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \"2\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \"4\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \"6\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = 2 WHERE (sens2 =  \"3\")&(ligne = 10)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = 3 WHERE (sens2 =  \"5\")&(ligne = 10)");
			for (int i =7;i<=22;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+(i-3)+" WHERE (sens2 =  \""+i+"\")&(ligne = 10)");
			}
			
//			//ligne metro 14
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 2 WHERE (identifiant = \"50055\")&(ligne = \"14\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 3 WHERE (identifiant = \"2005\")&(ligne = \"14\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 4 WHERE (identifiant = \"1839\")&(ligne = \"14\")");
			
			//ligne metro 7
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \"4\")&(ligne = 7)");
			for (int i = 34;i<=37;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \""+i+"\")&(ligne = 7)");
			}
			for (int i = 5;i<=33;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+(i-1)+" WHERE (sens1 =  \""+i+"\")&(ligne = 7)");
			}
			
			for (int i = 34;i<=37;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \""+i+"\")&(ligne = 7)");
			}
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 12, sens2 = 13 WHERE (identifiant = \"1711\")&(ligne = 7)");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 11, sens2 = 12 WHERE (identifiant = \"1630\")&(ligne = 7)");
			
			
			//ligne metro 7B
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = -1 WHERE (sens1 =  \"5\")&(ligne = \""+7+"B"+"\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 5 WHERE (sens1 =  \"6\")&(ligne = \""+7+"B"+"\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 6 WHERE (sens1 =  \"7\")&(ligne = \""+7+"B"+"\")");
			
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = -1 WHERE (sens2 =  \"1\")&(ligne = \""+7+"B"+"\")");
			for (int i = 2;i<=7;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+(i-1)+" WHERE (sens2 =  \""+i+"\")&(ligne = \""+7+"B"+"\")");
			}			
			
			//ligne metro FUN (funiculaire)
			Séquencement.Seq("metro", "3B", true, 0);
			Séquencement.Seq("metro", "3B", true, 1);
			
			//ligne metro ORV
			Séquencement.Seq("metro", "3B", true, 0);
			Séquencement.Seq("metro", "3B", true, 1);
			
			
			//ligne tram T1
			Séquencement.Seq("tram", "T1", true, 0);
			Séquencement.Seq("tram", "T1", true, 1);
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 38 WHERE (identifiant =  \"142784\")&(ligne = \""+"T1"+"\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = 39 WHERE (identifiant =  \"142783\")&(ligne = \""+"T1"+"\")");
				
			//ligne tram T2
			Séquencement.Seq("tram", "T2", true, 0);
			Séquencement.Seq("tram", "T2", true, 1);
			
			//ligne tram T3A
			Séquencement.Seq("tram", "T3A", true, 0);
			Séquencement.Seq("tram", "T3A", true, 1);
			
			//ligne tram T3B
			Séquencement.Seq("tram", "T3B", true, 0);
			Séquencement.Seq("tram", "T3B", true, 1);
			
			//ligne tram T3
			Séquencement.Seq("tram", "T3", true, 0);
			Séquencement.Seq("tram", "T3", true, 1);
			
			
			//ligne rer A
			Séquencement.Seq("rer", "A", true, 0);
			Séquencement.Seq("rer", "A", true, 1);
			for (int i = 10;i<=18;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+"-1"+" WHERE (sens1 =  \""+i+"\")&(ligne = \""+"A"+"\")");
			}
			for (int i = 27;i<=35;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+"-1"+" WHERE (sens1 =  \""+i+"\")&(ligne = \""+"A"+"\")");
			}
			for (int i = 19;i<=26;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+(i-9)+" WHERE (sens1 =  \""+i+"\")&(ligne = \""+"A"+"\")");
			}
			
			for (int i = 24;i<=35;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+"-1"+" WHERE (sens2 =  \""+i+"\")&(ligne = \""+"A"+"\")");
			}
			
			//ligne rer B
			Séquencement.Seq("rer", "B", true, 0);
			Séquencement.Seq("rer", "B", true, 1);
			for (int i = 17;i<=19;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+"-1"+" WHERE (sens1 =  \""+i+"\")&(ligne = \""+"B"+"\")");
			}
			for (int i = 20;i<=31;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+(i-3)+" WHERE (sens1 =  \""+i+"\")&(ligne = \""+"B"+"\")");
			}
			stmt.executeUpdate("UPDATE ratp_lignes SET sens1 = "+"-1"+" WHERE (sens1 =  \""+32+"\")&(ligne = \""+"B"+"\")");
		
			for (int i = 4;i<=19;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+"-1"+" WHERE (sens2 =  \""+i+"\")&(ligne = \""+"B"+"\")");
			}
			for (int i = 20;i<=30;i++) {
				stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+(i-16)+" WHERE (sens2 =  \""+i+"\")&(ligne = \""+"B"+"\")");
			}
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+"-1"+" WHERE (sens2 =  \""+31+"\")&(ligne = \""+"B"+"\")");
			stmt.executeUpdate("UPDATE ratp_lignes SET sens2 = "+"15"+" WHERE (sens2 =  \""+32+"\")&(ligne = \""+"B"+"\")");
			
			
			
		} 
		// gestion des cas d'erreur
		catch(Exception e) 
		{ System.err.println(  e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close(); 
				con.close();
			} 
		catch(Exception e){}



	}
	
	
}
