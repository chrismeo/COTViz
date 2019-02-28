import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class COTupdater {
	private File folder_futures;
	private File[] list_of_files;
	private String folder = "";
	private int last_year;
	private String datehead;
	private String[] futureslist;
	private HashMap<String, String> hash = new HashMap<String, String>();
//TEST
	public COTupdater() {
		makefutureslist();
		makehash();
	}

	public void update() {
		checklastdate();
		downloadCOT();
		write_future_files();
	}

	private void checklastdate() {
		File file = new File("head");
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				datehead = br.readLine();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void write_future_files() {
		File dir = new File("tables");
		if (!dir.exists())
			dir.mkdir();
		folder = dir.getPath();

		for (int k = 0; k < futureslist.length; k++) {
			String name = futureslist[k];
			String path = "";
			String OS = System.getProperty("os.name");
			if (OS.startsWith("Windows"))
				path = folder + "\\" + name;
			else
				path = folder + "/" + name;

			File f = new File(path);
			try {
				// PrintWriter pw = new PrintWriter(f);
				FileWriter tablefw = new FileWriter(f, true);
				for (int l = list_of_files.length - 1; l >= 0; l--) {
					InputStream fs = new FileInputStream(list_of_files[l]);
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					HSSFSheet sheet = wb.getSheetAt(0);

					for (Row row : sheet) {
						Cell cell0 = row.getCell(0);
						String celltext0 = cell0.getStringCellValue();
						String line = "";

						if (celltext0.contains(hash.get(name)))// (celltext0.equals(name))
						{
							// Datum
							Cell cell2 = row.getCell(2);
							Date date = new Date();
							date = cell2.getDateCellValue();

							DateFormat df = new SimpleDateFormat("MM/yy");
							String datestring = df.format(date);
							if ((datehead != null) && (datestring.compareTo(datehead) == 0)) {
								break;
							}
							line += datestring;
							line += " ";

							// Commercials
							Cell cell11 = row.getCell(11);
							Cell cell12 = row.getCell(12);
							double result = cell11.getNumericCellValue() - cell12.getNumericCellValue();
							int commercials = (int) result;
							line += String.valueOf(commercials);
							line += " ";

							// Large Traders
							Cell cell8 = row.getCell(8);
							Cell cell9 = row.getCell(9);
							double result2 = cell8.getNumericCellValue() - cell9.getNumericCellValue();
							int largetraders = (int) result2;
							line += String.valueOf(largetraders);
							line += " ";

							// Small Traders
							Cell cell15 = row.getCell(15);
							Cell cell16 = row.getCell(16);
							double result3 = cell15.getNumericCellValue() - cell16.getNumericCellValue();
							int smalltraders = (int) result3;
							line += String.valueOf(smalltraders);

							// pw.println(line);
							tablefw.write(line + "\n");
						}
					}

					wb.close();
					fs.close();
				}

				// pw.close();
				tablefw.close();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}

		// latest date
		InputStream isdate;
		try {
			int n = list_of_files.length;
			isdate = new FileInputStream(list_of_files[n - 1]);
			HSSFWorkbook wbdate = new HSSFWorkbook(isdate);
			HSSFSheet sheetdate = wbdate.getSheetAt(0);

			Cell cell2 = sheetdate.getRow(1).getCell(2);
			Date date = new Date();
			date = cell2.getDateCellValue();
			File filedate = new File("head");

			if (filedate.exists()) {
				filedate.delete();
				filedate = new File("head");
			}

			DateFormat df = new SimpleDateFormat("dd/MM/yy");
			String datestring = df.format(date);
			BufferedWriter out = new BufferedWriter(new FileWriter(filedate, true));
			out.write(datestring);
			out.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// delete folder unzip/
		File fileunzip = new File("unzip/");
		File[] f = fileunzip.listFiles();
		for (File s : f) {
			s.delete();
		}

		fileunzip.delete();
	}

	private void downloadCOT() {
		int year = Calendar.getInstance().get(Calendar.YEAR);

		File dirtables = new File("tables");

		if (dirtables.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(dirtables.listFiles()[0]));
				String s = br.readLine();
				StringTokenizer st = new StringTokenizer(s);
				int yy = Integer.valueOf(st.nextToken().substring(3, 5));
				last_year = 2000 + yy;
			}

			catch (IOException e2) {
				e2.printStackTrace();
			}
		}

		if (!dirtables.exists())
			last_year = 1986;

		File dir = new File("cot-excel");
		dir.mkdir();
		for (int i = last_year; i <= 2003; i++) {
			try (BufferedInputStream inputStream = new BufferedInputStream(
					new URL("https://www.cftc.gov/sites/default/files/files/dea/history/deafut_xls_" + i + ".zip")
							.openStream());
					FileOutputStream fileOS = new FileOutputStream("cot-excel/" + i + "dea_fut_xls_.zip")) {
				byte data[] = new byte[1024];
				int byteContent;
				while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
					fileOS.write(data, 0, byteContent);
				}
			}

			catch (IOException e) {
			}
		}

		for (int i = last_year; i <= year; i++) {
			try (BufferedInputStream inputStream = new BufferedInputStream(
					new URL("https://www.cftc.gov/sites/default/files/files/dea/history/dea_fut_xls_" + i + ".zip")
							.openStream());
					FileOutputStream fileOS = new FileOutputStream("cot-excel/" + i + "dea_fut_xls_.zip")) {
				byte data[] = new byte[1024];
				int byteContent;
				while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
					fileOS.write(data, 0, byteContent);
				}
			}

			catch (IOException e) {
			}
		}

		unzipCOT();
		folder_futures = new File("unzip");
		list_of_files = folder_futures.listFiles();
		// File test = new File("futures");
		// makefutureslist();

		// if (!test.exists()) write_futurenames_gui();
	}

	private void unzipCOT() {
		File dir = new File("cot-excel");
		File[] zipfiles = dir.listFiles();
		String destDir = "unzip/";
		File dir1 = new File(destDir);
		if (!dir1.exists())
			dir1.mkdirs();

		try {
			for (int i = 0; i < zipfiles.length; i++) {
				File zipfile = zipfiles[i];
				String prefix = zipfile.getName().substring(0, 4);
				String zipFilePath = zipfile.getAbsolutePath();
				unzip(zipFilePath, destDir, prefix);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		for (File s : zipfiles) {
			s.delete();
		}

		dir.delete();
	}

	private void unzip(String zipFilePath, String destDir, String prefix) throws IOException {
		FileInputStream fis;
		byte[] buffer = new byte[1024];
		fis = new FileInputStream(zipFilePath);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(destDir + File.separator + prefix + fileName);
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;

			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();
			zis.closeEntry();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		fis.close();
	}

	/*
	 * private void write_futurenames_gui() { File test = new File("futures"); if
	 * (!test.exists()) { try { PrintWriter pw = new PrintWriter("futures"); for
	 * (int i = 0; i < futures_allowed.size(); i++) {
	 * pw.println(futures_allowed.get(i)); }
	 * 
	 * pw.close(); }
	 * 
	 * catch (FileNotFoundException e) { e.printStackTrace(); } } }
	 */
/*
	public HashMap<String,String> getFuturesHash() {
		return hash;
	}
	*/
	public String[] getFuturesList() {
		return futureslist;
	}

	private void makehash() {
		hash.put("LEANHOGS", "LEAN HOGS - CHICAGO MERCANTILE EXCHANGE");
		hash.put("FEEDERCATTLE", "FEEDER CATTLE - CHICAGO MERCANTILE EXCHANGE");
		hash.put("LIVECATTLE", "LIVE CATTLE - CHICAGO MERCANTILE EXCHANGE");
		hash.put("LUMBER", "RANDOM LENGTH LUMBER - CHICAGO MERCANTILE EXCHANGE");
		hash.put("SUGARNo11", "SUGAR NO. 11 - ICE FUTURES U.S.");
		hash.put("COFFEE", "COFFEE C - ICE FUTURES U.S.");
		hash.put("ORANGEJUICE", "FRZN CONCENTRATED ORANGE JUICE - ICE FUTURES U.S.");
		hash.put("COTTON", "COTTON NO. 2 - ICE FUTURES U.S.");
		hash.put("COCOA", "COCOA - ICE FUTURES U.S.");
		hash.put("SOYBEANOIL", "SOYBEAN OIL - CHICAGO BOARD OF TRADE");
		hash.put("SOYBEANMEAL", "SOYBEAN MEAL - CHICAGO BOARD OF TRADE");
		hash.put("SOYBEANS", "SOYBEANS - CHICAGO BOARD OF TRADE");
		hash.put("OATS", "OATS - CHICAGO BOARD OF TRADE");
		hash.put("RICE", "ROUGH RICE - CHICAGO BOARD OF TRADE");
		hash.put("WHEAT", "WHEAT-SRW - CHICAGO BOARD OF TRADE");
		hash.put("CORN", "CORN - CHICAGO BOARD OF TRADE");
		hash.put("ETHANOL", "CBT ETHANOL - CHICAGO BOARD OF TRADE");
		hash.put("NATURALGAS", "NATURAL GAS - NEW YORK MERCANTILE EXCHANGE");
		hash.put("HEATINGOIL", "#2 HEATING OIL");
		hash.put("GASOLINE", "GASOLINE BLENDSTOCK (RBOB) - NEW YORK MERCANTILE EXCHANGE");
		hash.put("WTI", "CRUDE OIL, LIGHT SWEET - NEW YORK MERCANTILE EXCHANGE");
		hash.put("COPPER", "COPPER-GRADE #1 - COMMODITY EXCHANGE INC.");
		hash.put("PALLADIUM", "PALLADIUM - NEW YORK MERCANTILE EXCHANGE");
		hash.put("GOLD", "GOLD - COMMODITY EXCHANGE INC.");
		hash.put("SILVER", "SILVER - COMMODITY EXCHANGE INC.");
		hash.put("PLATINUM", "PLATINUM - NEW YORK MERCANTILE EXCHANGE");
		hash.put("S&P", "S&P 500 Consolidated - CHICAGO MERCANTILE EXCHANGE");
		hash.put("DJIA", "DJIA Consolidated - CHICAGO BOARD OF TRADE");
		hash.put("NASDAQ", "NASDAQ-100 Consolidated - CHICAGO MERCANTILE EXCHANGE");
		hash.put("RUSSELL2000MINI", "RUSSELL 2000 MINI INDEX FUTURE - ICE FUTURES U.S.");
		hash.put("NIKKEI", "NIKKEI STOCK AVERAGE - CHICAGO MERCANTILE EXCHANGE");
		hash.put("USTREASURYBONDS", "U.S. TREASURY BONDS - CHICAGO BOARD OF TRADE");
		hash.put("2YEARUSTREASURYNOTES", "2-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
		hash.put("5YEARUSTREASURYNOTES", "5-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
		hash.put("10YEARUSTREASURYNOTES", "10-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE");
		hash.put("30DAYFEDERALFUNDS", "30-DAY FEDERAL FUNDS - CHICAGO BOARD OF TRADE");
		hash.put("AUSTRALIANDOLLAR", "AUSTRALIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE");
		hash.put("BRAZILIANREAL", "BRAZILIAN REAL - CHICAGO MERCANTILE EXCHANGE");
		hash.put("BRITISHPOUNDSTERLING", "BRITISH POUND STERLING - CHICAGO MERCANTILE EXCHANGE");
		hash.put("EUROFX", "EURO FX - CHICAGO MERCANTILE EXCHANGE");
		hash.put("JAPANESEYEN", "JAPANESE YEN - CHICAGO MERCANTILE EXCHANGE");
		hash.put("CANADIANDOLLAR", "CANADIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE");
		hash.put("MEXICANPESO", "MEXICAN PESO - CHICAGO MERCANTILE EXCHANGE");
		hash.put("NEWZEALANDDOLLAR", "NEW ZEALAND DOLLAR - CHICAGO MERCANTILE EXCHANGE");
		hash.put("RUSSIANRUBLE", "RUSSIAN RUBLE - CHICAGO MERCANTILE EXCHANGE");
		hash.put("BITCOIN", "BITCOIN-USD - CBOE FUTURES EXCHANGE");
		hash.put("SWISSFRANC", "SWISS FRANC - CHICAGO MERCANTILE EXCHANGE");
	}

	private void makefutureslist() {
		futureslist = new String[] { "LEANHOGS", "FEEDERCATTLE", "LIVECATTLE", "LUMBER", "SUGARNo11",
				"COFFEE", "ORANGEJUICE", "COTTON", "COCOA", "SOYBEANOIL", "SOYBEANMEAL", "SOYBEANS", "OATS", "RICE",
				"WHEAT", "CORN", "ETHANOL", "NATURALGAS", "HEATINGOIL", "GASOLINE", "WTI", "COPPER", "PALLADIUM",
				"GOLD", "SILVER", "PLATINUM", "S&P", "DJIA", "NASDAQ", "RUSSELL2000MINI", "NIKKEI", "USTREASURYBONDS",
				"2YEARUSTREASURYNOTES", "5YEARUSTREASURYNOTES", "10YEARUSTREASURYNOTES",
				"30DAYFEDERALFUNDS", "AUSTRALIANDOLLAR",
				"BRAZILIANREAL", "BRITISHPOUNDSTERLING",
				"EUROFX", "JAPANESEYEN",
				"CANADIANDOLLAR", "MEXICANPESO",
				"NEWZEALANDDOLLAR", "RUSSIANRUBLE",
				"BITCOIN", "SWISSFRANC" };

		/*
		 * futures_allowed = Stream.of("LEAN HOGS - CHICAGO MERCANTILE EXCHANGE",
		 * "FEEDER CATTLE - CHICAGO MERCANTILE EXCHANGE",
		 * "LIVE CATTLE - CHICAGO MERCANTILE EXCHANGE",
		 * "RANDOM LENGTH LUMBER - CHICAGO MERCANTILE EXCHANGE",
		 * "SUGAR NO. 11 - ICE FUTURES U.S.", "COFFEE C - ICE FUTURES U.S.",
		 * "FRZN CONCENTRATED ORANGE JUICE - ICE FUTURES U.S.",
		 * "COTTON NO. 2 - ICE FUTURES U.S.", "COCOA - ICE FUTURES U.S.",
		 * "SOYBEAN OIL - CHICAGO BOARD OF TRADE",
		 * "SOYBEAN MEAL - CHICAGO BOARD OF TRADE", "SOYBEANS - CHICAGO BOARD OF TRADE",
		 * "OATS - CHICAGO BOARD OF TRADE", "ROUGH RICE - CHICAGO BOARD OF TRADE",
		 * "WHEAT-SRW - CHICAGO BOARD OF TRADE", "CORN - CHICAGO BOARD OF TRADE",
		 * "CBT ETHANOL - CHICAGO BOARD OF TRADE",
		 * "NATURAL GAS - NEW YORK MERCANTILE EXCHANGE", "#2 HEATING OIL",
		 * "GASOLINE BLENDSTOCK (RBOB) - NEW YORK MERCANTILE EXCHANGE",
		 * "CRUDE OIL, LIGHT SWEET - NEW YORK MERCANTILE EXCHANGE",
		 * "COPPER-GRADE #1 - COMMODITY EXCHANGE INC.",
		 * "PALLADIUM - NEW YORK MERCANTILE EXCHANGE", "GOLD - COMMODITY EXCHANGE INC.",
		 * "SILVER - COMMODITY EXCHANGE INC.",
		 * "PLATINUM - NEW YORK MERCANTILE EXCHANGE",
		 * "S&P 500 Consolidated - CHICAGO MERCANTILE EXCHANGE",
		 * "DJIA Consolidated - CHICAGO BOARD OF TRADE",
		 * "NASDAQ-100 Consolidated - CHICAGO MERCANTILE EXCHANGE",
		 * "RUSSELL 2000 MINI INDEX FUTURE - ICE FUTURES U.S.",
		 * "NIKKEI STOCK AVERAGE - CHICAGO MERCANTILE EXCHANGE",
		 * "U.S. TREASURY BONDS - CHICAGO BOARD OF TRADE",
		 * "2-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE",
		 * "5-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE",
		 * "10-YEAR U.S. TREASURY NOTES - CHICAGO BOARD OF TRADE",
		 * "30-DAY FEDERAL FUNDS - CHICAGO BOARD OF TRADE",
		 * "AUSTRALIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE",
		 * "BRAZILIAN REAL - CHICAGO MERCANTILE EXCHANGE",
		 * "BRITISH POUND STERLING - CHICAGO MERCANTILE EXCHANGE",
		 * "EURO FX - CHICAGO MERCANTILE EXCHANGE",
		 * "JAPANESE YEN - CHICAGO MERCANTILE EXCHANGE",
		 * "CANADIAN DOLLAR - CHICAGO MERCANTILE EXCHANGE",
		 * "MEXICAN PESO - CHICAGO MERCANTILE EXCHANGE",
		 * "NEW ZEALAND DOLLAR - CHICAGO MERCANTILE EXCHANGE",
		 * "RUSSIAN RUBLE - CHICAGO MERCANTILE EXCHANGE",
		 * "BITCOIN-USD - CBOE FUTURES EXCHANGE",
		 * "SWISS FRANC - CHICAGO MERCANTILE EXCHANGE").collect(Collectors.toList());
		 */
	}
}
