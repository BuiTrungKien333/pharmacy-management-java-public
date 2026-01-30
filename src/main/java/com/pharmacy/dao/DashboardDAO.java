package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;

public class DashboardDAO {

	private static final Logger log = LoggerFactory.getLogger(DashboardDAO.class);

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM");

	public int countDailyInvoices() {
		String sql = "select count(*) from tbl_hoa_don where ngay_lap >= cast(GETDATE() as date) and ngay_lap < DATEADD(DAY, 1, CAST(GETDATE() AS date))";
		int totalInvoices = 0;

		log.debug("[DAO] Executing SQL: {}", sql);

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next())
				totalInvoices = rs.getInt(1);

			return totalInvoices;

		} catch (SQLException e) {
			log.error("[DAO] Database error while counting daily invoices.", e);
			return 0;
		}
	}

	public double calculateDailyRevenue() {
		String sql = "select sum(tong_tien_sau_giam) as doanh_thu from tbl_hoa_don where ngay_lap >= cast(GETDATE() as date) and ngay_lap < DATEADD(DAY, 1, CAST(GETDATE() AS date))";
		double revenue = 0.0;
		log.debug("[DAO] Executing SQL: {}", sql);

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next())
				revenue = rs.getDouble(1);

			return revenue;

		} catch (SQLException e) {
			log.error("[DAO] Database error while calculating daily revenue.", e);
			return 0.0;
		}
	}

	public double calculateDailyProfit() {
		String sql = "select SUM((cthd.don_gia - lo.gia_nhap) * cthd.so_luong) from tbl_chi_tiet_hoa_don cthd inner join tbl_lo_thuoc lo on cthd.so_lo = lo.so_lo where cthd.created_at >= cast(GETDATE() as date) and cthd.created_at < DATEADD(DAY, 1, CAST(GETDATE() AS date))";

		double profit = 0.0;
		log.debug("[DAO] Executing SQL for daily profit calculation.");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next())
				profit = rs.getDouble(1);

			return profit;

		} catch (SQLException e) {
			log.error("[DAO] Database error while calculating daily profit.", e);
			return 0.0;
		}
	}

	public int countDailyNewCustomers() {
		String sql = "SELECT COUNT(*) FROM tbl_khach_hang " + "WHERE created_at >= CAST(GETDATE() AS date) "
				+ "AND created_at < DATEADD(DAY, 1, CAST(GETDATE() AS date))";

		int newCustomers = 0;
		log.debug("[DAO] Executing SQL for daily new customer count.");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next())
				newCustomers = rs.getInt(1);

			return newCustomers;

		} catch (SQLException e) {
			log.error("[DAO] Database error while counting daily new customers.", e);
			return 0;
		}
	}

	public Map<String, Double> revenueLast7Days() {
		Map<String, Double> mp = new LinkedHashMap<>();
		String sql = "select cast(ngay_lap as date) as date_val, sum(tong_tien_sau_giam) as total from tbl_hoa_don where ngay_lap >= DATEADD(DAY, -6, CAST(GETDATE() AS date)) group by cast(ngay_lap as date) order by date_val asc";

		log.debug("[DAO] Executing SQL for last 7 days revenue chart.");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
			while (rs.next()) {
				Date date = rs.getDate("date_val");
				double value = rs.getDouble("total");
				String dateLabel = sdf.format(date);

				mp.put(dateLabel, value);
			}

		} catch (SQLException e) {
			log.error("[DAO] Database error while fetching last 7 days revenue.", e);
		}

		return mp;
	}

	public Map<String, Integer> countInvoicesLast5Days(String tableName) {
		Map<String, Integer> data = new LinkedHashMap<>();

		String sql = String.format("SELECT CAST(ngay_lap AS date) AS date_val, COUNT(id) AS total " + "FROM %s "
				+ "WHERE ngay_lap >= DATEADD(DAY, -4, CAST(GETDATE() AS date)) " + "GROUP BY CAST(ngay_lap AS date) "
				+ "ORDER BY date_val ASC", tableName);

		log.debug("[DAO] Executing SQL for {} count: {}", tableName, sql);

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Date date = rs.getDate("date_val");
				int total = rs.getInt("total");

				String dateLabel = SDF.format(date);
				data.put(dateLabel, total);
			}

		} catch (SQLException e) {
			log.error("[DAO] Database error while fetching last 5 days count for table: {}", tableName, e);
		}

		return data;
	}

}