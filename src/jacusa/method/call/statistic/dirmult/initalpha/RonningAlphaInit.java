package jacusa.method.call.statistic.dirmult.initalpha;

import java.util.Arrays;

import jacusa.pileup.Pileup;

public class RonningAlphaInit extends AbstractAlphaInit {

	private double minVariance;
	
	public RonningAlphaInit() {
		this(Math.pow(10, -5));
	}

	public RonningAlphaInit(double minVariance) {
		super("Roning", "See Ronning 1989");
		this.minVariance = minVariance;
	}

	void setMinVariance(double minVariacnce) {
		this.minVariance = minVariacnce;
	}
	
	@Override
	public double[] init(
			final int[] baseIs, 
			final Pileup[] pileups,
			final double[][] pileupMatrix, 
			final double[] pileupCoverages
			) {
		
		int n = pileupMatrix[0].length;
		
		final double[][] pileupProportionMatrix = new double[pileups.length][n];
		for (int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			for (int baseI : baseIs) {
				pileupProportionMatrix[pileupI][baseI] = pileupMatrix[pileupI][baseI] / pileupCoverages[pileupI];
			}
		}
		
		// init
		double[] alpha = new double[n];
		Arrays.fill(alpha, 0d);

		double[] mean = new double[n];
		Arrays.fill(mean, 0d);
		for (int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			for (int baseI : baseIs) {
				mean[baseI] += pileupProportionMatrix[pileupI][baseI];
			}
		}
		for (int baseI : baseIs) {
			mean[baseI] /= (double)(pileups.length);
		}
		
		double[] variance = new double[n];
		Arrays.fill(variance, 0d);
		for (int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			for (int baseI : baseIs) {
				variance[baseI] += Math.pow(pileupProportionMatrix[pileupI][baseI] - mean[baseI], 2d);
			}
		}

		// Ronning 1989 to set Method Of Moments
		double[] tmp = new double[baseIs.length];
		for (int i = 0; i < baseIs.length; ++i) {
			int baseI = baseIs[i];
			if (pileups.length - 1 == 0) {
				variance[baseI] = minVariance;
			} else {
				variance[baseI] /= (double)(pileups.length - 1);
				if (variance[baseI] < minVariance) {
					variance[baseI] = minVariance;
				}
			}

			double v = mean[baseI] * (1d - mean[baseI]) / variance[baseI] - 1d;
			if (v > 0) {
				tmp[i] = v;
			} else {
				tmp[i] = 0.0;
			}
		}
		
		Arrays.sort(tmp);
		// Ronning 1989 to set Method Of Moments
		double alphaNull = 0.0;
		int j = 0;
		for (int i = 0; i < baseIs.length - 1; ++i) {
			if (tmp[i] > 0.0) {
				if (alphaNull == 0.0) {
					alphaNull = tmp[i];
					++j;
				} else {
					alphaNull *= tmp[i];
					++j;
				}
			}
		}

		//alphaNull = Math.pow(alphaNull, 1d / (double)(baseIs.length - 1));
		alphaNull = Math.pow(alphaNull, 1d / (double)(j));
		for (int baseI : baseIs) {
			alpha[baseI] = mean[baseI] * alphaNull;
		}
		
		return alpha;
	}

	@Override
	public double[] init(
			final int[] baseIs, 
			final Pileup pileup,
			final double[] pileupVector,
			final double[] pileupErrorVector,
			final double pileupCoverage
			) {
		// FIXME
		int n = pileupVector.length;
		double[] alpha = new double[n];
		Arrays.fill(alpha, 0.0001);
		// TODO
		return null;
	}

}