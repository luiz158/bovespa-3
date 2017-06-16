package br.com.destrosoft.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Dados do pregão
 * 
 * @author destro
 */
@Entity
@Table(schema = "market", name = "market_res_d")
@IdClass(value = MarketDailyPK.class)
public class MarketDaily implements Serializable {

	private static final long serialVersionUID = -1854868867102647734L;

	@Id
	@Temporal(TemporalType.DATE)
	@Column(name = "date")
	private Date date;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "market_type_id")
	private int type;

	@Column(name = "price_open")
	private double priceOpen;

	@Column(name = "price_max")
	private double priceMax;

	@Column(name = "price_min")
	private double priceMin;

	@Column(name = "price_average")
	private double priceAverage;

	@Column(name = "price_close")
	private double priceClose;

	@Column(name = "total_trades")
	private int totalTrades;

	@Column(name = "total_quantity_stock_traded")
	private long totalQuantityStockTraded;

	@Column(name = "total_volume")
	private BigDecimal totalVolume;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getPriceOpen() {
		return priceOpen;
	}

	public void setPriceOpen(double priceOpen) {
		this.priceOpen = priceOpen;
	}

	public double getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(double priceMax) {
		this.priceMax = priceMax;
	}

	public double getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(double priceMin) {
		this.priceMin = priceMin;
	}

	public double getPriceAverage() {
		return priceAverage;
	}

	public void setPriceAverage(double priceAverage) {
		this.priceAverage = priceAverage;
	}

	public double getPriceClose() {
		return priceClose;
	}

	public void setPriceClose(double priceClose) {
		this.priceClose = priceClose;
	}

	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}

	public int getTotalTrades() {
		return totalTrades;
	}

	public void setTotalTrades(int totalTrades) {
		this.totalTrades = totalTrades;
	}

	public long getTotalQuantityStockTraded() {
		return totalQuantityStockTraded;
	}

	public void setTotalQuantityStockTraded(long totalQuantityStockTraded) {
		this.totalQuantityStockTraded = totalQuantityStockTraded;
	}

}
