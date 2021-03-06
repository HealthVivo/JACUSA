package jacusa.estimate.coverage;

import jacusa.phred2prob.Phred2Prob;
import jacusa.pileup.Pileup;

import java.util.Arrays;

public class CoverageEstimateParameters extends AbstractCoverageEstimateParameters {

	private final double initialAlphaNull;

	public CoverageEstimateParameters(double initialAlphaNull, Phred2Prob phred2Prob) {
		super("coverage", "Coverage bases", phred2Prob);
		this.initialAlphaNull = initialAlphaNull;
	}
	
	@Override
	public double[] estimateAlpha(int[] baseIs, Pileup[] pileups) {
		int coverage = 0;
		for (Pileup pileup : pileups) {
			coverage += pileup.getCoverage();
		}
		return estimateAlpha(baseIs, pileups, coverage);
	}

	public double[] estimateAlpha(int[] baseIs, Pileup[] pileups, int coverage) {
		final double[] alpha = new double[baseIs.length];
		if (initialAlphaNull > 0.0) {
			Arrays.fill(alpha, initialAlphaNull / (double)baseIs.length);
		} else {
			Arrays.fill(alpha, 0.0);
		}

		for (Pileup pileup : pileups) {
			double[] probVector = phred2Prob.colMeanProb(baseIs, pileup);
			// INFO: coverage and pileup.getCoverage() may be different.
			// e.g.: when replicates are available...
			// (double)coverage * 
			for(int baseI = 0; baseI < baseIs.length; ++baseI) {
				alpha[baseI] += probVector[baseI] * (double)coverage;
			}
		}

		return alpha;
	}
	
	public double[][] probabilityMatrix(int[] baseIs, Pileup[] pileups) {
		final double[][] probs = new double[pileups.length][baseIs.length];

		for(int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			// sum the probabilities giving alpha 
			probs[pileupI] = phred2Prob.colMeanProb(baseIs, pileups[pileupI]);
		}

		return probs;		
	};

	public int getMeanCoverage(Pileup[] pileups) {
		if (pileups.length == 1) { 
			return pileups[0].getCoverage();
		}

		int coverage = 0;
		for (Pileup pileup : pileups) {
			coverage += pileup.getCoverage();
		}
		return (int)Math.round((double)coverage / (double)pileups.length);
	}
	
}