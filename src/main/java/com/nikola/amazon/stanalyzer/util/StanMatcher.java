package com.nikola.amazon.stanalyzer.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import com.nikola.amazon.stanalyzer.entity.Stan;

public class StanMatcher {

	public static boolean same(Stan stan1, Stan stan2) {

		if (stan1.getLink() != null && stan1.getLink().equals(stan2.getLink())) {
			return true;
		} else
			return sameLokacijaNekretnine(stan1.getLokacija(),
					stan2.getLokacija(), ",")
					&& sameKvadratura(stan1.getKvadratura(),
							stan2.getKvadratura())
					&& sameSprat(stan1.getSprat(), stan2.getSprat())
					&& sameSobnost(stan1.getSobnost(), stan2.getSobnost());
	}

	private static boolean sameSobnost(String sobnost, String sobnost2) {
		if (sobnost == null && sobnost2 == null) {
			return true;
		} else if ((sobnost == null && sobnost2 != null)
				|| (sobnost != null && sobnost2 == null)) {
			return false;
		} else
			return sobnost.equals(sobnost2);
	}

	private static boolean sameKvadratura(Integer kvadratura1,
			Integer kvadratura2) {
		return kvadratura1.equals(kvadratura2);
	}

	private static boolean sameSprat(Integer sprat1, Integer sprat2) {
		return sprat1.equals(sprat2);
	}

	private static boolean sameLokacijaNekretnine(String lokacija1,
			String lokacija2, String delimiter) {
		if (lokacija1 == null || lokacija2 == null) {
			return false;
		}

		// zameni nasa slova sa ascii, prebaci u lowercase ("Vitanovačka" ==
		// "Vitanovacka" == "vitanovacka")
		lokacija1 = replaceSerbianAndLowercase(lokacija1);
		lokacija2 = replaceSerbianAndLowercase(lokacija2);

		String[] lokacija1Array = lokacija1.split(delimiter);
		String[] lokacija2Array = lokacija2.split(delimiter);

		// proveri svaku rec, da li se sadrzi u nizu lokacija
		Set<String> set1 = new LinkedHashSet<String>(
				Arrays.asList(lokacija1Array));
		Set<String> set2 = new LinkedHashSet<String>(
				Arrays.asList(lokacija2Array));

		Set<String> biggerSet = set1.size() > set2.size() ? set1 : set2;
		Set<String> smallerSet = set1.size() > set2.size() ? set2 : set1;

		return containsWithLevenshteinDistance(
				biggerSet.toArray(new String[biggerSet.size()]),
				smallerSet.toArray(new String[smallerSet.size()]), 1);
	}

	// nizovi su bez duplikata
	// ako veci niz sadrzi svaki element manjeg, sa pragom tolerancije, true
	private static boolean containsWithLevenshteinDistance(
			String[] biggerArray, String[] smallerArray, int distanceTolerance) {
		if (smallerArray.length == 0) {
			return true;
		}
		for (int i = 0; i < smallerArray.length; i++) {
			for (int j = 0; j < biggerArray.length; j++) {
				// ako smo nateteli na 2 dovoljno slicna stringa, ponovi proces
				// sa nizovima bez njih
				if (computeLevenshteinDistance(smallerArray[i], biggerArray[j]) <= distanceTolerance) {
					return containsWithLevenshteinDistance(
							ArrayUtils.remove(biggerArray, j),
							ArrayUtils.remove(smallerArray, i),
							distanceTolerance);
				}
			}
		}
		return false;
	}

	private static String replaceSerbianAndLowercase(String input) {
		return input.toLowerCase().replaceAll("š", "s").replaceAll("ć", "c")
				.replaceAll("č", "c").replaceAll("ž", "z").replaceAll("đ", "d");
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	private static int computeLevenshteinDistance(String str1, String str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}

	public static void main(String[] args) throws IOException {

		// String[] niz1 = new
		// String[]{"Beograd","Opština Vozdovac","Vitanovačka"};
		// String[] niz2 = new
		// String[]{"Beograd","Opstina Voždovac","Kumodraška","Vitanovacka"};
		//
		// Stan stan1 = new Stan();
		// Stan stan2 = new Stan();
		//
		// stan1.setSobnost("3+");
		// stan2.setSobnost("3+");
		//
		// stan1.setSajt(Sajt.NEKRETNINE_RS);
		// stan1.setSajt(Sajt.HALO_OGLASI);
		//
		// stan1.setKvadratura(52);
		// stan2.setKvadratura(52);
		//
		// stan1.setSprat(2);
		// stan2.setSprat(2);
		//
		// stan1.setLokacija("Beograd,Opština Zvezdara,Mirijevo,Mezijska ");
		// stan2.setLokacija("Beograd");
		//
		// System.out.println("isti su : "+same(stan1, stan2));
		//
		// Node rootNode = new
		// RSQLParser().parse("(genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000) or year=out=(2005,2006,2007,2008)");
		// String sqlQuery = rootNode.accept(new RSQLSQLVisitor("FILMOVI"));
		// System.out.println(sqlQuery);

		Path path = Paths.get("/home/dzoni/Downloads/warAndPeace.txt");
		// The stream hence file will also be closed here
		try (Stream<String> lines = Files.lines(path)) {
			Map<String, Long> collect = lines
					.flatMap(s -> Arrays.stream(s.split("\\s+")))
					.map(s -> s.toLowerCase().replaceAll("[^a-zA-Z]", ""))
					.collect(
							Collectors.groupingBy(o -> o, Collectors.counting()));
			collect = sortByValue(collect);
			collect.forEach((k,v)->System.out.println(k+"="+v));
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
