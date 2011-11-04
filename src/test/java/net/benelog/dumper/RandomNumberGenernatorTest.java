package net.benelog.dumper;

import static org.junit.Assert.assertTrue;

import net.benelog.dumper.RandomNumberGenernator;

import org.junit.Test;

public class RandomNumberGenernatorTest {
	@Test
	public void numberShouldBeGeneratedByRandom(){
		int fromNumber = 10000;
		int toNumber = 20000;
		RandomNumberGenernator gen = new RandomNumberGenernator();
		
		for(int i=0;i<30000 ; i++){
			int num = gen.pick(fromNumber, toNumber);
			assertTrue(num>= fromNumber);
			assertTrue(num<= toNumber);
		}
	}

}
