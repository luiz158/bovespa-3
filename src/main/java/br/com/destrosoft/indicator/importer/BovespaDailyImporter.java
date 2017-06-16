package br.com.destrosoft.indicator.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import br.com.destrosoft.entity.MarketDaily;

public class BovespaDailyImporter {

	private static final Logger LOG = Logger.getLogger(BovespaDailyImporter.class);

	private static String BDI = "http://bvmf.bmfbovespa.com.br/InstDados/SerHist/COTAHIST_D";

	private EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("market");

	public static void main(String[] args) {

		try {
			LOG.info("Begin");

			BovespaDailyImporter importer = new BovespaDailyImporter();

			importer.updateDaily();

			LOG.info("done");
			System.exit(0);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			System.exit(-1);
		}
	}

	/**
	 * Atualização diária
	 * 
	 * @throws IOException
	 * 
	 * @throws ParseException
	 */
	private void updateDaily() throws ImporterException, IOException {
		LOG.info("[DAILY] Updating...");

		// Efetua leitura da última data carregada
		BufferedReader bufferedReader = new BufferedReader(new FileReader("lastDate"));
		String lastDate = bufferedReader.readLine();
		bufferedReader.close();

		// Por padrão sempre pega os últimos 365 pregões, caso lastDate esteja
		// vazio
		Date startDate = new Date(System.currentTimeMillis() - (365L * 24L * 60L * 60L * 1000L));

		if (lastDate != null) {
			// Última data mais o dia do pregão atual
			startDate = new Date(Long.valueOf(String.valueOf(lastDate)) + (1L * 24L * 60L * 60L * 1000L));
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis() + (1L * 24L * 60L * 60L * 1000L));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date now = cal.getTime();

		while (startDate.before(now)) {

			this.updateBovespa(startDate);

			startDate = new Date(startDate.getTime() + 24L * 60L * 60L * 1000L);
		}

		// Salva ultima data carregada
		FileWriter fw = new FileWriter("lastDate");
		fw.write(String.valueOf(startDate.getTime()));
		fw.close();

		LOG.info("[DAILY] done");
	}

	/**
	 * Atualiza dados de boletim diário da Bovespa na base de dados
	 * 
	 * @param date
	 * 
	 * @throws Exception
	 */
	private void updateBovespa(Date date) throws ImporterException {

		try {

			HttpClient client = new HttpClient();

			SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");

			String fileName = BDI + sf.format(date) + ".zip";

			GetMethod get = new GetMethod(fileName);

			LOG.info("[BDI] date: " + date + ", fileName: " + fileName);

			int r = client.executeMethod(get);

			if (r != 200) {
				LOG.warn("[BDI] Problemas em baixar arquivo BDI, date: " + date + ", fileName: " + fileName);
				return;
			}

			InputStream in = get.getResponseBodyAsStream();

			ZipInputStream zIn = new ZipInputStream(in);

			// Posiciona ponteiro
			zIn.getNextEntry();

			BufferedReader reader = new BufferedReader(new InputStreamReader(zIn));

			String line = null;

			EntityManager em = FACTORY.createEntityManager();

			try {
				em.getTransaction().begin();

				while ((line = reader.readLine()) != null) {

					if (line.startsWith("01")) {
						MarketDaily md = new MarketDaily();
						md.setDate(date);

						md.setCode(line.substring(12, 23).trim());

						md.setType(Integer.valueOf(line.substring(24, 27).trim()));

						md.setPriceOpen(Double.parseDouble(line.substring(56, 69)) / 100.0);
						md.setPriceMax(Double.parseDouble(line.substring(69, 82)) / 100.0);
						md.setPriceMin(Double.parseDouble(line.substring(82, 95)) / 100.0);
						md.setPriceAverage(Double.parseDouble(line.substring(95, 108)) / 100.0);
						md.setPriceClose(Double.parseDouble(line.substring(108, 121)) / 100.0);

						md.setTotalTrades(Integer.parseInt(line.substring(147, 152)));
						md.setTotalQuantityStockTraded(Long.parseLong(line.substring(152, 170)));
						md.setTotalVolume(new BigDecimal(Double.parseDouble(line.substring(170, 188)) / 100.0));

						em.merge(md);
					}
				}

				em.getTransaction().commit();
			} catch (Exception e) {
				if (em != null && em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
				LOG.error(e.getMessage(), e);
			} finally {
				if (em != null && em.isOpen()) {
					em.close();
				}
			}
		} catch (IOException e) {
			throw new ImporterException(e.getMessage(), e);
		}
	}

}
