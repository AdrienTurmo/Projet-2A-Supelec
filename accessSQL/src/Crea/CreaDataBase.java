package Crea;
import java.io.*;
import java.sql.*;

public class CreaDataBase {
	/**
	 * Ici on construit les bases de données, on a les différents appels SQL de base effectués ici
	 * chaque base de données a sa propre construction, chacune ce situa dans une classe Crea* 
	 */

	public static void CreaData(String nom_table, String[] nom_col,boolean nouvelle) {

		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con = null;
		Statement stmt = null;
		Statement stmt2 = null;

		int nb_col = nom_col.length;	//nombre de colonne de la base de données

		try {

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			stmt2 = con.createStatement();

			if (!nouvelle) {
				stmt.execute("drop table "+nom_table);
			}


			// les trois chaines de caractères suivantes permettent de créer les table de façon générique
			String col1 = "";
			String col2 = "";
			String values = "values(";			
			for (int i = 0; i<nb_col-1;i++){
				col1 = col1 + nom_col[i]+" varchar(50),";
				col2 = col2 + nom_col[i]+", ";
				values = values+"?,";
			}
			col1 = col1 + nom_col[nb_col-1]+" varchar(50)";
			col2 = col2 + nom_col[nb_col-1];
			values = values + "?)";


			stmt.execute("create table "+nom_table+"("+col1+")");
			PreparedStatement pstmt = con.prepareStatement("Insert into "+nom_table+"("+col2+") " + values);
			
			
			/**
			 * 
			 * non séquencement
			 * 
			 */
			if (!nom_table.endsWith("seq")) {

				try	{

					File f = new File ("C:\\Users\\Adrien\\Desktop\\Projet log\\ProjetSeq8A2B7\\datasets\\toutxcel\\"+nom_table+".csv");
					FileReader fr = new FileReader (f);
					BufferedReader br = new BufferedReader (fr);

					try {
						String line = br.readLine();
						// on appelle la classe homonyme correspodant à la création de la base de données
						Class<?> cl = Class.forName("Crea.Crea"+nom_table.substring(0,1).toUpperCase()+nom_table.substring(1)); //on a une petite modif à faire à case d'une majuscule
						Class<?>[] cArg = {String.class,PreparedStatement.class,BufferedReader.class};
						// on appelle la méthode de construction
						Object[] arg = {line,pstmt,br};
						cl.getMethod("crea",cArg).invoke(null, arg);

					}
					catch (IOException exception)
					{
						System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
					}			

				}
				catch (FileNotFoundException exception)
				{
					System.out.println ("Le fichier n'a pas été trouvé");
				}

				/**
				 * 
				 * séquencement sncf
				 * 
				 */
			} else {
				
				String type = nom_table.substring(0, nom_table.length()-4);
				ResultSet routes = stmt2.executeQuery("SELECT id_ligne FROM "+type+"_routes");

				while (routes.next()) {
					String[][] donnees = Séquencement.Seq(type, routes.getString(1));

					for (int i = 0;i<donnees[0].length;i++){
						String id_route = routes.getString(1);
						String id_station = donnees[0][i];
						String ordre = ""+i;
						String temps_next = donnees[1][i];

						pstmt.setString(1,id_route);
						pstmt.setString(2,id_station);
						pstmt.setString(3,ordre);
						pstmt.setString(4,temps_next);

						pstmt.executeUpdate();												
					}


				}			
			}

		} 
		// gestion des cas d'erreur
		catch(Exception e) 
		{ System.err.println(  e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close(); 
				stmt2.close(); 
				con.close();
			} 
		catch(Exception e){}



	}
}
