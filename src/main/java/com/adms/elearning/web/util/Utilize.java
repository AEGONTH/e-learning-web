package com.adms.elearning.web.util;

import java.util.Random;

public class Utilize {

	public static char[] getAlphabetAtoZ() {
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		return alphabet;
	}

	public static String getRandomCitizenID() {
		char[] citizen = new char[13];
		int sum = 0;
		for(int i = 0; i < 12; i++) {
			citizen[i] = i == 0 ? (char) randomInt(1, 8) : (char) randomInt(0, 9);
			sum += (Character.getNumericValue(citizen[i]) * (13 - i));
		}
		citizen[12] = (char) getChecksum(sum);
		return String.valueOf(citizen);
	}

	private static int getChecksum(int sumOfCitizen) {
		return (11 - (sumOfCitizen % 11)) % 10;
	}

	public static boolean isCitizenIdValid(String citizenId) {
		boolean flag = false;
		int sum = 0;

		if(citizenId.length() == 13) {
			char[] chars = citizenId.toCharArray();
			for(int i = 0; i < 12; i++) sum += (Character.getNumericValue(chars[i]) * (13 - i));
			flag = ((11 - (sum % 11)) % 10) == Character.getNumericValue(chars[12]);
		}
		return flag;
	}

	public static int randomInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
}
