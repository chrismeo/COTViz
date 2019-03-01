import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
	private String lastdate_string, currentdate_string;
	private String[] futureslist;
	private HashMap<String, String> hash = new HashMap<String, String>();

	/*
	 * public COTupdater() { makefutureslist(); makehash(); }
	 */

	
	public void init()
	{
		makefutureslist();
		makehash();
	}
	
	public void update() {
		readhead();
		downloadCOT();
		write_future_files();
		writehead();
	}

	private void writehead() {
		File headfile = new File("head");
		try {
			FileWriter fw = new FileWriter(headfile, false);			
			fw.write(currentdate_string);
			fw.close();
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readhead() {
		File file = new File("head");
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				lastdate_string = br.readLine();
				StringTokenizer st = new StringTokenizer(lastdate_string);
				int yy = Integer.valueOf(st.nextToken().substring(6, 8));
				if (yy >= 0)
					last_year = 2000 + yy;
				if (yy < 0)
					last_year = 1900 + yy;
	
				br.close();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (!file.exists()) {
			last_year = 1986;
		}
	}

	private void write_future_files() {
		File headfile = new File("head");
		
		File dir = new File("tables");
		if (!dir.exists()) {
			dir.mkdir();
		}

		folder = dir.getPath();

		for (int k = 0; k < futureslist.length; k++) {
			String name = futureslist[k];
			String path = "";
			String pathold = "";
			
			String OS = System.getProperty("os.name");
			if ((OS.startsWith("Windows")) && (!headfile.exists()))
				path = folder + "\\" + name;
			if ((OS.startsWith("Windows")) && (headfile.exists())) {
				path = folder + "\\" + name + "temp";
				pathold = folder + "\\" + name;
			}
			if ((!OS.startsWith("Windows")) && (!headfile.exists()))
				path = folder + "/" + name;
			if ((!OS.startsWith("Windows")) && (headfile.exists())) {
				path = folder + "/" + name + "temp";
				pathold = folder + "/" + name;
			}

			File f = new File(path);
			try {
				FileWriter tablefw = new FileWriter(f, true);
				for (int l = 0; l<list_of_files.length; l++) {
				//for (int l = list_of_files.length - 1; l >= 0; l--) {
					InputStream fs = new FileInputStream(list_of_files[l]);
					HSSFWorkbook wb = new HSSFWorkbook(fs);
					HSSFSheet sheet = wb.getSheetAt(0);

					int r = sheet.getLastRowNum();
					for(int j=r-1;j>=0;j--) {
						Row row = sheet.getRow(j);
					//for (Row row : sheet) {
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
							
							DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
							String datestring2 = df2.format(date);

							if ((lastdate_string != null) && (datestring2.compareTo(lastdate_string) == 0)) {
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

							tablefw.write(line + "\n");
							
							/*
							 * if(headfile.exists()) { String strold; File old = new File(pathold);
							 * BufferedReader br = new BufferedReader(new FileReader(pathold)); FileWriter
							 * fw = new FileWriter(f, true); while ((strold = br.readLine()) != null) {
							 * fw.write(strold + "\n"); }
							 * 
							 * br.close(); fw.close();
							 * 
							 * old.renameTo(f); }
							 */
						}
					}

					wb.close();
					fs.close();
				}

				tablefw.close();
	//			if(headfile.exists()) f.delete();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}

		//currentdate_string
		InputStream is;
		try {
			int l= list_of_files.length;
			is = new FileInputStream(list_of_files[l-1]);
			HSSFWorkbook hssfwb = new HSSFWorkbook(is);
			HSSFSheet sheet = hssfwb.getSheetAt(0);
			Row row = sheet.getRow(1);
			Cell cell2 = row.getCell(2);
			Date date = new Date();
			date = cell2.getDateCellValue();
			DateFormat df2 = new SimpleDateFormat("dd/MM/yy");
			currentdate_string = df2.format(date);		
		} 
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		futureslist = new String[] { "LEANHOGS", "FEEDERCATTLE", "LIVECATTLE", "LUMBER", "SUGARNo11", "COFFEE",
				"ORANGEJUICE", "COTTON", "COCOA", "SOYBEANOIL", "SOYBEANMEAL", "SOYBEANS", "OATS", "RICE", "WHEAT",
				"CORN", "ETHANOL", "NATURALGAS", "HEATINGOIL", "GASOLINE", "WTI", "COPPER", "PALLADIUM", "GOLD",
				"SILVER", "PLATINUM", "S&P", "DJIA", "NASDAQ", "RUSSELL2000MINI", "NIKKEI", "USTREASURYBONDS",
				"2YEARUSTREASURYNOTES", "5YEARUSTREASURYNOTES", "10YEARUSTREASURYNOTES", "30DAYFEDERALFUNDS",
				"AUSTRALIANDOLLAR", "BRAZILIANREAL", "BRITISHPOUNDSTERLING", "EUROFX", "JAPANESEYEN", "CANADIANDOLLAR",
				"MEXICANPESO", "NEWZEALANDDOLLAR", "RUSSIANRUBLE", "BITCOIN", "SWISSFRANC" };

	}
}
