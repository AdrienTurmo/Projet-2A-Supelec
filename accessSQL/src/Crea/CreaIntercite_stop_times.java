package Crea;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CreaIntercite_stop_times {

	public static void crea(String line,PreparedStatement pstmt,BufferedReader br) throws SQLException, IOException {

		line = br.readLine();
		while (line != null ) {
			String line2= new String(line.getBytes(),"UTF-8");
			Pattern pattern= Pattern.compile("[,]");
			String[] result=pattern.split(line2);		
			
			
			String trip_id= result[0];
			String arrival_time= result[1];
			String departure_time= result[2];
			String stop_id= result[3];
			String stop_sequence= result[4];


			pstmt.setString(1,trip_id);
			pstmt.setString(2,arrival_time);
			pstmt.setString(3,departure_time);
			pstmt.setString(4,stop_id);
			pstmt.setString(5,stop_sequence);

			
			pstmt.executeUpdate();
			line = br.readLine();

		}
	}
	
	
	
}
