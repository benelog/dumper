package net.benelog.dumper;

import java.util.Random;

public class RandomNumberGenernator {
	Random random = new Random();

	public int pick(int fromNumber, int toNumber) {
		int picked = (random.nextInt(toNumber - fromNumber)) + fromNumber;
		return picked;
	}

}
