import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Crea.*;


public class Projet {

	public static void main(String args[]){ 	 


		/**
		 * Creation des bases de données
		 */
		//String[] nom_col = {"stop_id","x_metromap","y_metromap","x_rermap","y_rermap"};   		//ratp_arrets_coord
		//String[] nom_col = {"identifiant","x","y","nom","commune","reseau"};						//ratp_arrets
		//String[] nom_col = {"identifiant","ligne","terminus","type"};				       			//ratp_lignes
		//String[] nom_col = {"id_ligne","id_agence","nom_ligne","type_ligne"};						//*_routes
		//String[] nom_col = {"id_arret","nom","lat","long",type_ligne,"station_parent"};			//*_stops
		//String[] nom_col = {"trip_id","arrival_time","departure_time","stop_id","stop_sequence"};	//*_stop_times
		//String[] nom_col = {"id_ligne","id_service","id_voyage","nom","direction"};				//*_trips
		//String[] nom_col = {"id_ligne","id_station","ordre","temps_next"};						//*_seq
		//CreaDataBase.CreaData("transilien_seq", nom_col,true);									//true = nouvelle table, false = table déjà existante (et donc à drop)
		//CreaCorrespondances.CreaCorres(false);	//true = nouvelle table, false = table déjà existante (et donc à drop)				
		
		/**
		 * Séquencement des gares d'une ligne
		 */		
		//Séquencement.Seq("metro", "7", false, 0);

		/**
		 * Séquencement et modification de la table ratp_lignes pour rer/metro/tram
		 */
		//AlterRatp_lignes.AlterData("sens1", "sens2");


		/**
		 * modification des noms
		 */
		//CreamodifDesNoms.ModifNoms();


		/**
		 * tests
		 */

//		Noeud N1 = new Noeud("1884","B");
//		Noeud[] v = new Noeud[10000];
//		v[0]=N1;
//		int place = 1;
//		int place_avant = 0;
//		int tours = 0;
//
//
//		while ((place<10000)&(tours<4)) {
//			int place_temp = place;
//
//			for (int i = place_avant; i<place;i++) {
//				place = Chemin_ratp.add_ligne(v[i], v, place);
//				place = Chemin_ratp.add_correspondance(v[i], v, place);
//			}
//			place_avant = place_temp;
//
//			tours++;
//		}
//
//		for (int i =0; i<9999; i++) {
//			if (v[i]!=null){
//				v[i].print();
//				System.out.println(i);
//			}
//		}
//
//		graphe g = new graphe(N1);
		//		
		//		System.out.print(g.djinska(v[32],33));

//				Noeud N1 = new Noeud("A");
//				Noeud N2 = new Noeud("B");
//				Noeud N3 = new Noeud("C");
//				Noeud N4 = new Noeud("D");
//				Noeud N5 = new Noeud("E");
//				Noeud N6 = new Noeud("F");
//				Noeud N7 = new Noeud("G");
//				
//				Noeud[] v = new Noeud[7];
//				v[0] = N1;
//				v[1] = N2;
//				v[2] = N3;
//				v[3] = N4;
//				v[4] = N5;
//				v[5] = N6;
//				v[6] = N7;
//				
//				
//				v[0].add_fils(N2, 2);
//				v[0].add_fils(N3, 1);
//				
//				v[1].add_fils(N1, 2);
//				v[1].add_fils(N4, 2);
//				
//				v[2].add_fils(N1, 1);
//				v[2].add_fils(N4, 5);
//				v[2].add_fils(N6, 12);
//				
//				v[3].add_fils(N2, 2);
//				v[3].add_fils(N3, 5);
//				v[3].add_fils(N5, 4);
//				v[3].add_fils(N6, 2);
//				
//				v[4].add_fils(N4, 4);
//				v[4].add_fils(N7, 2);AV
//				
//				v[5].add_fils(N3, 12);
//				v[5].add_fils(N4, 2);
//				v[5].add_fils(N7, 2);
//				
//				v[6].add_fils(N5, 2);
//				v[6].add_fils(N6, 1);
//		//		
//				graphe g = new graphe(N1);
//				
//				System.out.print(g.djinska("F",7));

//		System.out.print(g.djinska("50399",place));
		
		//String transport_dispo = "(\"bus\",\"tram\",\"metro\",\"intercite\",\"ter\",\"rer\")";
		//String reseau_dispo = "(\"sncf\",\"ratp\",\"melange\")";
		
//		Chemin_ratp.chemin("1649", "7", "1815",1); //1 = moins corres, 2 = plus rapide, 3 = plus cours //villejuif-arragon -> mairie d'yvry
//		Chemin_ratp.chemin("1920", "B", "1668",1);		
//		Chemin_ratp.chemin("1626", "B", "1668",2);	
//		Chemin_ratp.chemin("1981", "4", "1685",1);
//		Chemin_ratp.chemin("1862", "10", "1746",1); //ligne 10 Javel-Andre-Citroen-> Mirabeau
//		Chemin_ratp.chemin("2018", "10", "1836",1); //ligne10 bologne jean jores -> Michel-Ange-Auteuil
//		Chemin_ratp.chemin("2018", "1717",1); //ligne10 bologne jean jores -> porte d'auteuil
//		Chemin_ratp.chemin("1848", "1754",1); //glacière(6) -> ménilmontant(2)
		
//		Chemin_ratp.chemin("171507", "1818",1); //Olympiade (14) -> Mairie de Saint-Ouen(13)
		//Trouve_chemin.chemin("1813","ratp" "14", "1804",1); //Olympiade (14) -> Liège(13)
		//Trouve_chemin.chemin("StopArea:OCE87723429", "ter", "1884", "ratp", 1, transport_dispo, reseau_dispo);
		
//		Chemin_ratp.chemin("1967", "1884",1);
		
		//Chemin_ratp.chemin("171507", "1818",1);
		
		
		
		
//		Noeud N1 = new Noeud("142803","A");
//		Noeud N2 = new Noeud("142801","A");
//		System.out.println(N1.calcul_distance(N2));
		

		
	}


}
