package com.pharmacy.bus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.DashboardDAO;

public class DashboardBUS {

	private static final Logger log = LoggerFactory.getLogger(DashboardBUS.class);

	private final DashboardDAO dashboardDAO = new DashboardDAO();

	public int getCountDailyInvoices() {
		log.debug("[BUS] Requesting daily invoice count from DAO...");
		int count = dashboardDAO.countDailyInvoices();
		log.info("[BUS] Dashboard - Daily Invoices: {}", count);
		return count;
	}

	public double getCalculateDailyRevenue() {
		log.debug("[BUS] Requesting daily revenue calculation from DAO...");
		double revenue = dashboardDAO.calculateDailyRevenue();
		log.info("[BUS] Dashboard - Daily Revenue: {}", revenue);
		return revenue;
	}

	public double getCalculateDailyProfit() {
		log.debug("[BUS] Requesting daily profit calculation from DAO...");
		double profit = dashboardDAO.calculateDailyProfit();
		log.info("[BUS] Dashboard - Daily Profit: {}", profit);
		return profit;
	}

	public int getCountDailyNewCustomers() {
		log.debug("[BUS] Requesting daily new customer count from DAO...");
		int count = dashboardDAO.countDailyNewCustomers();
		log.info("[BUS] Dashboard - New Customers: {}", count);
		return count;
	}

	public Map<String, Double> getRevenueLast7Days() {
		log.debug("[BUS] Requesting chart data (Last 7 Days Revenue)...");

		Map<String, Double> daoMap = dashboardDAO.revenueLast7Days();

		Map<String, Double> finalRevenueMap = new LinkedHashMap<>();

		LocalDate today = LocalDate.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

		for (int i = 6; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			String label = date.format(fmt);
			Double revenue = daoMap.getOrDefault(label, 0.0);

			finalRevenueMap.put(label, revenue);
		}

		return finalRevenueMap;
	}

	public Map<String, Integer> getSalesInvoiceCountLast5Days() {
		Map<String, Integer> mp = dashboardDAO.countInvoicesLast5Days("tbl_hoa_don");

		Map<String, Integer> finalMap = new LinkedHashMap<>();

		LocalDate today = LocalDate.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

		for (int i = 4; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			String label = date.format(fmt);
			Integer total = mp.getOrDefault(label, 0);

			finalMap.put(label, total);
		}

		return finalMap;
	}

	public Map<String, Integer> getReturnInvoiceCountLast5Days() {
		Map<String, Integer> mp = dashboardDAO.countInvoicesLast5Days("tbl_hoa_don_tra");

		Map<String, Integer> finalMap = new LinkedHashMap<>();

		LocalDate today = LocalDate.now();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

		for (int i = 4; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			String label = date.format(fmt);
			Integer total = mp.getOrDefault(label, 0);

			finalMap.put(label, total);
		}

		return finalMap;
	}
}