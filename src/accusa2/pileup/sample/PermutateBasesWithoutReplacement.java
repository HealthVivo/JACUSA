package accusa2.pileup.sample;


import java.util.Random;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.DefaultParallelPileup;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.process.phred2prob.Phred2Prob;

public class PermutateBasesWithoutReplacement implements PermutateParallelPileup{

	private Random random = new Random(0);  

	public ParallelPileup permutate(ParallelPileup parallelPileup) {
		// containter for permutated parallel pileup
		final ParallelPileup permutated = new DefaultParallelPileup(parallelPileup.getNA(), parallelPileup.getNB());

		// 
		Pileup pooled = new DefaultPileup(parallelPileup.getPooledPileup());
		int[] alleles = pooled.getAlleles();
		int[] baseCount = pooled.getCounts().getBaseCount();
		int[] pooledCoverage = {pooled.getCoverage()};
		
		final DefaultPileup[] permutated1 = permuatePileup(pooledCoverage, alleles, baseCount, parallelPileup.getPileupsA());
		permutated.setPileupsA(permutated1);

		final DefaultPileup[] permutated2 = permuatePileup(pooledCoverage, alleles, baseCount, parallelPileup.getPileupsB());
		permutated.setPileupsB(permutated2);

		return permutated;
	}

	private DefaultPileup[] permuatePileup(int[] pooledCoverage, int[] alleles, int[] baseCount, Pileup[] pileups) {
		DefaultPileup[] permutated = new DefaultPileup[pileups.length];

		for(int j = 0; j < pileups.length; ++j) {
			Pileup pileup = pileups[j];
			permutated[j] = new DefaultPileup();
			
			int[] quals = collapseQualCount(pileup);
			for(int coverage = pileup.getCoverage(); coverage > 0; --coverage) {
				int base = sampleBase(pooledCoverage, alleles, baseCount);
				byte qual = sampleQual(coverage, quals);
				permutated[j].getCounts().addBase(base, qual);
				quals[qual]--;
			}
		}
		return permutated;
	}
	
	private int[] collapseQualCount(Pileup pileup) {
		int[] quals = new int[Phred2Prob.MAX_Q];
		for(int i = 0; i < Phred2Prob.MAX_Q; ++i) {
			for(int base : pileup.getAlleles()) {
				quals[i] += pileup.getCounts().getQualCount()[base][i];
			}
		}

		return quals;
	}
	
	private int sampleBase(int[] pooledCoverage, int[] alleles, int[] baseCount) {
		final int r = random.nextInt(pooledCoverage[0]);
		int count = 0;

		for(int base : alleles) {
			
			if(r >= count && r < count + baseCount[base]) {
				baseCount[base]--;
				pooledCoverage[0]--;
				
				if( baseCount[base] == 0 ) {
					int[] tmpAlleles = new int[alleles.length - 1];
					int j = 0;
					for(int i = 0; i < alleles.length; ++i) {
						if(alleles[i] != base) {
							tmpAlleles[j] = base;
							++j;
						}
					}
					alleles = tmpAlleles;
				}
				
				return base;
			}
			count += baseCount[base];
		}

		return -1;
	}
	
	private byte sampleQual(final int coverage, final int[] qualCount) {
		final int r = random.nextInt(coverage);
		int count = 0;
		for(int i = 0; i < qualCount.length; ++i) {
			if(r >= count && r < count + qualCount[i]) {
				return (byte)(i);
			}
			count += qualCount[i];
		}

		return -1;
	}

}
