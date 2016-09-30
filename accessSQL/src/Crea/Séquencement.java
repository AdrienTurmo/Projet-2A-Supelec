package Crea;
import java.sql.*;
import java.util.regex.Pattern;

public class Séquencement {

	public static void Seq(String type,String ligne, boolean modif, int numterm) {

		// initialisation des connections		 
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con=null;
		Statement stmt= null;
		Statement stmt2= null;
		Statement stmt3= null;
		Statement stmt4= null;

		try	{

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();	
			stmt2 = con.createStatement();
			stmt3 = con.createStatement();
			stmt4 = con.createStatement();

			/**
			 * 
			 * ratp
			 * 
			 * **/
			if ((type == "metro")||(type == "rer")||(type == "tram")||(type == "bus")) {

				// on extrait tous les identifiants des stations d'une ligne
				ResultSet id_term = stmt.executeQuery("SELECT identifiant, terminus FROM ratp_lignes WHERE (type =  \""+type+"\")&(ligne = \""+ligne+"\")");
				String terminus = id_term.getString(2);

				// on prend le nom d'un des terminus
				Pattern pattern= Pattern.compile("[/]");
				String[] terminus2 = pattern.split(terminus);
				String term = terminus2[numterm];
				term = term.trim();
				System.out.println(term + "\n");
				numterm++;


				// on enlève du nom les caratères génants
				term = term.replaceAll("é","%");
				term = term.replaceAll("è","%");
				term = term.replaceAll("â","%");
				term = term.replaceAll("ç","%");
				term = term.replaceAll(" - ","%");
				term = term.replaceAll("-","%");
				term = term.replaceAll(" ","%");
				term = term.replaceAll("\\(","%");
				term = term.replaceAll("\\)","%");

				int[] id = new int[100];        // contient les identifiants des stations
				Double[] x = new Double[100];   // contient les coordonnées en x des stations
				Double[] y = new Double[100];   // contient les coordonnées en y des stations
				String[] nom = new String[100]; // contient les noms des stations
				int nb_station = 0;             // contient le nombre de stations


				// on retrouve les données x,y,nom à partir des identifiants des stations
				while(id_term.next()){

					ResultSet xy = stmt2.executeQuery("SELECT identifiant, x, y, nom FROM ratp_arrets WHERE identifiant = " +id_term.getString(1));

					id[nb_station]=  Integer.parseInt(xy.getString(1));
					x[nb_station]= Double.parseDouble(xy.getString(2));
					y[nb_station]= Double.parseDouble(xy.getString(3));
					nom[nb_station]=xy.getString(4);

					nb_station++;
				}
				// System.out.println("Terminus potentiels trouvés");


				// on va retrouver le séquencement des stations
				String[] ordre = new String[nb_station]; // contient les noms/identifiants/... dans l'ordre à partir d'un terminus

				int gare_actuelle = 0; // la gare dont on cherche la suivante						

				// on retrouve tous les identifiants des stations portant le nom du terminus voulu			
				ResultSet id_terminus = stmt3.executeQuery("SELECT identifiant FROM ratp_arrets WHERE nom LIKE  \""+term+"%\"");

				int[] id_termin = new int[100];		//tous les identifiants des stations du meme nom que le terminus
				int indice = 0;						//indice du vecteur précédent
				int nb_station_trouve = 0;			//nombre de station pouvant être un terminus
				String[] nom_trouve = new String[10];	//nom des stations pouvant être un terminus et ce trouvant sur la ligne voulu
				int[] id_trouve = new int[10];		//identifiants des stations pouvant être un terminus


				while(id_terminus.next()){			                	  	
					id_termin[indice] = Integer.parseInt(id_terminus.getString(1));
					//on cherche seulement les terminus possibles parmis la station
					for( int i =0; i<nb_station; i++){
						if (id_termin[indice]==id[i]){
							System.out.println(id[i]+" "+ nom[i] +"\n");
							nom_trouve[nb_station_trouve]=nom[i];
							id_trouve[nb_station_trouve] = id[i];
							nb_station_trouve++;
						}
					}
					indice++;
				}
				System.out.println(nb_station_trouve +" station(s) possible comme terminus\n"); // si il y a plus d'un terminus possible il y a risque d'erreur

				// initialisation du séquencement avec le terminus (on le reperd dans le tableau)
				for (int j = 0; j<nb_station; j++) {
					if (id[j] == id_trouve[0]) {
						ordre[0] = nom[j];
						id[j] = 0;
						gare_actuelle = j;				 
					}
				}

				if (modif) {
					stmt4.executeUpdate("UPDATE ratp_lignes SET sens"+numterm+"="+0+" WHERE (identifiant =  \""+id_trouve[0]+"\")&(ligne = \""+ligne+"\")");
				}

				// pour toute les stations on reperd la gare la plus proche en (x,y), on la retire, et on recommence avec la gare suivante repérée
				for (int j=1;j<nb_station;j++) {
					Double d = 10E10;	// distance de la gare la plus proche
					int next_gare = 0;	// indice de la gare la plus proche
					for (int k = 0; k<nb_station; k++ ) {
						if (id[k]!=0){
							Double d_g = Math.sqrt((x[gare_actuelle]-x[k])*(x[gare_actuelle]-x[k])+(y[gare_actuelle]-y[k])*(y[gare_actuelle]-y[k]));
							if (d_g<d){
								next_gare = k;
								d = d_g;
							}
						}								
					}				
					gare_actuelle = next_gare;
					ordre[j] = nom[gare_actuelle];
					if (modif) {
						stmt4.executeUpdate("UPDATE ratp_lignes SET sens"+numterm+"="+j+" WHERE (identifiant =  \""+id[gare_actuelle]+"\")&(ligne = \""+ligne+"\")");
					}
					id[gare_actuelle]=0;				
				}

				// affichage
				for (int l = 0; l<nb_station;l++){
					System.out.println("Gare n°"+(l+1)+" : "+ordre[l]+"\n");
				}



				/**
				 * 
				 * sncf
				 * 
				 * **/
			} else if ((type == "transilien")||(type == "intercite")||(type == "ter")) {


				// on extrait tous les identifiants des trajets d'une route
				ResultSet id_trip = stmt.executeQuery("SELECT id_voyage FROM "+type+"_trips WHERE (id_ligne = \""+ligne+"\")");


				int nb_stations_max = 0;
				String trip_ref = "";

				while(id_trip.next()){	
					ResultSet id_station = stmt2.executeQuery("SELECT stop_sequence FROM "+type+"_stop_times WHERE (trip_id = \""+id_trip.getString(1)+"\")"); 
					int nb_stations =0;
					while(id_station.next()){
						nb_stations++;												
					}	
					if(nb_stations>nb_stations_max){
						nb_stations_max = nb_stations;
						trip_ref = id_trip.getString(1);
					}
				}


				String[] arrivee = new String[nb_stations_max];
				String[] depart = new String[nb_stations_max];
				String[] station = new String[nb_stations_max];
				String[] temps = new String[nb_stations_max];
				ResultSet trip_ref_RS = stmt3.executeQuery("SELECT arrival_time, departure_time, stop_id FROM "+type+"_stop_times WHERE (trip_id = \""+trip_ref+"\")");

				int ind = 0;

				while (trip_ref_RS.next()) {
					arrivee[ind] = trip_ref_RS.getString(1);
					depart[ind] = trip_ref_RS.getString(2);
					station[ind] = trip_ref_RS.getString(3);					
					ind++;
				}

				for (int i = 0;i<nb_stations_max-1;i++){
					Pattern pattern= Pattern.compile("[:]");
					String[] t_d = pattern.split(depart[i]);
					String[] t_a = pattern.split(arrivee[i+1]);
					temps[i] = ""+((Integer.parseInt(t_a[0])-Integer.parseInt(t_d[0]))*60+Integer.parseInt(t_a[1])-Integer.parseInt(t_d[1]));
				}
				temps[nb_stations_max-1]="0";


				for (int i = 0;i<nb_stations_max;i++){
					System.out.println("station "+i+" : "+station[i]+" temps à la suivante : "+temps[i]);
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
				stmt3.close(); 
				stmt4.close(); 				
				con.close();
			} 
		catch(Exception e){}  


	}





	public static String[][] Seq(String type,String ligne) {

		String result2[][] = new String[1][1];


		// initialisation des connections		 
		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con=null;
		Statement stmt= null;
		Statement stmt2= null;
		Statement stmt3= null;

		try	{

			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();	
			stmt2 = con.createStatement();
			stmt3 = con.createStatement();


			// on extrait tous les identifiants des trajets d'une route
			ResultSet id_trip = stmt.executeQuery("SELECT id_voyage FROM "+type+"_trips WHERE (id_ligne = \""+ligne+"\")");


			int nb_stations_max = 0;
			String trip_ref = "";

			while(id_trip.next()){	
				ResultSet id_station = stmt2.executeQuery("SELECT stop_sequence FROM "+type+"_stop_times WHERE (trip_id = \""+id_trip.getString(1)+"\")"); 
				int nb_stations =0;
				while(id_station.next()){
					nb_stations++;												
				}	
				if(nb_stations>nb_stations_max){
					nb_stations_max = nb_stations;
					trip_ref = id_trip.getString(1);
				}
			}


			String[] arrivee = new String[nb_stations_max];
			String[] depart = new String[nb_stations_max];
			String[] station = new String[nb_stations_max];
			String[] temps = new String[nb_stations_max];
			ResultSet trip_ref_RS = stmt3.executeQuery("SELECT arrival_time, departure_time, stop_id FROM "+type+"_stop_times WHERE (trip_id = \""+trip_ref+"\")");

			int ind = 0;

			while (trip_ref_RS.next()) {
				arrivee[ind] = trip_ref_RS.getString(1);
				depart[ind] = trip_ref_RS.getString(2);
				station[ind] = trip_ref_RS.getString(3);					
				ind++;
			}

			for (int i = 0;i<nb_stations_max-1;i++){
				Pattern pattern= Pattern.compile("[:]");
				String[] t_d = pattern.split(depart[i]);
				String[] t_a = pattern.split(arrivee[i+1]);
				temps[i] = ""+((Integer.parseInt(t_a[0])-Integer.parseInt(t_d[0]))*60+Integer.parseInt(t_a[1])-Integer.parseInt(t_d[1]));
			}
			temps[nb_stations_max-1]="0";


			String result[][] = new String[2][nb_stations_max];

			result[0] = station;
			result[1] = temps;

			return result;

		}		
		// gestion des cas d'erreur
		catch(Exception e) 
		{ System.err.println(  e.getMessage()); }
		if (con!=null) 
			try {
				stmt.close();
				stmt2.close(); 
				stmt3.close(); 
				con.close();
			} 
		catch(Exception e){} 

		return result2;

	}
}
