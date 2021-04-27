package it.polito.tdp.poweroutages.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutage;

public class PowerOutageDAO {
	
	public List<Nerc> getNercList() {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	
	public List<PowerOutage> getPowerOutages(int nercId, int nOre){
		nOre *= 3600;
		final String sql = "SELECT * "
				+ "FROM poweroutages "
				+ "WHERE to_seconds(date_event_finished) - to_seconds(date_event_began) <= ? "
				+ "AND nerc_id = ? "
				+ "ORDER BY YEAR(date_event_began) ASC";
		List<PowerOutage> outages = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nOre);
			st.setInt(2, nercId);
			
			ResultSet res = st.executeQuery();

			while (res.next()) {
				LocalDateTime db = res.getTimestamp("date_event_began").toLocalDateTime();
				LocalDateTime df = res.getTimestamp("date_event_finished").toLocalDateTime();
				PowerOutage po = new PowerOutage(res.getInt("Id"), res.getInt("event_type_id"), res.getInt("tag_id"), 
						res.getInt("area_id"), res.getInt("nerc_id"), res.getInt("responsible_id"), res.getInt("customers_affected"), 
						db, df, res.getInt("demand_loss"));
				outages.add(po);
			}
			
			conn.close();
		} 
		catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return outages;
	}
}
