

package Crea;

import java.sql.*;
import java.util.regex.Pattern;


public class CreaCorrespondances {

	public static void CreaCorres(boolean nouvelle){

		String database = ("jdbc:sqlite:SQLiteSpy.db3");
		Connection con=null;
		Statement stmt= null;
		Statement stmt2= null;
		Statement stmt3= null;
		Statement stmt4= null;
		try {

			String nom_table = "Correspondances";
			Class.forName("org.sqlite.JDBC");	
			con = DriverManager.getConnection(database, "", "");
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			stmt3 = con.createStatement();
			stmt4 = con.createStatement();
			String[] res = {"rer" , "metro" , "tram"};


			if (!nouvelle) {
				stmt.execute("drop table " + nom_table); //drop la table si elle n'est pas nouvelle
			}

			stmt.execute("create table "+ nom_table +"(ident1 varchar(30), ident2 varchar(30), type1 varchar(10), type2 varchar(10), temps varchar(3), reseau varchar(10))");
			PreparedStatement pstmt = con.prepareStatement("Insert into "+nom_table+"(ident1, ident2, type1, type2, temps, reseau) values(?,?,?,?,?,?)");

			/*
			 * correspondances inter ratp
			 */
			for (int i= 0; i<res.length; i++){
				ResultSet station = stmt2.executeQuery("SELECT identifiant,nom,x,y FROM ratp_arrets WHERE reseau = \"" + res[i] + "\"");

				while (station.next()){
					String ident1 = station.getString(1);
					String nom1 = station.getString(2);
					String type1 = res[i];
					Double x= Double.parseDouble(station.getString(3));
					Double y= Double.parseDouble(station.getString(4));
					String[] nomtemp;
					String[] nom2;

					if(Double.parseDouble(ident1) == 1967){
						Pattern pattern= Pattern.compile("[-]");
						nomtemp = pattern.split(nom1);
						nom2 = new String[nomtemp.length+1];
						nom2[0]=nom1;
						for(int h = 1; h<nom2.length;h++){
							nom2[h]=nomtemp[h-1];
						}
					}else if(Double.parseDouble(ident1) == 5604){
						nom2 = new String[2];
						nom2[0]=nom1;
						nom2[1] = "Saint Michel";
					}else if(Double.parseDouble(ident1) == 11492532){
						nom2 = new String[1];
						nom2[0]="Aéroport Charles-de-Gaulle";
					}else if(Double.parseDouble(ident1) == 1871){
						nom2 = new String[1];
						nom2[0]="Défense";
					}
					else{
						nom2 = new String[1];
						nom2[0]=nom1;
					}


					for (int j = 0 ; j<nom2.length; j++){

						String nom = nom2[j];
						nom = nom.trim();

						nom = nom.replaceAll("é","*");
						nom = nom.replaceAll("â","*");
						nom = nom.replaceAll("ç","*");
						nom = nom.replaceAll(" - ","%");
						nom = nom.replaceAll("-","%");
						nom = nom.replaceAll(" ","%");
						nom = nom.replaceAll("\\(","%");
						nom = nom.replaceAll("\\)","%");

						ResultSet id_corresp = stmt3.executeQuery("SELECT identifiant,reseau,x,y FROM ratp_arrets WHERE (nom LIKE  \"%"+nom+"%\") & (identifiant <> \"" + ident1+ "\")");

						while (id_corresp.next()){

							String ident2 = id_corresp.getString(1);
							String type2 = id_corresp.getString(2);
							double x1= Double.parseDouble(id_corresp.getString(3));
							double y1= Double.parseDouble(id_corresp.getString(4));
							double critere = 10.0;


							double distance = 2*Math.asin(Math.sqrt((Math.sin((y-y1)/2))*(Math.sin((y-y1)/2)) + Math.cos(y)*Math.cos(y1)*(Math.sin((x-x1)/2))*(Math.sin((x-x1)/2))));
							double temp = (distance*1000);

							String reseau = "ratp";
							if(temp<critere){
								if(temp<2.5){
									temp = (double) 4;
								}else{
									temp = Math.floor(temp*2);
								}
								String temps = Double.toString(temp);

								pstmt.setString(1,ident1);

								pstmt.setString(2,ident2);

								pstmt.setString(3,type1);

								pstmt.setString(4,type2);

								pstmt.setString(5,temps);

								pstmt.setString(6,reseau);


								pstmt.executeUpdate();

							}
						}
					}
				}
			}


			/*
			 * Correspondances inter sncf
			 */
			ResultSet reslt = stmt2.executeQuery("SELECT i.id_arret,i.nom,t.nom FROM intercite_stops i, ter_stops t WHERE i.type_ligne =\"1\" AND i.id_arret=t.id_arret");
			while (reslt.next()){
				String id_station = reslt.getString(1);


				String temps = "4.0";
				String type1 = "intercite";
				String type2 = "ter";
				String reseau = "sncf";

				pstmt.setString(1,id_station);
				pstmt.setString(2,id_station);
				pstmt.setString(3,type1);
				pstmt.setString(4,type2);
				pstmt.setString(5,temps);
				pstmt.setString(6,reseau);

				pstmt.executeUpdate();
			}

			ResultSet reslt1 = stmt2.executeQuery("SELECT id_arret FROM transilien_stops WHERE type_ligne =\"1\"");
			while (reslt1.next()){

				String id_station = reslt.getString(1);
				String id_commun = id_station.substring(12, id_station.length());
				String tp = "";

				ResultSet type = stmt3.executeQuery("SELECT rt.type_ligne,seq.id_station FROM transilien_routes rt,transilien_seq seq, transilien_stops stp WHERE rt.id_ligne = seq.id_ligne AND seq.id_station = stp.id_arret AND stp.station_parent = \"" + id_station +"\"");
				while (type.next()){
					String temp = type.getString(1);

					if(!temp.equals(tp)){
						tp=temp;
						String type1="";
						if(tp.equals("0")){
							type1="tram";
						}else if(tp.equals("1")){
							type1="metro";
						}else if(tp.equals("2")){
							type1="rer";
						}else if(tp.equals("3")){
							type1="bus";
						}
						ResultSet corres1 = stmt4.executeQuery("SELECT id_arret FROM ter_stops WHERE type_ligne =\"1\" AND id_arret LIKE \"%" + id_commun +"%\"");
						while(corres1.next()){
							String id = corres1.getString(1);


							String temps = "4.0";
							String type2 = "ter";
							String reseau = "sncf";


							pstmt.setString(1,id_station);

							pstmt.setString(2,id);

							pstmt.setString(3,type1);

							pstmt.setString(4,type2);

							pstmt.setString(5,temps);

							pstmt.setString(6,reseau);


							pstmt.executeUpdate();
						}

						ResultSet corres = stmt4.executeQuery("SELECT id_arret FROM intercite_stops WHERE type_ligne =\"1\" AND id_arret LIKE \"%" + id_commun +"%\"");
						while(corres.next()){
							String id = corres.getString(1);


							String temps = "4.0";
							String type2 = "intercite";
							String reseau = "sncf";


							pstmt.setString(1,id_station);

							pstmt.setString(2,id);

							pstmt.setString(3,type1);

							pstmt.setString(4,type2);

							pstmt.setString(5,temps);

							pstmt.setString(6,reseau);


							pstmt.executeUpdate();
						}
					}
				}
			}

			ResultSet reslt2 = stmt2.executeQuery("SELECT id_arret,nom FROM transilien_stops WHERE type_ligne =\"1\"");
			while (reslt2.next()){
				String id_station = reslt.getString(1);
				String id_commun = id_station.substring(12, id_station.length());
				String tp = "";

				ResultSet type = stmt3.executeQuery("SELECT rt.type_ligne FROM transilien_routes rt,transilien_seq seq, transilien_stops stp WHERE rt.id_ligne = seq.id_ligne AND seq.id_station = stp.id_arret AND stp.station_parent = \"" + id_station +"\"");
				while (type.next()){
					String temp = type.getString(1);

					if(!temp.equals(tp)){
						tp=temp;
						String type1="";
						if(tp.equals("0")){
							type1="tram";
						}else if(tp.equals("1")){
							type1="metro";
						}else if(tp.equals("2")){
							type1="rer";
						}else if(tp.equals("3")){
							type1="bus";
						}
						ResultSet corres = stmt4.executeQuery("SELECT id_arret,nom FROM intercite_stops WHERE type_ligne =\"1\" AND id_arret LIKE \"%" + id_commun +"%\"");
						while(corres.next()){
							String id = corres.getString(1);

							String temps = "4.0";
							String type2 = "intercite";
							String reseau = "sncf";


							pstmt.setString(1,id_station);

							pstmt.setString(2,id);

							pstmt.setString(3,type1);

							pstmt.setString(4,type2);

							pstmt.setString(5,temps);

							pstmt.setString(6,reseau);


							pstmt.executeUpdate();
						}
					}
				}
			}


			/*
			 * correspondances entre ratp et sncf
			 */

			ResultSet reslttrans = stmt2.executeQuery("SELECT id_arret,nom,long,lat FROM transilien_stops WHERE type_ligne =\"1\"");

			while (reslttrans.next()){

				String id_station = reslttrans.getString(1);
				String nom_arret = reslttrans.getString(2);
				Double x1= Double.parseDouble(reslttrans.getString(3));
				Double y1= Double.parseDouble(reslttrans.getString(4));
				String tp = "";

				String nom = nom_arret;
				nom = nom.trim();

				nom = nom.replaceAll("é","*");
				nom = nom.replaceAll("â","*");
				nom = nom.replaceAll("ç","*");
				nom = nom.replaceAll(" - ","%");
				nom = nom.replaceAll("-","%");
				nom = nom.replaceAll(" ","%");
				nom = nom.replaceAll("\\(","%");
				nom = nom.replaceAll("\\)","%");


				ResultSet type = stmt3.executeQuery("SELECT rt.type_ligne FROM transilien_routes rt,transilien_seq seq, transilien_stops stp WHERE rt.id_ligne = seq.id_ligne AND seq.id_station = stp.id_arret AND stp.station_parent = \"" + id_station +"\"");

				while (type.next()){
					String temp = type.getString(1);

					if(!temp.equals(tp)){
						tp=temp;
						String type1="";
						if(tp.equals("0")){
							type1="tram";
						}else if(tp.equals("1")){
							type1="metro";
						}else if(tp.equals("2")){
							type1="rer";
						}else if(tp.equals("3")){
							type1="bus";
						}

						ResultSet id_corresp = stmt4.executeQuery("SELECT identifiant,reseau,x,y,nom FROM ratp_arrets WHERE nom LIKE  \"%"+nom+"%\"");
						while(id_corresp.next()){
							String id = id_corresp.getString(1);
							String type2 = id_corresp.getString(2);
							Double x= Double.parseDouble(id_corresp.getString(3));
							Double y= Double.parseDouble(id_corresp.getString(4));
							double critere = 10.0;


							double distance = 2*Math.asin(Math.sqrt((Math.sin((y-y1)/2))*(Math.sin((y-y1)/2)) + Math.cos(y)*Math.cos(y1)*(Math.sin((x-x1)/2))*(Math.sin((x-x1)/2))));
							double tempo = (distance*1000);

							if(tempo<critere){
								if(tempo<2.5){
									tempo = (double) 4;
								}else{
									tempo = Math.floor(tempo*2);
								}
								String temps = Double.toString(tempo);
								String resx = "melange";



								pstmt.setString(1,id_station);

								pstmt.setString(2,id);

								pstmt.setString(3,type1);

								pstmt.setString(4,type2);

								pstmt.setString(5,temps);

								pstmt.setString(6,resx);


								pstmt.executeUpdate();
							}
						}
					}
				}
			}


			ResultSet resltter = stmt2.executeQuery("SELECT id_arret,nom,long,lat FROM ter_stops WHERE type_ligne =\"1\" AND nom LIKE \"%paris%\"");

			while (resltter.next()){

				String id_station = resltter.getString(1);
				String nom_arret = resltter.getString(2);
				Double x1= Double.parseDouble(resltter.getString(3));
				Double y1= Double.parseDouble(resltter.getString(4));
				String type1 = "ter";

				String nom="";
				if(id_station.endsWith("87547000")){
					nom = "Austerlitz";
				}else if(id_station.endsWith("87686667")){
					nom = "Bercy";
				}else if(id_station.endsWith("87113001")){
					nom = "Gare de l'est";
				}else if(id_station.endsWith("87686006")){
					nom = "gare de Lyon";
				}else if(id_station.endsWith("87391102") || id_station.endsWith("87391003")){
					nom = "Montparnasse";
				}else if(id_station.endsWith("87271007")){
					nom = "Gare du nord";
				}else if(id_station.endsWith("87384008")){
					nom = "Lazare";
				}else{
					nom = nom_arret;
				}

				nom = nom.replaceAll("é","*");
				nom = nom.replaceAll("â","*");
				nom = nom.replaceAll("ç","*");
				nom = nom.replaceAll(" - ","%");
				nom = nom.replaceAll("-","%");
				nom = nom.replaceAll(" ","%");
				nom = nom.replaceAll("\\(","%");
				nom = nom.replaceAll("\\)","%");


				ResultSet id_corresp = stmt4.executeQuery("SELECT identifiant,reseau,x,y,nom FROM ratp_arrets WHERE nom LIKE  \"%"+nom+"%\"");
				while(id_corresp.next()){
					String id = id_corresp.getString(1);
					String type2 = id_corresp.getString(2);
					Double x= Double.parseDouble(id_corresp.getString(3));
					Double y= Double.parseDouble(id_corresp.getString(4));
					double critere = 10.0;

					double distance = 2*Math.asin(Math.sqrt((Math.sin((y-y1)/2))*(Math.sin((y-y1)/2)) + Math.cos(y)*Math.cos(y1)*(Math.sin((x-x1)/2))*(Math.sin((x-x1)/2))));
					double tempo = (distance*1000);

					if(tempo<critere){
						if(tempo<2.5){
							tempo = (double) 4;
						}else{
							tempo = Math.floor(tempo*2);
						}
						String temps = Double.toString(tempo);
						String resx = "melange";


						pstmt.setString(1,id_station);
						pstmt.setString(2,id);
						pstmt.setString(3,type1);
						pstmt.setString(4,type2);
						pstmt.setString(5,temps);
						pstmt.setString(6,resx);

						pstmt.executeUpdate();

					}
				}
			}			
			ResultSet reslttrain = stmt2.executeQuery("SELECT id_arret,nom,long,lat FROM intercite_stops WHERE type_ligne =\"1\"");

			while (reslttrain.next()){

				String id_station = reslttrain.getString(1);
				String nom_arret = reslttrain.getString(2);
				Double x1= Double.parseDouble(reslttrain.getString(3));
				Double y1= Double.parseDouble(reslttrain.getString(4));
				String type1 = "intercite";

				String nom="";
				if(id_station.endsWith("87547000")){
					nom = "Austerlitz";
				}else if(id_station.endsWith("87686667")){
					nom = "Bercy";
				}else if(id_station.endsWith("87113001")){
					nom = "Gare de l'est";
				}else if(id_station.endsWith("87686006")){
					nom = "gare de Lyon";
				}else if(id_station.endsWith("87391102") || id_station.endsWith("87391003")){
					nom = "Montparnasse";
				}else if(id_station.endsWith("87271007")){
					nom = "Gare du nord";
				}else if(id_station.endsWith("87384008")){
					nom = "Lazare";
				}else {
					nom=nom_arret;
				}

				nom = nom.replaceAll("é","*");
				nom = nom.replaceAll("â","*");
				nom = nom.replaceAll("ç","*");
				nom = nom.replaceAll(" - ","%");
				nom = nom.replaceAll("-","%");
				nom = nom.replaceAll(" ","%");
				nom = nom.replaceAll("\\(","%");
				nom = nom.replaceAll("\\)","%");


				ResultSet id_corresp = stmt4.executeQuery("SELECT identifiant,reseau,x,y,nom FROM ratp_arrets WHERE nom LIKE  \"%"+nom+"%\"");
				while(id_corresp.next()){
					String id = id_corresp.getString(1);
					String type2 = id_corresp.getString(2);
					Double x= Double.parseDouble(id_corresp.getString(3));
					Double y= Double.parseDouble(id_corresp.getString(4));
					double critere = 10.0;


					double distance = 2*Math.asin(Math.sqrt((Math.sin((y-y1)/2))*(Math.sin((y-y1)/2)) + Math.cos(y)*Math.cos(y1)*(Math.sin((x-x1)/2))*(Math.sin((x-x1)/2))));
					double tempo = (distance*1000);

					if(tempo<critere){
						if(tempo<2.5){
							tempo = (double) 4;
						}else{
							tempo = Math.floor(tempo*2);
						}
						String temps = Double.toString(tempo);
						String resx = "melange";



						pstmt.setString(1,id_station);
						pstmt.setString(2,id);
						pstmt.setString(3,type1);
						pstmt.setString(4,type2);
						pstmt.setString(5,temps);
						pstmt.setString(6,resx);

						pstmt.executeUpdate();
					}
				}
			}
		}

		/**
		 *  gestion des cas d'erreur
		 */

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

	}

}


